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
 * Tests the backwards data dependency (conservative mode) slicer.
 * @author qhanam
 *
 */
public abstract class TestSlicer extends TestCase {
	
	protected abstract Slicer.Direction getDirection();
	protected abstract Slicer.Type getType();
	
	/**
	 * Tests the control and data dependency slices.
	 * @param path
	 * @param method
	 * @param seed
	 * @param controlExpected
	 * @param expected
	 */
	public void runTest(String path, String method, int seed, int[] expected, Slicer.Options[] options){
		List<ASTNode> dataActual = getSlice(path, method, seed, options);
		
		System.out.println("\nNodes in slice:" + dataActual );
		for(ASTNode node : dataActual){
			System.out.print(Slicer.getLineNumber(node) + ": " + node.toString());
		}
		
		checkSlice(dataActual, expected);
	}
	
	/**
	 * Checks the slice against the expected output.
	 * @param slice
	 * @param expected
	 */
	public void checkSlice(List<ASTNode> slice, int[] expected){
		int i = 0;
		int[] actual = new int[slice.size()];
		for(ASTNode statement : slice){
			actual[i] = Slicer.getLineNumber(statement);
			i++;
		}
		Arrays.sort(actual);
		Arrays.sort(expected);
		assertArrayEquals(expected, actual);
	}
	
	/**
	 * Generate a data/control dependency slice.
	 * @param path
	 * @param method
	 * @param seedLine
	 */
	public List<ASTNode> getSlice(String path, String method, int seedLine, Slicer.Options[] options){
		CompilationUnit cu = SampleUse.getAST(path);
		MethodVisitor methodVisitor;;
		
		methodVisitor = new MethodVisitor(method, seedLine, this.getDirection(), this.getType(), options);
		cu.accept(methodVisitor);
		
		return methodVisitor.slice;
	}
}
