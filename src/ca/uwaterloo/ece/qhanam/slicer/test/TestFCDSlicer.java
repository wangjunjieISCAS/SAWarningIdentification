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
public class TestFCDSlicer extends TestSlicer {
	
	private final Slicer.Options[] BCD_OPTIONS1 = new Slicer.Options[]{
			Slicer.Options.OMIT_SEED};
	
	@Override
	protected Slicer.Direction getDirection(){ return Slicer.Direction.FORWARDS; }
	
	@Override 
	protected Slicer.Type getType(){ return Slicer.Type.CONTROL; }
	
	@Test
	public void testSimpleFor(){
		runTest("test_files/HelloWorld.java", "doStuff", 11, new int[]{12,13,14,15}, this.BCD_OPTIONS1);
	}
	
	@Test
	public void testIfReturn(){
		runTest("test_files/HelloWorld.java", "doNothing", 28, new int[]{29,30,31,32,33}, this.BCD_OPTIONS1);
	}
	
	@Test
	public void testNotControl(){
		runTest("test_files/HelloWorld.java", "doNothing", 27, new int[]{}, this.BCD_OPTIONS1);
	}
	
	@Test
	public void testNestedControl(){
		runTest("test_files/HelloWorld.java", "nestedControl", 39, new int[]{40,41,42}, this.BCD_OPTIONS1);
	}
}
