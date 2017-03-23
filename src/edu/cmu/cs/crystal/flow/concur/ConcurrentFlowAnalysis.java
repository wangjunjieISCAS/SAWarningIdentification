/**
 * Copyright (c) 2006, 2007, 2008 Marwan Abi-Antoun, Jonathan Aldrich, Nels E. Beckman,
 * Kevin Bierhoff, David Dickey, Ciera Jaspan, Thomas LaToza, Gabriel Zenarosa, and others.
 *
 * This file is part of Crystal.
 *
 * Crystal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Crystal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Crystal.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.cmu.cs.crystal.flow.concur;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.flow.FlowAnalysis;
import edu.cmu.cs.crystal.flow.IBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.flow.IFlowAnalysis;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.flow.ITransferFunction;
import edu.cmu.cs.crystal.internal.Crystal;
import edu.cmu.cs.crystal.util.Utilities;

/**
 * An implementation of IFlowAnalysis that analyzes methods in
 * concurrently. Creates a thread pool, and when analyzedPreemtively
 * is called (or the constructor that takes a list of methods) we
 * generate a future for each method declaration, force its analysis
 * in a brand new FlowAnalysis object, and store that in a mapping
 * from method declarations to IFlowAnalysis objects. Later, we can
 * delegate the standard flow analysis methods on the future, forcing its
 * completion.
 * 
 * This class is EXPERIMENTAL because certain shared classes, most notably
 * EclipseTAC, are not yet thought to be thread-safe. The biggest worry is
 * that there is no memory barrier between different analyses.
 * 
 * @author Nels Beckman
 *
 * @param <LE>
 */
public class ConcurrentFlowAnalysis<LE> 
implements IFlowAnalysis<LE> {
	
	/**
	 * Thread pool for executing concurrent flow analyses.
	 */
	private ExecutorService threadPool = 
		Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	
	private final Map<MethodDeclaration, Future<IFlowAnalysis<LE>>> analyzedMethods = 
		new HashMap<MethodDeclaration, Future<IFlowAnalysis<LE>>>();  
	
	private final Crystal myCrystal;
	private final ITransferFunction<LE> transferFunction;
	protected final IFlowAnalysis<LE> defaultFlowAnalysis;
	
	@SuppressWarnings("unused")
	private ConcurrentFlowAnalysis() {
		throw new RuntimeException("Bug: Invalid constuctor called");
	}
	
	/**
	 * Creates a new concurrent flow analysis and begins to analyze the given
	 * method bodies immediately. 
	 * @param transferFunction The transfer function defining the analysis.
	 * @param methods Starts analyzing these methods immediately in background threads.
	 * @param crystal
	 */
	public ConcurrentFlowAnalysis(ITransferFunction<LE> transferFunction,
			List<MethodDeclaration> methods,
			Crystal crystal) {
		
		this(transferFunction, crystal);
		analyzePreemitively(methods);
	}
	
	/**
	 * Creates a new concurrent flow analysis but does not analyze any methods
	 * immediately. 
	 * @see #analyzePreemitively
	 * @param transferFunction
	 * @param crystal
	 */
	public ConcurrentFlowAnalysis(ITransferFunction<LE> transferFunction,
			Crystal crystal) {
		this.myCrystal = crystal;
		this.transferFunction = transferFunction;
		this.defaultFlowAnalysis = this.createNewFlowAnalysis(transferFunction, crystal);
	}
	
	/**
	 * Perform dataflow analysis asynchronously on the list of methods given.
	 * This method should return relatively quickly, but analysis will be
	 * performed in another thread. If you have already called the constructor
	 * that takes a list of methods, calling this method is not required unless
	 * you have added new methods to analyze.
	 * 
	 * @param methods
	 */
	public void analyzePreemitively(List<MethodDeclaration> methods) {
		for(final MethodDeclaration decl: methods) {
			if( this.analyzedMethods.containsKey(decl) ) continue;
			
			Future<IFlowAnalysis<LE>> future =
				threadPool.submit(new Callable<IFlowAnalysis<LE>>() {
					public IFlowAnalysis<LE> call() throws Exception {
						IFlowAnalysis<LE> analyzer =
							ConcurrentFlowAnalysis.this.createNewFlowAnalysis(transferFunction, myCrystal);
						/*
						 * TODO: Ugly, we are forcing an analysis by calling getresults
						 * on the method declaration. This only works because of my
						 * inside knowledge of how FlowAnalysis works and is not inherent
						 * in the interface of the method.
						 */
						analyzer.getResultsAfter(decl);
						return analyzer;
					}

				});
			
			analyzedMethods.put(decl, future);
		}
	}
	
	@SuppressWarnings("unchecked")
	private IFlowAnalysis<LE> createNewFlowAnalysis(ITransferFunction<LE> transferFunction,
			Crystal crystal) {
		/*
		 * This will be called once for each method so that threads
		 * will not step on each other. In ConcurrentTACFlowAnalysis,
		 * hopefully we can just override this to return an TACFA.
		 */
		if( transferFunction instanceof IBranchSensitiveTransferFunction ) {
			return new FlowAnalysis<LE>((IBranchSensitiveTransferFunction<LE>)transferFunction);
		}
		else {
			return new FlowAnalysis<LE>(transferFunction);
		}
	}
	
	public IResult<LE> getLabeledResultsAfter(ASTNode node) {
		/*
		 * TODO: Need a find a way to get rid of this call...
		 */
		MethodDeclaration decl = Utilities.getMethodDeclaration(node);
		
		if( decl!= null && this.analyzedMethods.containsKey(decl) ) {
			try {
				return this.analyzedMethods.get(decl).get().getLabeledResultsAfter(node);
			} catch (Exception e) {/* Do nothing, let fall through to default. */ }
		}
		return addAsFakeFuture(decl,defaultFlowAnalysis).getLabeledResultsAfter(node);
	}

	public IResult<LE> getLabeledResultsBefore(ASTNode node) {
		MethodDeclaration decl = Utilities.getMethodDeclaration(node);
		
		if( decl!= null && this.analyzedMethods.containsKey(decl) ) {
			try {
				return this.analyzedMethods.get(decl).get().getLabeledResultsBefore(node);
			} catch (Exception e) {/* Do nothing, let fall through to default. */ }
		}
		return addAsFakeFuture(decl,defaultFlowAnalysis).getLabeledResultsBefore(node);
	}

	@Deprecated
	public LE getResultsAfter(ASTNode node) {
		return getResultsAfterCFG(node);
	}

	@Deprecated
	public LE getResultsBefore(ASTNode node) {
		return getResultsBeforeCFG(node);
	}

	public LE getResultsAfterCFG(ASTNode node) {
		MethodDeclaration decl = Utilities.getMethodDeclaration(node);
		
		if( decl!= null && this.analyzedMethods.containsKey(decl) ) {
			try {
				return this.analyzedMethods.get(decl).get().getResultsAfterCFG(node);
			} catch (Exception e) {/* Do nothing, let fall through to default. */ }
		}
		return addAsFakeFuture(decl,defaultFlowAnalysis).getResultsAfterCFG(node);
	}

	public LE getResultsBeforeCFG(ASTNode node) {
		MethodDeclaration decl = Utilities.getMethodDeclaration(node);
		
		if( decl!= null && this.analyzedMethods.containsKey(decl) ) {
			try {
				return this.analyzedMethods.get(decl).get().getResultsBeforeCFG(node);
			} catch (Exception e) {/* Do nothing, let fall through to default. */ }
		}
		return addAsFakeFuture(decl,defaultFlowAnalysis).getResultsBeforeCFG(node);
	}

	public LE getResultsBeforeAST(ASTNode node) {
		MethodDeclaration decl = Utilities.getMethodDeclaration(node);
		
		if( decl!= null && this.analyzedMethods.containsKey(decl) ) {
			try {
				return this.analyzedMethods.get(decl).get().getResultsBeforeAST(node);
			} catch (Exception e) {/* Do nothing, let fall through to default. */ }
		}
		return addAsFakeFuture(decl,defaultFlowAnalysis).getResultsBeforeAST(node);
	}

	public LE getResultsAfterAST(ASTNode node) {
		MethodDeclaration decl = Utilities.getMethodDeclaration(node);
		
		if( decl!= null && this.analyzedMethods.containsKey(decl) ) {
			try {
				return this.analyzedMethods.get(decl).get().getResultsAfterAST(node);
			} catch (Exception e) {/* Do nothing, let fall through to default. */ }
		}
		return addAsFakeFuture(decl,defaultFlowAnalysis).getResultsAfterAST(node);
	}


	/*
	 * In an effort provide further caching, this method will add the given
	 * method declaration to the analyzedMethods map immediately, without
	 * attempting to perform any computation asynchronously (hence the FAKE future).
	 * Can't decide if this is a good idea or not... 
	 */
	private IFlowAnalysis<LE> addAsFakeFuture(MethodDeclaration decl, 
			final IFlowAnalysis<LE> fa) {
		this.analyzedMethods.put(decl, new Future<IFlowAnalysis<LE>>() {
			public boolean cancel(boolean mayInterruptIfRunning) {
				return false;
			}
			public IFlowAnalysis<LE> get() throws InterruptedException, ExecutionException {
				return fa;
			}
			public IFlowAnalysis<LE> get(long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException,
					TimeoutException {
				return fa;
			}
			public boolean isCancelled() {
				return false;
			}
			public boolean isDone() {
				return true;
			}
		});
		return fa;
	}

	protected Map<MethodDeclaration, Future<IFlowAnalysis<LE>>> getAnalyzedMethods() {
		return analyzedMethods;
	}

	public LE getEndResults(MethodDeclaration d) {
		throw new UnsupportedOperationException("Unimplemented");
	}

	public IResult<LE> getLabeledEndResult(MethodDeclaration d) {
		throw new UnsupportedOperationException("Unimplemented");
	}

	public IResult<LE> getLabeledStartResult(MethodDeclaration d) {
		throw new UnsupportedOperationException("Unimplemented");
	}

	public LE getStartResults(MethodDeclaration d) {
		throw new UnsupportedOperationException("Unimplemented");
	}
}
