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
package edu.cmu.cs.crystal.tac;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.IAnalysisInput;
import edu.cmu.cs.crystal.flow.AnalysisDirection;
import edu.cmu.cs.crystal.flow.IBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.flow.IFlowAnalysisDefinition;
import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.flow.ITransferFunction;
import edu.cmu.cs.crystal.flow.LabeledSingleResult;
import edu.cmu.cs.crystal.flow.MotherFlowAnalysis;
import edu.cmu.cs.crystal.flow.SingleResult;
import edu.cmu.cs.crystal.tac.eclipse.CompilationUnitTACs;
import edu.cmu.cs.crystal.tac.eclipse.EclipseInstructionSequence;
import edu.cmu.cs.crystal.tac.eclipse.EclipseTAC;
import edu.cmu.cs.crystal.tac.model.SourceVariable;
import edu.cmu.cs.crystal.tac.model.SuperVariable;
import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.tac.model.TempVariable;
import edu.cmu.cs.crystal.tac.model.ThisVariable;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * This class implements flow analyses over 3-address code instructions
 * ({@link TACInstruction}).  To define a specific analysis, implement 
 * {@link ITACTransferFunction} for conventional or 
 * {@link ITACBranchSensitiveTransferFunction} for branch-sensitive flow analyses.
 * 
 * @author Kevin Bierhoff
 * @since Crystal 3.3
 * @param <LE>	The type that represents the analysis knowledge
 */
public class TACFlowAnalysis<LE> 
extends MotherFlowAnalysis<LE> implements ITACFlowAnalysis<LE> {
	
	/**
	 * Flow analysis definition for superclass that translates
	 * AST nodes into TAC instructions and invokes the client-
	 * defined transfer function on those instructions.
	 * Driver object is re-used across different methods to be analyzed.
	 */
	private final AbstractTACAnalysisDriver<?> driver;
	
	/**
	 * Creates a branch insensitive flow analysis object
	 * @param transferFunction
	 * @param analysisInput
	 */
	public TACFlowAnalysis(ITACTransferFunction<LE> transferFunction, IAnalysisInput analysisInput) {
		super();
		this.driver = new BranchInsensitiveTACAnalysisDriver(transferFunction, analysisInput.getComUnitTACs().unwrap());
		transferFunction.setAnalysisContext(driver);
	}

	/**
	 * Creates a branch insensitive flow analysis object.
	 * @param transferFunction
	 * @param eclipseTAC
	 */
	public TACFlowAnalysis(ITACTransferFunction<LE> transferFunction, CompilationUnitTACs eclipseTAC) {
		super();
		this.driver = new BranchInsensitiveTACAnalysisDriver(transferFunction, eclipseTAC);
		transferFunction.setAnalysisContext(driver);
	}

	/**
	 * Creates a branch sensitive flow analysis object.
	 * @param transferFunction
	 * @param analysisInput
	 */
	public TACFlowAnalysis(ITACBranchSensitiveTransferFunction<LE> transferFunction,
			IAnalysisInput analysisInput) {
		super();
		this.driver = new BranchSensitiveTACAnalysisDriver(transferFunction, analysisInput.getComUnitTACs().unwrap());
		transferFunction.setAnalysisContext(driver);
	}

	/**
	 * Creates a branch sensitive flow analysis object.
	 * @param transferFunction
	 * @param eclipseTAC
	 */
	public TACFlowAnalysis(ITACBranchSensitiveTransferFunction<LE> transferFunction,
			CompilationUnitTACs eclipseTAC) {
		super();
		this.driver = new BranchSensitiveTACAnalysisDriver(transferFunction, eclipseTAC);
		transferFunction.setAnalysisContext(driver);
	}

	public LE getResultsAfter(final TACInstruction instr) {
		ASTNode node = instr.getNode();
		// get regular results before looking up root instruction
		// to switch to surrounding method, if necessary 
		// (driver.tac could be null or outdated otherwise)
		final LE nodeResults = getResultsAfter(node);
		final TACInstruction rootInstr = this.driver.tac.instruction(node);
		if(rootInstr == instr) {
			// usual case: only one instruction for this node
			// return regular results
			return nodeResults;
		}
		else if(rootInstr instanceof EclipseInstructionSequence) {
			// compute intermediary results for instruction sequence
			EclipseInstructionSequence seq = (EclipseInstructionSequence) rootInstr;
			LE incoming =  getResultsOrNull(node, this.driver.tf.getAnalysisDirection() == AnalysisDirection.BACKWARD_ANALYSIS, false);
			if(incoming == null) 
				// no result available -> return bottom
				return nodeResults;
			else
				// derive result for needed instruction in sequence
				return mergeLabeledResult(this.driver.deriveResult(seq, incoming, instr, true), node);
		}
		else
			throw new UnsupportedOperationException("Can't determine results for instruction: " + instr);
	}

	public LE getResultsBefore(final TACInstruction instr) {
		ASTNode node = instr.getNode();
		// get regular results before looking up root instruction
		// to switch to surrounding method, if necessary 
		// (driver.tac could be null or outdated otherwise)
		final LE nodeResults = getResultsBefore(node);
		final TACInstruction rootInstr = this.driver.tac.instruction(node);
		if(rootInstr == instr) {
			// usual case: only one instruction for this node
			// return regular results
			return nodeResults;
		}
		else if(rootInstr instanceof EclipseInstructionSequence) {
			// compute intermediary results for instruction sequence
			EclipseInstructionSequence seq = (EclipseInstructionSequence) rootInstr;
			LE incoming = getResultsOrNull(node, this.driver.tf.getAnalysisDirection() == AnalysisDirection.BACKWARD_ANALYSIS, false);
			if(incoming == null) 
				// no result available -> return bottom
				return nodeResults;
			else
				// derive result for needed instruction in sequence
				return mergeLabeledResult(this.driver.deriveResult(seq, incoming, instr, false), node);
		}
		else
			throw new UnsupportedOperationException("Can't determine results for instruction: " + instr);
	}

	public IResult<LE> getLabeledResultsAfter(final TACInstruction instr) {
		ASTNode node = instr.getNode();
		// get regular results before looking up root instruction
		// to switch to surrounding method, if necessary 
		// (driver.tac could be null or outdated otherwise)
		final IResult<LE> nodeResults = getLabeledResultsAfter(node);
		final TACInstruction rootInstr = this.driver.tac.instruction(node);
		if(rootInstr == instr) {
			// usual case: only one instruction for this node
			// return regular results
			return nodeResults;
		}
		else if(rootInstr instanceof EclipseInstructionSequence) {
			// compute intermediary results for instruction sequence
			EclipseInstructionSequence seq = (EclipseInstructionSequence) rootInstr;
			LE incoming = getResultsOrNull(node, this.driver.tf.getAnalysisDirection() == AnalysisDirection.BACKWARD_ANALYSIS, false);
			if(incoming == null) 
				// no result available -> return bottom
				return nodeResults;
			else
				// derive result for needed instruction in sequence
				return this.driver.deriveResult(seq, incoming, instr, true);
		}
		else
			throw new UnsupportedOperationException("Can't determine results for instruction: " + instr);
	}

	public IResult<LE> getLabeledResultsBefore(final TACInstruction instr) {
		ASTNode node = instr.getNode();
		// get regular results before looking up root instruction
		// to switch to surrounding method, if necessary 
		// (driver.tac could be null or outdated otherwise)
		final IResult<LE> nodeResults = getLabeledResultsBefore(node);
		final TACInstruction rootInstr = this.driver.tac.instruction(node);
		if(rootInstr == instr) {
			// usual case: only one instruction for this node
			// return regular results
			return nodeResults;
		}
		else if(rootInstr instanceof EclipseInstructionSequence) {
			// compute intermediary results for instruction sequence
			EclipseInstructionSequence seq = (EclipseInstructionSequence) rootInstr;
			LE incoming = getResultsOrNull(node, this.driver.tf.getAnalysisDirection() == AnalysisDirection.BACKWARD_ANALYSIS, false);
			if(incoming == null) 
				// no result available -> return bottom
				return nodeResults;
			else
				// derive result for needed instruction in sequence
				return this.driver.deriveResult(seq, incoming, instr, false);
		}
		else
			throw new UnsupportedOperationException("Can't determine results for instruction: " + instr);
	}

	public ASTNode getNode(Variable x, TACInstruction instruction) {
		if(x instanceof TempVariable) {
			return ((TempVariable) x).getNode();
		}
		// TODO return better node for source, type, and keyword variables
		return instruction.getNode();
	}

	public Variable getVariable(ASTNode node) {
		if(hasResults(node) || findSurroundingMethod(node) == getCurrentMethod())
			return driver.tac.variable(node);
		throw new IllegalArgumentException("Not currently analyzing method surrounding node: " + node);
	}
	
	public ThisVariable getThisVariable(MethodDeclaration methodDecl) {
		if(methodDecl == null || methodDecl != getCurrentMethod())
			throw new IllegalArgumentException("Not currently analyzing method: " + methodDecl);
		return driver.tac.thisVariable();
	}
	
	public SourceVariable getSourceVariable(IVariableBinding varBinding) {
		if(varBinding.getDeclaringMethod() == null)
			throw new IllegalArgumentException("Not a local or parameter: " + varBinding);
		if(false == varBinding.getDeclaringMethod().equals(getCurrentMethod().resolveBinding()))
			throw new IllegalArgumentException("Not currently analyzing method declaring variable: " + varBinding);
		return driver.tac.sourceVariable(varBinding);
	}
	
	public ThisVariable getImplicitThisVariable(IBinding accessedElement) {
		// TODO make sure this is only called for accesses from currently analyzed method
		return driver.tac.implicitThisVariable(accessedElement);
	}
	
	@Override
	protected IFlowAnalysisDefinition<LE> createTransferFunction(MethodDeclaration method) {
		driver.switchToMethod(method);
		return driver;
	}

	/**
	 * Subclasses of this class transfer over AST nodes by translating 
	 * AST nodes into TAC instructions and calling a client-provided TAC-based 
	 * transfer function.  This class implements common functionality
	 * for querying results using TAC instructions; subclass perform
	 * the actual translation into TAC instructions and the transfer over
	 * the client-provided transfer function.
	 * @author Kevin Bierhoff
	 * @since Crystal 3.3
	 * @param <TF> Exact type of transfer function being wrapped
	 */
	protected abstract class 
	AbstractTACAnalysisDriver<TF extends IFlowAnalysisDefinition<LE>> 
	implements IFlowAnalysisDefinition<LE>, ITACAnalysisContext {

		protected TF tf;
		protected EclipseTAC tac;
		protected final CompilationUnitTACs compUnitTacs;
		
		public AbstractTACAnalysisDriver(TF tf, CompilationUnitTACs compUnitTacs) {
			super();
			this.tf = tf;
			this.compUnitTacs = compUnitTacs;
		}
		
		/**
		 * Switches the analysis over to the given method, which will
		 * require changing the {@link #tac} being used.
		 * The method being switched to <b>must be in sync</b> with 
		 * {@link MotherFlowAnalysis#getCurrentMethod()}.
		 * @param methodDecl
		 */
		public void switchToMethod(MethodDeclaration methodDecl) {
			this.tac = this.compUnitTacs.getMethodTAC(methodDecl);
		}
		
		/*
		 * IFlowAnalysisDefinition methods
		 */
		
		public AnalysisDirection getAnalysisDirection() {
			return tf.getAnalysisDirection();
		}
		
		public ILatticeOperations<LE> getLatticeOperations() {
			return tf.getLatticeOperations();
		}
		
		public LE createEntryValue(MethodDeclaration method) {
			return tf.createEntryValue(method);
		}
		
		/*
		 * ITACAnalysisContext methods
		 */
		
		public MethodDeclaration getAnalyzedMethod() {
			return TACFlowAnalysis.this.getCurrentMethod();
		}


		public SourceVariable getSourceVariable(IVariableBinding varBinding) {
			return tac.sourceVariable(varBinding);
		}

		public SuperVariable getSuperVariable() {
			return tac.superVariable(null);
		}

		public ThisVariable getThisVariable() {
			return tac.thisVariable();
		}

		public Variable getVariable(ASTNode node) {
			return tac.variable(node);
		}

		/**
		 * Internal method to derive the result for a specific instruction
		 * in an instruction sequence (which is an internal class used to
		 * represent some eclipse AST nodes as instructions.
		 * @param seq Instruction sequence
		 * @param incoming Incoming analysis information
		 * @param targetInstruction instruction within the sequence
		 * @param afterResult <code>true</code> if the <i>after</i> result is
		 * requested, <code>false</code> for the <i>before</i> result.
		 * While <code>incoming</code> is relative to the analysis direction,
		 * <code>afterResult</code> is not.
		 * @return result before or after <code>targetInstruction</code>
		 * @see EclipseInstructionSequence#deriveResult(ITACTransferFunction, TACInstruction, Object, boolean)
		 * @see EclipseInstructionSequence#deriveResult(ITACBranchSensitiveTransferFunction, List, TACInstruction, Object, boolean)
		 */
		public abstract IResult<LE> deriveResult(EclipseInstructionSequence seq, LE incoming, TACInstruction targetInstruction, boolean afterResult);
	}
	
	/**
	 * Branch-insensitive version of transferring over TAC instructions.
	 * @author Kevin Bierhoff
	 * @since Crystal 3.3
	 */
	protected class BranchInsensitiveTACAnalysisDriver 
	extends AbstractTACAnalysisDriver<ITACTransferFunction<LE>> 
	implements ITransferFunction<LE> {
		
		public BranchInsensitiveTACAnalysisDriver(ITACTransferFunction<LE> tf,
				CompilationUnitTACs compUnitTacs) {
			super(tf, compUnitTacs);
		}
		
		public LE transfer(ASTNode astNode, LE incoming) {
			LE result;
			TACInstruction instr = tac.instruction(astNode);
			if(instr == null)
				result = incoming;
			else
				result = instr.transfer(tf, incoming);
			return result;
		}
		
		public IResult<LE> deriveResult(EclipseInstructionSequence seq, LE incoming, TACInstruction targetInstruction, boolean afterResult) {
			return new SingleResult<LE>(seq.deriveResult(tf, targetInstruction, incoming, afterResult));
		}

	}
	
	/**
	 * Branch-sensitive version of transferring over TAC instructions.
	 * @author Kevin Bierhoff
	 * @since Crystal 3.3
	 */
	protected class BranchSensitiveTACAnalysisDriver 
	extends AbstractTACAnalysisDriver<ITACBranchSensitiveTransferFunction<LE>> 
	implements IBranchSensitiveTransferFunction<LE> {
		
		public BranchSensitiveTACAnalysisDriver(ITACBranchSensitiveTransferFunction<LE> tf,
				CompilationUnitTACs compUnitTacs) {
			super(tf, compUnitTacs);
		}

		public IResult<LE> transfer(ASTNode astNode, List<ILabel> labels, LE value) {
			TACInstruction instr = tac.instruction(astNode);
			if(instr == null)
				return new LabeledSingleResult<LE>(value, labels);
			else
				return instr.transfer(tf, labels, value);
		}
		
		public IResult<LE> deriveResult(EclipseInstructionSequence seq, LE incoming, 
				TACInstruction targetInstruction, boolean afterResult) {
			Set<ILabel> labels = tf.getAnalysisDirection() == AnalysisDirection.BACKWARD_ANALYSIS ?
					getLabeledResultsBefore(targetInstruction.getNode()).keySet() :
						getLabeledResultsAfter(targetInstruction.getNode()).keySet();
			return seq.deriveResult(tf, new ArrayList<ILabel>(labels), targetInstruction, incoming, afterResult);
		}
	}

}
