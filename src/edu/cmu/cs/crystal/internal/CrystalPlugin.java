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
package edu.cmu.cs.crystal.internal;

import java.util.logging.Logger;


/**
 * The eclipse plugin that will launch and maintain the analysis.
 * 
 * Eventually, this should be an extension point. For now,
 * just plug in your analyses here.
 * @author David Dickey
 * @author Jonathan Aldrich
 * 
 */
@Deprecated
public class CrystalPlugin extends AbstractCrystalPlugin {
	
	private static final Logger logger = Logger.getLogger(CrystalPlugin.class.getName());
	
	/**
	 * Modify this method to instantiate and register the analyses you
	 * want the framework to run.
	 */
	public void setupCrystalAnalyses(Crystal crystal) {
		logger.info("CrystalPlugin::setupCrystalAnalyses() Begin");
		// ********************************************************************
		// INSTANTIATE AND REGISTER YOUR ANALYSES BELOW
		
//		CFGTestAnalysis myCFGAnalysis = new EclipseCFGTestAnalysis(); crystal.registerAnalysis(myCFGAnalysis);
// 		RDAnalysis rd = new RDAnalysis(); crystal.registerAnalysis(rd);

		//ZeroAnalysis za = new ZeroAnalysis(); crystal.registerAnalysis(za);
		//ProtocolAnalysis pa = new ProtocolAnalysis(); crystal.registerAnalysis(pa);
		
//		EclipseTACTestRunAnalysis tactest = new EclipseTACTestRunAnalysis(); crystal.registerAnalysis(tactest);
//		EclipseTACSimpleTestDriver simpletest = new EclipseTACSimpleTestDriver(); crystal.registerAnalysis(simpletest);
// 		LVAnalysis lv = new LVAnalysis(); crystal.registerAnalysis(lv);

//		ConstantAnalysis constant = new ConstantAnalysis(); crystal.registerAnalysis(constant);
//		LoopCountingAnalysis loops = new LoopCountingAnalysis(); crystal.registerAnalysis(loops);
//		MayAliasAnalysis may = new MayAliasAnalysis(loops); crystal.registerAnalysis(may);
//		RelationshipChecker check = new RelationshipChecker(); crystal.registerAnalysis(check);
//		RelationshipAnalysis relAnalysis = new RelationshipAnalysis(may, constant, check); crystal.registerAnalysis(relAnalysis);
//		ConstraintAnalysis constraint = new ConstraintAnalysis(relAnalysis, may, constant, new ConstraintChecker()); crystal.registerAnalysis(constraint);
//		NullPointerAnalysis np = new SimpleNullPointerAnalysis(); crystal.registerAnalysis(np);
 //		BranchSensitiveNullPointerAnalysis branchNP = new BranchSensitiveNullPointerAnalysis(); crystal.registerAnalysis(branchNP);
 //		TACNullAnalysis tnp = new TACNullAnalysis(); crystal.registerAnalysis(tnp);
 //		BranchSensitiveNullPointerAnalysis bsnp = new BranchSensitiveNullPointerAnalysis(); crystal.registerAnalysis(bsnp);


		// INSTATIATE AND REGISTER YOUR ANALYSES ABOVE
		// ********************************************************************
		logger.info("CrystalPlugin::setupCrystalAnalyses() End");	
	}
}
