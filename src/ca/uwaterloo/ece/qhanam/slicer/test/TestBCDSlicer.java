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
public class TestBCDSlicer extends TestSlicer {
	
	private final Slicer.Options[] BCD_OPTIONS1 = new Slicer.Options[]{
			Slicer.Options.OMIT_SEED};

	private final Slicer.Options[] BCD_OPTIONS2 = new Slicer.Options[]{
			Slicer.Options.OMIT_SEED, Slicer.Options.SWITCH_AS_IF};
	
	@Override
	protected Slicer.Direction getDirection(){ return Slicer.Direction.BACKWARDS; }
	
	@Override 
	protected Slicer.Type getType(){ return Slicer.Type.CONTROL; }
	
	/*
	@Test
	public void testStatementCaseConditionals(){
		runTest("test_files/Test3.java", "eventListener", 9, new int[]{8,7,4}, this.BCD_OPTIONS1);
	}
	
	@Test
	public void testSwitchAsIfElse(){
		runTest("test_files/Test3.java", "eventListener", 9, new int[]{8,7,5,4}, this.BCD_OPTIONS2);
	}
	*/
	@Test
	public void testDataDeps(){
		runTest("test_files/Test1.java", "drawLine", 7, new int[]{6}, this.BCD_OPTIONS1);
	}
	
	@Test
	public void testControlDeps(){
		runTest("test_files/GC.java", "drawString", 2112, new int[]{2055,2059}, this.BCD_OPTIONS1);
	}
	
	@Test
	public void testIfWhileNestedConditionals(){
		runTest("test_files/Test2.java", "getLaunchConfigurations", 11, new int[]{10,6}, this.BCD_OPTIONS1);
	}
	
	@Test
	public void testWhileLoopSeed(){
		runTest("test_files/Test2.java", "getLaunchConfigurations", 6, new int[]{}, this.BCD_OPTIONS1);
	}
	
	@Test
	public void testNoExpressionMethod(){
		runTest("test_files/drawString-1.java", "drawString", 10, new int[]{9,6}, this.BCD_OPTIONS1);
	}
	
	@Test
	public void testAdditionAssignment(){
		runTest("test_files/Scrollable.java", "computeTrim", 7, new int[]{}, this.BCD_OPTIONS1);
	}
}
