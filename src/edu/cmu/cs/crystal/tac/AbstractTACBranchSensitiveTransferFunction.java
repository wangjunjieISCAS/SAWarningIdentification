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

import java.util.List;

import edu.cmu.cs.crystal.flow.AnalysisDirection;
import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.flow.LabeledSingleResult;
import edu.cmu.cs.crystal.tac.model.ArrayInitInstruction;
import edu.cmu.cs.crystal.tac.model.BinaryOperation;
import edu.cmu.cs.crystal.tac.model.CastInstruction;
import edu.cmu.cs.crystal.tac.model.ConstructorCallInstruction;
import edu.cmu.cs.crystal.tac.model.CopyInstruction;
import edu.cmu.cs.crystal.tac.model.DotClassInstruction;
import edu.cmu.cs.crystal.tac.model.EnhancedForConditionInstruction;
import edu.cmu.cs.crystal.tac.model.InstanceofInstruction;
import edu.cmu.cs.crystal.tac.model.LoadArrayInstruction;
import edu.cmu.cs.crystal.tac.model.LoadFieldInstruction;
import edu.cmu.cs.crystal.tac.model.LoadLiteralInstruction;
import edu.cmu.cs.crystal.tac.model.MethodCallInstruction;
import edu.cmu.cs.crystal.tac.model.NewArrayInstruction;
import edu.cmu.cs.crystal.tac.model.NewObjectInstruction;
import edu.cmu.cs.crystal.tac.model.ReturnInstruction;
import edu.cmu.cs.crystal.tac.model.SourceVariableDeclaration;
import edu.cmu.cs.crystal.tac.model.SourceVariableReadInstruction;
import edu.cmu.cs.crystal.tac.model.StoreArrayInstruction;
import edu.cmu.cs.crystal.tac.model.StoreFieldInstruction;
import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.tac.model.UnaryOperation;

/**
 * Abstract base class for defining branch-sensitive flow analyses 
 * based on 3-address code instructions ({@link TACInstruction}).  
 * It returns {@link LabeledSingleResult}s in all cases.  
 * Override the <code>transfer</code> methods for instructions that your analysis
 * needs to consider.
 * @author Kevin Bierhoff
 *
 * @param <LE> LatticeElement subclass that represents the analysis knowledge.
 */
public abstract class AbstractTACBranchSensitiveTransferFunction<LE> 
		implements ITACBranchSensitiveTransferFunction<LE> {

	private ITACAnalysisContext analysisContext;

	/**
	 * Default constructor.
	 */
	public AbstractTACBranchSensitiveTransferFunction() {
		super();
	}

	/**
	 * Returns {@link AnalysisDirection#FORWARD_ANALYSIS}.
	 * @return {@link AnalysisDirection#FORWARD_ANALYSIS}.
	 */
	public AnalysisDirection getAnalysisDirection() {
		return AnalysisDirection.FORWARD_ANALYSIS;
	}

	public ITACAnalysisContext getAnalysisContext() {
		return analysisContext;
	}

	public void setAnalysisContext(ITACAnalysisContext analysisContext) {
		this.analysisContext = analysisContext;
	}

	public IResult<LE> transfer(ArrayInitInstruction instr, List<ILabel> labels,
			LE value) {
		return LabeledSingleResult.createResult(value, labels);
	}

	public IResult<LE> transfer(BinaryOperation binop, List<ILabel> labels,
			LE value) {
		return LabeledSingleResult.createResult(value, labels);
	}

	public IResult<LE> transfer(CastInstruction instr, List<ILabel> labels,
			LE value) {
		return LabeledSingleResult.createResult(value, labels);
	}

	public IResult<LE> transfer(DotClassInstruction instr, List<ILabel> labels,
			LE value) {
		return LabeledSingleResult.createResult(value, labels);
	}

	public IResult<LE> transfer(ConstructorCallInstruction instr, List<ILabel> labels,
			LE value) {
		return LabeledSingleResult.createResult(value, labels);
	}

	public IResult<LE> transfer(CopyInstruction instr, List<ILabel> labels,
			LE value) {
		return LabeledSingleResult.createResult(value, labels);
	}

	public IResult<LE> transfer(EnhancedForConditionInstruction instr,
			List<ILabel> labels, LE value) {
		return LabeledSingleResult.createResult(value, labels);
	}

	public IResult<LE> transfer(InstanceofInstruction instr, List<ILabel> labels,
			LE value) {
		return LabeledSingleResult.createResult(value, labels);
	}

	public IResult<LE> transfer(LoadLiteralInstruction instr, List<ILabel> labels,
			LE value) {
		return LabeledSingleResult.createResult(value, labels);
	}

	public IResult<LE> transfer(LoadArrayInstruction instr, List<ILabel> labels,
			LE value) {
		return LabeledSingleResult.createResult(value, labels);
	}

	public IResult<LE> transfer(LoadFieldInstruction instr, List<ILabel> labels,
			LE value) {
		return LabeledSingleResult.createResult(value, labels);
	}

	public IResult<LE> transfer(MethodCallInstruction instr, List<ILabel> labels,
			LE value) {
		return LabeledSingleResult.createResult(value, labels);
	}

	public IResult<LE> transfer(NewArrayInstruction instr, List<ILabel> labels,
			LE value) {
		return LabeledSingleResult.createResult(value, labels);
	}

	public IResult<LE> transfer(NewObjectInstruction instr, List<ILabel> labels,
			LE value) {
		return LabeledSingleResult.createResult(value, labels);
	}

	public IResult<LE> transfer(ReturnInstruction instr, List<ILabel> labels,
			LE value) {
		return LabeledSingleResult.createResult(value, labels);
	}

	public IResult<LE> transfer(StoreArrayInstruction instr, List<ILabel> labels,
			LE value) {
		return LabeledSingleResult.createResult(value, labels);
	}

	public IResult<LE> transfer(StoreFieldInstruction instr, List<ILabel> labels,
			LE value) {
		return LabeledSingleResult.createResult(value, labels);
	}

	public IResult<LE> transfer(SourceVariableDeclaration instr,
			List<ILabel> labels, LE value) {
		return LabeledSingleResult.createResult(value, labels);
	}

	public IResult<LE> transfer(SourceVariableReadInstruction instr,
			List<ILabel> labels, LE value) {
		return LabeledSingleResult.createResult(value, labels);
	}

	public IResult<LE> transfer(UnaryOperation unop, List<ILabel> labels,
			LE value) {
		return LabeledSingleResult.createResult(value, labels);
	}

}
