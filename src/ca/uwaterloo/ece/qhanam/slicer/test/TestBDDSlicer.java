package ca.uwaterloo.ece.qhanam.slicer.test;

import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;

import ca.uwaterloo.ece.qhanam.slicer.Slicer;
import junit.framework.TestCase;

/**
 * Tests the backwards data dependency slicer.
 * 
 * For now we just use a loose input space partitioning to measure coverage.
 * 
 * @author qhanam
 *
 */
public class TestBDDSlicer extends TestSlicer {
	
	private final Slicer.Options[] BDD_OPTIONS1 = new Slicer.Options[]{
			Slicer.Options.CONTROL_EXPRESSIONS_ONLY,
			Slicer.Options.OMIT_SEED,
			Slicer.Options.RESTRICTIVE};
	
	private final Slicer.Options[] BDD_OPTIONS2 = new Slicer.Options[]{
			Slicer.Options.CONTROL_EXPRESSIONS_ONLY,
			Slicer.Options.OMIT_SEED,
			Slicer.Options.CONSERVATIVE};
	
	@Override
	protected Slicer.Direction getDirection(){ return Slicer.Direction.BACKWARDS; }
	
	@Override 
	protected Slicer.Type getType(){ return Slicer.Type.DATA; }
	
	@Test
	public void testRestrictiveOption(){
		runTest("test_files/FastTreeMap.java", "clone", 225, new int[]{221,218,216}, this.BDD_OPTIONS1);
	}
	
	@Test
	public void testConservativeOption(){
		runTest("test_files/FastTreeMap.java", "clone", 225, new int[]{224,221,218,216}, this.BDD_OPTIONS2);
	}
	
	@Test
	public void testDataDeps(){
		runTest("test_files/Test1.java", "drawLine", 7, new int[]{4,2}, this.BDD_OPTIONS1);
	}
	
	@Test
	public void testControlDeps(){
		runTest("test_files/GC.java", "drawString", 2112, new int[]{2030}, this.BDD_OPTIONS1);
	}
	
	@Test
	public void testIfWhileNestedConditionals(){
		runTest("test_files/Test2.java", "getLaunchConfigurations", 11, new int[]{7,4}, this.BDD_OPTIONS1);
	}
	
	@Test
	public void testWhileLoopSeed(){
		runTest("test_files/Test2.java", "getLaunchConfigurations", 6, new int[]{3}, this.BDD_OPTIONS1);
	}
	
	@Test
	public void testNoExpressionMethod(){
		runTest("test_files/drawString-1.java", "drawString", 10, new int[]{2}, this.BDD_OPTIONS1);
	}
	
	@Test
	public void testAdditionAssignment(){
		runTest("test_files/Scrollable.java", "computeTrim", 7, new int[]{4,5}, this.BDD_OPTIONS1);
	}
}
