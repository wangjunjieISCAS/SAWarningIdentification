package edu.cmu.cs.crystal.util;

public interface TypeHierarchy {

	/**
	 * 
	 * @param subType A fully qualified type name in the classpath.  Should be . separated.
	 * @param superType A fully qualified type name in the classpath.  Should be . separated.
	 * @return true if subType <: superType
	 */
	public abstract boolean isSubtypeCompatible(String subType, String superType);

	/**
	 * 
	 * @param t1 A fully qualified type name in the classpath. Should be . separated.
	 * @param t2 A fully qualified type name in the classpath. Should be . separated.
	 * @return true if there exists some t3 such that t3 <: t1 and t3 <: t2
	 */
	public abstract boolean existsCommonSubtype(String t1, String t2);

	/**
	 * A version of existsCommonSubtype that allows for the client to skip checks if the
	 * client has already done them. It's a minor optimization, but may come in handy.
	 * 
	 * @param t1 A fully qualified type name in the classpath. Should be . separated.
	 * @param t2 A fully qualified type name in the classpath. Should be . separated.
	 * @param skipCheck1 true if we should skip the check for t1 <: t2.
	 * @param skipCheck2 true if we should skip the check for t2 <: t1
	 * @return true if there exists some t3 such that t3 <: t1 and t3 <: t2
	 */
	public abstract boolean existsCommonSubtype(String t1, String t2, boolean skipCheck1, boolean skipCheck2);
}