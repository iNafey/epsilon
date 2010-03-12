/*******************************************************************************
 * Copyright (c) 2009 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Louis Rose - initial API and implementation
 ******************************************************************************
 *
 * $Id$
 */
package org.eclipse.epsilon.eol.models.java;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.IReflectiveModel;
import org.junit.Test;
public class JavaModelTests {

	private static Object  string  = "foo";
	private static Object integer = 42;
	private static IModel javaModel = new JavaModel(Arrays.asList(string, integer));
	
	@Test
	public void allContentsReturnsBothModelElements() {
		assertEquals(Arrays.asList(string, integer), javaModel.allContents());
	}
	
	@Test
	public void getTypeNameOf() {
		assertEquals("java.lang.String",                      javaModel.getTypeNameOf(string));
		assertEquals("org.eclipse.emf.ecore.impl.EClassImpl", javaModel.getTypeNameOf(EcoreFactory.eINSTANCE.createEClass()));
	}
	
	@Test
	public void getPropertiesOf() throws EolModelElementTypeNotFoundException {
		final IReflectiveModel javaModel = new JavaModel(Arrays.asList((Object)new TestSubject()));
		
		assertEquals(Arrays.asList("foo", "bar", "baz"),
		            javaModel.getPropertiesOf(TestSubject.class.getCanonicalName()));
	}
	
	public static class TestSubject {

		public String getFoo() { return "foo"; }
		
		public void setFoo(String foo) {}
		
		public boolean isBar() { return true; }
		
		public Collection<?> getBaz() { return Collections.EMPTY_LIST; }
		
	}
}
