/* Copyright 2009 Requirements Management System  
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mags.remas.view.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TextAreaMessageDialog extends MessageDialog {
  private Text textAreaControl;
  private String textArea;

  public String getTextArea() {
    return textArea;
  }

  public void setTextArea(String textArea) {
    this.textArea = textArea;
  }

  public TextAreaMessageDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex,
      Throwable throwable) {
    super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels, defaultIndex);
  }

  public static boolean openErrorAndQuestion(Shell parent, String title, String message, String textArea, Throwable throwable) {
    TextAreaMessageDialog dialog = new TextAreaMessageDialog(parent, title, null, message, ERROR, new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 0, throwable);
    dialog.setShellStyle(dialog.getShellStyle() | SWT.SHEET | SWT.RESIZE);
    dialog.setTextArea(textArea);
    dialog.open();
    return dialog.open() == 0;
  }

  public static void openError(Shell parent, String title, String message, String textArea) {
    TextAreaMessageDialog dialog = new TextAreaMessageDialog(parent, title, null, message, ERROR, new String[] { IDialogConstants.OK_LABEL }, 0, null);
    dialog.setShellStyle(dialog.getShellStyle() | SWT.SHEET | SWT.RESIZE);
    dialog.setTextArea(textArea);
    dialog.open();
  }
  
  public static void openError(Shell parent, String title, String message, String textArea, Throwable throwable) {
    TextAreaMessageDialog dialog = new TextAreaMessageDialog(parent, title, null, message, ERROR, new String[] { IDialogConstants.OK_LABEL }, 0, throwable);
    dialog.setShellStyle(dialog.getShellStyle() | SWT.SHEET | SWT.RESIZE);
    dialog.setTextArea(textArea);
    dialog.open();
  }

  @Override
  protected Control createCustomArea(Composite parent) {
    super.createCustomArea(parent);
    textAreaControl = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.READ_ONLY);
    GridData gridData = new GridData(GridData.FILL_BOTH);
    gridData.horizontalSpan = 2;
    gridData.heightHint = 150;
    textAreaControl.setLayoutData(gridData);
    textAreaControl.setText(textArea == null ? "" : textArea);
    return textAreaControl;
  }
}