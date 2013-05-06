package codeOrchestra.lcs.views.elements.fileTree;

import java.io.File;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import codeOrchestra.lcs.project.LCSProject;

class FileTree extends ApplicationWindow {
	  /**
	   * FileTree constructor
	   */
	  public FileTree() {
	    super(null);
	  }

	  /**
	   * Runs the application
	   */
	  public void run() {
	    // Don't return from open() until window closes
	    setBlockOnOpen(true);

	    // Open the main window
	    open();

	    // Dispose the display
	    Display.getCurrent().dispose();
	  }

	  /**
	   * Configures the shell
	   * 
	   * @param shell
	   *            the shell
	   */
	  protected void configureShell(Shell shell) {
	    super.configureShell(shell);

	    // Set the title bar text and the size
	    shell.setText("File Tree");
	    shell.setSize(400, 400);
	  }

	  /**
	   * Creates the main window's contents
	   * 
	   * @param parent
	   *            the main window
	   * @return Control
	   */
	  protected Control createContents(Composite parent) {
	    Composite composite = new Composite(parent, SWT.NONE);
	    composite.setLayout(new GridLayout(1, false));

	    // Add a checkbox to toggle whether the labels preserve case
	    Button preserveCase = new Button(composite, SWT.CHECK);
	    preserveCase.setText("&Preserve case");

	    // Create the tree viewer to display the file tree
	    final TreeViewer tv = new TreeViewer(composite);
	    tv.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
	    File rootDir = null;
	    LCSProject currProject = LCSProject.getCurrentProject();
	    if (null!=currProject) {
	    	rootDir = currProject.getOutputDir();
	    }
	    tv.setContentProvider(new FileTreeContentProvider(rootDir));
	    tv.setLabelProvider(new FileTreeLabelProvider());
	    tv.setInput("root"); // pass a non-null that will be ignored

	    // When user checks the checkbox, toggle the preserve case attribute
	    // of the label provider
	    preserveCase.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	        boolean preserveCase = ((Button) event.widget).getSelection();
	        FileTreeLabelProvider ftlp = (FileTreeLabelProvider) tv
	            .getLabelProvider();
	        ftlp.setPreserveCase(preserveCase);
	      }
	    });
	    return composite;
	  }

	}