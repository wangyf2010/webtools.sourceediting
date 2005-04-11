/*
 * Copyright (c) 2002 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *   IBM - Initial API and implementation
 *   Jens Lukowski/Innoopract - initial renaming/restructuring
 * 
 */
package org.eclipse.wst.xml.core.internal.contentmodel.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * 
 * 
 */
public class CMDocumentFactoryRegistry 
{
	protected static CMDocumentFactoryRegistry instance;

	private static String DEFAULT_RESOURCE_TYPE = "*";

	protected Map resourceTypeMap = new HashMap();

	protected Vector documentBuilderList = new Vector();

	public CMDocumentFactoryRegistry() {
	}

	public void putFactory(String resourceType, CMDocumentFactoryDescriptor factoryDescriptor) {
		resourceTypeMap.put(resourceType, factoryDescriptor);
	}

	public CMDocumentFactory getFactory(String resourceType) {
		CMDocumentFactoryDescriptor factoryDescriptor = null;
		if (resourceType != null) {
			factoryDescriptor = (CMDocumentFactoryDescriptor) resourceTypeMap.get(resourceType);
		}
		if (factoryDescriptor == null) {
			// (dmw) issue: a default type of '*' means what? registered as
			// '*' is the way this works now. is that the intent? Or should it
			// mean registered as any other type?
			factoryDescriptor = (CMDocumentFactoryDescriptor) resourceTypeMap.get(DEFAULT_RESOURCE_TYPE);
		}
		return factoryDescriptor != null ? factoryDescriptor.getFactory() : null;
	}
}
