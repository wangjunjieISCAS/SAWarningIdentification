/**
 * Copyright (c) 2006, 2007, 2008 Marwan Abi-Antoun, Jonathan Aldrich, Nels E. Beckman, Kevin
 * Bierhoff, David Dickey, Ciera Jaspan, Thomas LaToza, Gabriel Zenarosa, and others.
 * 
 * This file is part of Crystal.
 * 
 * Crystal is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Crystal is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with Crystal. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package edu.cmu.cs.crystal.annotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IMemberValuePairBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import edu.cmu.cs.crystal.internal.CrystalRuntimeException;
import edu.cmu.cs.crystal.util.Pair;

/**
 * This class is a database for annotations. It can store annotations of methods we have analyzed or
 * of ones we have not. Either way, it does not store any AST information, so it returns Crystal
 * objects that represent an annotation. Those who want to use this database must register the type
 * of annotations to scan for, @see{ICrystalAnnotation}.
 * 
 * A ICrystalAnnotation provides all the information that one needs about the annotations. An
 * AnnotationSummary is particularly useful for finding all information relating to the method,
 * including parameters.
 * 
 * And just in case you were wondering...we can not use regular reflection objects here (Class,
 * Annotation, etc.) as the files we are analyzing aren't on the classpath of the system we are
 * running.
 * 
 * The annotation database currently allows clients to request annotations if they provide an ITypeBinding.
 * However, this means that Crystal needs to have parsed and analyzed the code in question to get an ITypeBinding
 * in the first place. Instead, we would like to retrieve annotations from unanalyzed code (for example, a library).
 * This can currently be done with @link{#getAnnosForType(IType)}, and the plan is to eventually allow methods and
 * fields based upon the Java Model instead of the Java ASTNodes.
 * 
 * @author ciera
 * @author Nels Beckman
 * 
 */
public class AnnotationDatabase {

	private static final Logger log = Logger.getLogger(AnnotationDatabase.class.getName());

	/**
	 * {@link MultiAnnotation}'s fully qualified name or {@code null}
	 * if it's not in the classpath (means edu.cmu.cs.planno plugin not loaded).
	 * Notice that edu.cmu.cs.planno is an optional dependency so it may be missing.
	 */
	private static final String MULTI_ANNOTATION_CLASSNAME;
	static {
		String className = null;
		try {
			className = MultiAnnotation.class.getName();
		} catch (Throwable t) {
			// Expect NoClassDefFoundError if MultiAnnotation is missing
			// catch-all just in case so we can keep going no matter what
			log.log(Level.INFO, "@MultiAnnotation not available, install edu.cmu.cs.planno plugin if you want to use multi-annotations", t);
		}
		MULTI_ANNOTATION_CLASSNAME = className;
	}

	private Map<String, Class<? extends ICrystalAnnotation>> qualNames;
	private Map<String, Class<? extends ICrystalAnnotation>> metaQualNames;

	private Map<String, AnnotationSummary> methods;
	private Map<String, List<ICrystalAnnotation>> classes;
	private Map<String, List<ICrystalAnnotation>> fields;

	public AnnotationDatabase() {
		qualNames = new HashMap<String, Class<? extends ICrystalAnnotation>>();
		metaQualNames = new HashMap<String, Class<? extends ICrystalAnnotation>>();
		methods = new HashMap<String, AnnotationSummary>();
		classes = new HashMap<String, List<ICrystalAnnotation>>();
		fields = new HashMap<String, List<ICrystalAnnotation>>();

	}

	public void register(String fullyQualifiedName,
	    Class<? extends ICrystalAnnotation> crystalAnnotationClass, boolean isMeta) {
		Class<? extends ICrystalAnnotation> annoClass =
		    isMeta ? metaQualNames.get(fullyQualifiedName) : qualNames.get(fullyQualifiedName);

		if (crystalAnnotationClass != null && annoClass != null
		    && !(crystalAnnotationClass.isAssignableFrom(annoClass))) {
			throw new CrystalRuntimeException("Can not register " + fullyQualifiedName + " for "
			    + crystalAnnotationClass.getCanonicalName() + ", the class "
			    + annoClass.getCanonicalName() + " is already registered.");
		}
		if (isMeta)
			metaQualNames.put(fullyQualifiedName, crystalAnnotationClass);
		else
			qualNames.put(fullyQualifiedName, crystalAnnotationClass);
	}

	/***
	 * Given a method binding, returns a summary that represents annotation info for that method
	 * declaration.
	 */
	public AnnotationSummary getSummaryForMethod(IMethodBinding binding) {
		while (binding != binding.getMethodDeclaration())
			binding = binding.getMethodDeclaration();
		String name = binding.getKey();

		AnnotationSummary result = methods.get(name);
		if (result == null) {
			result = createMethodSummary(binding);
			methods.put(name, result);
		}
		return result;
	}

	private AnnotationSummary createMethodSummary(IMethodBinding binding) {
		int paramCount = binding.getParameterTypes().length;
		String[] paramNames = new String[paramCount];
		for (int i = 0; i < paramCount; i++) {
			paramNames[i] = "arg" + i;
		}
		AnnotationSummary result = new AnnotationSummary(paramNames);
		result.addAllReturn(createAnnotations(binding.getAnnotations()));
		try {
			for (int i = 0; i < paramCount; i++) {
				result.addAllParameter(createAnnotations(binding.getParameterAnnotations(i)), i);
			}
		}
		catch (NullPointerException e) {
			if (log.isLoggable(Level.WARNING))
				log.log(Level.WARNING, "Bug in JDT (Eclipse 3.4M5) triggered in " + binding
				    + ".  Not all annotations on parameters might be available.", e);
		}

		return result;
	}

	/**
	 * This method will return the list of annotations associated with the
	 * given type. It's very similar in functionality to 
	 * {@link #getAnnosForType(ITypeBinding)} except that it works on the
	 * Java model element, so it does not require the type to have been parsed
	 * and analyzed by Crystal.
	 * @throws JavaModelException 
	 */
	public List<ICrystalAnnotation> getAnnosForType(IType type) throws JavaModelException {
		String name = type.getKey();

		List<ICrystalAnnotation> result = classes.get(name);
		if (result == null) {
			result = createAnnotations(type.getAnnotations(), type);
			classes.put(name, result);
		}
		return result;
	}
	
	/**
	 * This method will return the list of annotations associated with the
	 * given type. To work, this binding had to be parsed by Crystal. If you do not have
	 * a type that was parsed by Crystal, use {@link #getAnnosForType(IType)}.
	 */
	public List<ICrystalAnnotation> getAnnosForType(ITypeBinding type) {
		while (type != type.getTypeDeclaration())
			type = type.getTypeDeclaration();
		if (type.isPrimitive())
			return Collections.emptyList();
		if (type.isArray()) {
			log.warning("Annotations for array type requested: " + type.getName());
		}

		String name = type.getKey();

		List<ICrystalAnnotation> result = classes.get(name);
		if (result == null) {
			result = createAnnotations(type.getAnnotations());
			classes.put(name, result);
		}
		return result;
	}

	/**
	 * This method will return the list of annotations associated with the
	 * given variable. 
	 */
	public List<ICrystalAnnotation> getAnnosForVariable(IVariableBinding binding) {
		while (binding != binding.getVariableDeclaration())
			binding = binding.getVariableDeclaration();
		String name = binding.getKey();

		List<ICrystalAnnotation> result = fields.get(name);
		if (result == null) {
			result = createAnnotations(binding.getAnnotations());
			fields.put(name, result);
		}
		return result;
	}

	protected List<ICrystalAnnotation> createAnnotations(IAnnotationBinding[] bindings) {
		List<ICrystalAnnotation> result = new ArrayList<ICrystalAnnotation>(bindings.length);
		for (IAnnotationBinding anno : bindings) {
			result.addAll(createAnnotations(anno));
		}
		return Collections.unmodifiableList(result);
	}

	/**
	 * See {@link #createAnnotations(IAnnotationBinding[])}.
	 * @throws JavaModelException 
	 */
	protected List<ICrystalAnnotation> createAnnotations(IAnnotation[] annotations, IType relative_type) throws JavaModelException {
		List<ICrystalAnnotation> result = new ArrayList<ICrystalAnnotation>(annotations.length);
		for (IAnnotation anno : annotations) {
			result.addAll(createAnnotations(anno, relative_type));
		}
		return Collections.unmodifiableList(result);
	}
	
	/**
	 * See {@link #createAnnotations(IAnnotationBinding)}.
	 * @throws JavaModelException 
	 */
	protected List<ICrystalAnnotation> createAnnotations(IAnnotation anno, IType relative_type) throws JavaModelException {
		if (isMulti(anno, relative_type)) {
			for (IMemberValuePair pair : anno.getMemberValuePairs()) {
				Object value;
				if ("value".equals(pair.getMemberName()))
					value = pair.getValue();
				else if ("annos".equals(pair.getMemberName()))
					value = pair.getValue();
				else {
					log.warning("Ignore extra attribute in multi-annotation " + anno.getElementName()
					    + ": " + pair.toString());
					continue;
				}
				if (value instanceof Object[]) {
					Object[] array = (Object[]) value;
					List<ICrystalAnnotation> result =
					    new ArrayList<ICrystalAnnotation>(array.length);
					for (Object o : array) {
						result.add(createAnnotation((IAnnotation) o, relative_type));
					}
					return Collections.unmodifiableList(result);
				}
				else {
					// Eclipse doesn't desugar single-element arrays with omitted braces as arrays
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=223225
					return Collections.singletonList(createAnnotation((IAnnotationBinding) value));
				}
			}
			log.warning("Couldn't find annotation array in: " + anno);
		}
		return Collections.singletonList(createAnnotation(anno, relative_type));
	}
	
	protected List<ICrystalAnnotation> createAnnotations(IAnnotationBinding binding) {
		if (isMulti(binding)) {
			for (IMemberValuePairBinding pair : binding.getAllMemberValuePairs()) {
				Object value;
				if ("value".equals(pair.getName()))
					value = pair.getValue();
				else if ("annos".equals(pair.getName()))
					value = pair.getValue();
				else {
					log.warning("Ignore extra attribute in multi-annotation " + binding.getName()
					    + ": " + pair.toString());
					continue;
				}
				if (value instanceof Object[]) {
					Object[] array = (Object[]) value;
					List<ICrystalAnnotation> result =
					    new ArrayList<ICrystalAnnotation>(array.length);
					for (Object o : array) {
						result.add(createAnnotation((IAnnotationBinding) o));
					}
					return Collections.unmodifiableList(result);
				}
				else {
					// Eclipse doesn't desugar single-element arrays with omitted braces as arrays
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=223225
					return Collections.singletonList(createAnnotation((IAnnotationBinding) value));
				}
			}
			log.warning("Couldn't find annotation array in: " + binding);
		}
		return Collections.singletonList(createAnnotation(binding));
	}

	/**
	 * Given an annotation binding, determine what parser to use for it.
	 * 
	 * @param binding
	 * @return
	 */
	protected ICrystalAnnotation createAnnotation(IAnnotationBinding binding) {
		String qualName;
		ICrystalAnnotation crystalAnno;

		qualName = binding.getAnnotationType().getQualifiedName();

		crystalAnno = createCrystalAnnotation(binding.getAnnotationType());
		crystalAnno.setName(qualName);

		for (IMemberValuePairBinding pair : binding.getAllMemberValuePairs()) {
			crystalAnno.setObject(pair.getName(), getAnnotationValue(pair.getValue(), pair
			    .getMethodBinding().getReturnType().isArray()));
		}
		return crystalAnno;
	}

	
	
	/**
	 * Find the type of the given annotation.
	 * @param anno The annotation whose type you need.
	 * @param relative_type The relative type that we want to use to look up this annotation's type. In the
	 * Java model, you can only look up a type wrt another type.
	 * @return The type of the given annotation.
	 * @throws JavaModelException
	 */
	protected IType getTypeOfAnnotation(IAnnotation anno, IType relative_type) throws JavaModelException {
		IJavaProject project = anno.getJavaProject();
		Pair<String,String> name = getQualifiedAnnoType(anno, relative_type);
		IType anno_type = project.findType(name.fst(), name.snd());
		
		return anno_type;
	}
	
	/**
	 * Returns the fully qualified type name, as a package/type pair, for the given annotation relative
	 * to the relative type.
	 */
	protected Pair<String,String> getQualifiedAnnoType(IAnnotation anno, IType relative_type) throws JavaModelException {
		String[][] names = relative_type.resolveType(anno.getElementName());
		
		if( names.length > 1 ) throw new RuntimeException("Not yet implemented.");
		
		String pack = names[0][0];
		String clazz = names[0][1];
		return Pair.create(pack, clazz);
	}
	
	/**
	 * See {@link #createAnnotation(IAnnotationBinding)}.
	 * @throws JavaModelException 
	 */
	protected ICrystalAnnotation createAnnotation(IAnnotation anno, IType relative_type) throws JavaModelException {
		String qualName;
		ICrystalAnnotation crystalAnno;

		Pair<String,String> qual_name_ = getQualifiedAnnoType(anno, relative_type);
		qualName = "".equals(qual_name_.fst()) ? qual_name_.snd() : qual_name_.fst() + "." + qual_name_.snd();

		IType anno_type = getTypeOfAnnotation(anno, relative_type);
		crystalAnno = createCrystalAnnotation(anno_type);
		crystalAnno.setName(qualName);

		// These members have a value that is not default.
		Set<String> has_non_default_value = new HashSet<String>();
		
		for (IMemberValuePair pair : anno.getMemberValuePairs()) {
			boolean val_is_array = pair.getValue() instanceof Object[];
			
			has_non_default_value.add(pair.getMemberName());
			crystalAnno.setObject(pair.getMemberName(), 
					getAnnotationValue(pair.getValue(), val_is_array));
		}
		
		// Now, for every default that we have not already seen a value for
		// put the default value in.
		for( IMemberValuePair pair : findAnnotationDefaults(anno_type) ) {
			boolean val_is_array = pair.getValue() instanceof Object[];
			
			if( !has_non_default_value.contains(pair.getMemberName()) ) {
				crystalAnno.setObject(pair.getMemberName(), 
						getAnnotationValue(pair.getValue(), val_is_array));
			}
		}
		
		return crystalAnno;
	}
	
	/**
	 * Returns the default annotation values for the given annotation. 
	 * @throws JavaModelException 
	 */
	private List<IMemberValuePair> findAnnotationDefaults(IType anno_type) throws JavaModelException {
		IMethod[] properties = anno_type.getMethods();
		List<IMemberValuePair> result = new ArrayList<IMemberValuePair>();
		
		for( IMethod property : properties ) {
			IMemberValuePair default_val_ = property.getDefaultValue();
			if( default_val_ != null )
				result.add(default_val_);
		}
		
		return result;
	}

	/**
	 * Checks whether this annotation is marked as a multi annotation, as described by
	 * MultiAnnotation
	 * 
	 * @param annoBinding
	 * @return true id this is a multi annotation, and false if it is not.
	 */
	public boolean isMulti(IAnnotationBinding annoBinding) {
		ITypeBinding binding = annoBinding.getAnnotationType();

		for (IAnnotationBinding meta : binding.getAnnotations()) {
			if (meta.getAnnotationType().getQualifiedName().equals(MULTI_ANNOTATION_CLASSNAME))
				return true;
		}
		return false;
	}

	/**
	 * See {@link #isMulti(IAnnotationBinding)}.
	 * @throws JavaModelException 
	 */
	public boolean isMulti(IAnnotation anno, IType relative_type) throws JavaModelException {
		// First we have to go from this particular annotation, to its declaration...
		IType anno_type = getTypeOfAnnotation(anno, relative_type);

		for (IAnnotation meta : anno_type.getAnnotations()) {
			Pair<String,String> qual_name_ = getQualifiedAnnoType(meta, anno_type);
			String qual_name = "".equals(qual_name_.fst()) ? qual_name_.snd() : qual_name_.fst() + "." + qual_name_.snd();
			if (qual_name.equals(MULTI_ANNOTATION_CLASSNAME))
				return true;
		}
		return false;
	}
	
	/**
	 * @param value
	 * @return
	 */
	private Object getAnnotationValue(Object rawValue, boolean forceArray) {
		if (rawValue instanceof Object[]) {
			Object[] array = (Object[]) rawValue;
			Object[] result = new Object[array.length];
			for (int i = 0; i < array.length; i++)
				result[i] = getAnnotationValue(array[i], false);
			return result;
		}
		if (rawValue instanceof IAnnotationBinding) {
			rawValue = createAnnotation((IAnnotationBinding) rawValue);
		}
		if (forceArray) {
			// this is a workaround for an Eclipse "bug" (#223225)
			// Eclipse doesn't desugar single-element arrays with omitted braces as arrays
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=223225
			return new Object[] { rawValue };
		}
		// other values are literals
		return rawValue;
	}

	/**
	 * See {@link #createCrystalAnnotation(ITypeBinding)}.
	 * @throws JavaModelException 
	 */
	public ICrystalAnnotation createCrystalAnnotation(IType typeOfAnnotation) throws JavaModelException {
		Class<? extends ICrystalAnnotation> annoClass =
		    qualNames.get(typeOfAnnotation.getFullyQualifiedName('.'));
		
		if (annoClass == null) {
			IAnnotation[] metas = typeOfAnnotation.getAnnotations();
			// might still be a meta annotation. Check for this.
			for (int ndx = 0; ndx < metas.length && annoClass == null; ndx++) {
				IAnnotation meta = metas[ndx];
				
				Pair<String,String> meta_name = getQualifiedAnnoType(meta, typeOfAnnotation);
				String meta_name_ = meta_name.fst() + "." + meta_name.snd();
				
				annoClass = metaQualNames.get(meta_name_);
			}
		}

		if (annoClass == null)
			return new CrystalAnnotation();

		try {
			// this annotation is registered directly
			return annoClass.newInstance();
		}
		catch (InstantiationException e) {
			log.log(
			    Level.WARNING,
			    "Error instantiating custom annotation parser.  Using default representation.", e);
			return new CrystalAnnotation();
		}
		catch (IllegalAccessException e) {
			log.log(
			    Level.WARNING,
			    "Error accessing custom annotation parser.  Using default representation.", e);
			return new CrystalAnnotation();
		}
	}
	
	public ICrystalAnnotation createCrystalAnnotation(ITypeBinding typeBinding) {
		Class<? extends ICrystalAnnotation> annoClass =
		    qualNames.get(typeBinding.getQualifiedName());

		if (annoClass == null) {
			IAnnotationBinding[] metas = typeBinding.getAnnotations();
			// might still be a meta annotation. Check for this.
			for (int ndx = 0; ndx < metas.length && annoClass == null; ndx++) {
				IAnnotationBinding meta = metas[ndx];
				String metaName = meta.getAnnotationType().getQualifiedName();
				annoClass = metaQualNames.get(metaName);
			}
		}

		if (annoClass == null)
			return new CrystalAnnotation();

		try {
			// this annotation is registered directly
			return annoClass.newInstance();
		}
		catch (InstantiationException e) {
			log.log(
			    Level.WARNING,
			    "Error instantiating custom annotation parser.  Using default representation.", e);
			return new CrystalAnnotation();
		}
		catch (IllegalAccessException e) {
			log.log(
			    Level.WARNING,
			    "Error accessing custom annotation parser.  Using default representation.", e);
			return new CrystalAnnotation();
		}
	}

	public void addAnnotationToField(ICrystalAnnotation anno, FieldDeclaration field) {
		IVariableBinding binding;
		String name;
		List<ICrystalAnnotation> annoList;

		if (field.fragments().isEmpty())
			return;

		binding = ((VariableDeclarationFragment) field.fragments().get(0)).resolveBinding();
		name = binding.getKey();
		annoList = fields.get(name);
		if (annoList == null) {
			annoList = new ArrayList<ICrystalAnnotation>();
			fields.put(name, annoList);
		}
		annoList.add(anno);
	}

	public void addAnnotationToMethod(AnnotationSummary anno, MethodDeclaration method) {
		IMethodBinding binding = method.resolveBinding();
		String name = binding.getKey();

		AnnotationSummary existing = methods.get(name);
		if (existing == null)
			methods.put(name, anno);
		else
			existing.add(anno);
	}

	public void addAnnotationToType(ICrystalAnnotation anno, TypeDeclaration type) {
		ITypeBinding binding = type.resolveBinding();
		String name = binding.getKey();
		List<ICrystalAnnotation> annoList;

		annoList = classes.get(name);
		if (annoList == null) {
			annoList = new ArrayList<ICrystalAnnotation>();
			classes.put(name, annoList);
		}
		annoList.add(anno);
	}

	public static <A extends ICrystalAnnotation> List<A> filter(List<ICrystalAnnotation> list,
	    Class<A> type) {
		List<A> result = new LinkedList<A>();
		for (ICrystalAnnotation anno : list) {
			if (type.isAssignableFrom(anno.getClass())) {
				@SuppressWarnings("unchecked") A anno2 = (A) anno;
				result.add(anno2);
			}
		}
		return result;
	}

	static protected ICrystalAnnotation findAnnotation(String name, List<ICrystalAnnotation> list) {
		ICrystalAnnotation result = null;
		for (ICrystalAnnotation anno : list) {
			if (anno.getName().equals(name)) {
				assert result == null;
				result = anno;
			}
		}
		return result;
	}
}
