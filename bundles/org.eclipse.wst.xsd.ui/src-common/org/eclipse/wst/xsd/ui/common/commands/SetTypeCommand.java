/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.common.commands;

import org.eclipse.jface.window.Window;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSpecification;
import org.eclipse.wst.xsd.adt.edit.ComponentReferenceEditManager;
import org.eclipse.wst.xsd.adt.edit.IComponentDialog;
import org.eclipse.wst.xsd.editor.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.common.actions.SetTypeAction;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;

public class SetTypeCommand extends BaseCommand
{
  XSDConcreteComponent parent;
  private boolean continueApply;
  XSDBaseAdapter adapter;
  String action;

  public SetTypeCommand(String label, String ID, XSDConcreteComponent parent)
  {
    super(label);
    this.parent = parent;
    this.action = ID;
  }
  
  public void setAdapter(XSDBaseAdapter adapter)
  {
    this.adapter = adapter;
  }

  public void execute()
  {
    ComponentReferenceEditManager componentReferenceEditManager = getComponentReferenceEditManager();
    continueApply = true; 
    if (parent instanceof XSDElementDeclaration)
    {
      if (action.equals(SetTypeAction.SET_NEW_TYPE_ID))
      {
        ComponentSpecification newValue = (ComponentSpecification)invokeDialog(componentReferenceEditManager.getNewDialog());
        if (continueApply)
          componentReferenceEditManager.modifyComponentReference(adapter, newValue);
      }
      else
      {
        ComponentSpecification newValue = (ComponentSpecification)invokeDialog(componentReferenceEditManager.getBrowseDialog());
        if (continueApply)
          componentReferenceEditManager.modifyComponentReference(adapter, newValue);
      }
      formatChild(parent.getElement());
    }

  }

  private Object invokeDialog(IComponentDialog dialog)
  {
    Object newValue = null;

    if (dialog == null)
    {
      return null;
    }

    if (dialog.createAndOpen() == Window.OK)
    {
      newValue = dialog.getSelectedComponent();
    }
    else
    {
      continueApply = false;
    }

    return newValue;
  }

  protected ComponentReferenceEditManager getComponentReferenceEditManager()
  {
    ComponentReferenceEditManager result = null;
    IEditorPart editor = getActiveEditor();
    if (editor != null)
    {
      result = (ComponentReferenceEditManager)editor.getAdapter(ComponentReferenceEditManager.class);
    }  
    return result;
  }
  
  private IEditorPart getActiveEditor()
  {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
    IEditorPart editorPart = workbenchWindow.getActivePage().getActiveEditor();
    return editorPart;
  }    

}
