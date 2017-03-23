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
public class TestFDDSlicer extends TestSlicer {
	
	private final Slicer.Options[] FDD_OPTIONS1 = new Slicer.Options[]{
			Slicer.Options.CONTROL_EXPRESSIONS_ONLY,
			Slicer.Options.OMIT_SEED,
			Slicer.Options.RESTRICTIVE};
	
	private final Slicer.Options[] FDD_OPTIONS2 = new Slicer.Options[]{
			Slicer.Options.CONTROL_EXPRESSIONS_ONLY,
			Slicer.Options.OMIT_SEED,
			Slicer.Options.CONSERVATIVE};
	
	@Override
	protected Slicer.Direction getDirection(){ return Slicer.Direction.FORWARDS; }
	
	@Override 
	protected Slicer.Type getType(){ return Slicer.Type.DATA; }
	
	@Test
	public void testMemberVariable(){
		runTest("test_files/HelloWorld.java", "doStuff", 8, new int[]{12,13}, this.FDD_OPTIONS1);
	}
	
	@Test
	public void testLoopConditional(){
		runTest("test_files/HelloWorld.java", "doStuff", 9, new int[]{11,12,14,15,21}, this.FDD_OPTIONS1);
	}
}
