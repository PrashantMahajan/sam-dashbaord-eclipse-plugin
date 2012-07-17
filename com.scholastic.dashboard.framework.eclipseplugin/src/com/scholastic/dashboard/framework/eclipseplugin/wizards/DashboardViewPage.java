package com.scholastic.dashboard.framework.eclipseplugin.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.scholastic.dashboard.framework.eclipseplugin.commandparser.CommandParserController;
import com.scholastic.dashboard.framework.exceptionhandler.ExceptionController;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (js).
 */

public class DashboardViewPage extends WizardPage {
	private Text g_txtProject;
	private Text g_txtCommand;
	private Text g_txtModelName;
	private Text g_txtExceptions;
	private ISelection g_objSelection;
	public static DashboardViewPage FileGenerateForm = null;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public DashboardViewPage(ISelection selection) {
		super("wizardPage");
		this.setTitle("Create new Dashboard View");
		this.setDescription("This wizard helps you to create a new View for the Dashbaord.");
		this.g_objSelection = selection;
		DashboardViewPage.FileGenerateForm = this;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite prm_objParent) {
		Button v_objCreateFiles;
		Label v_ctlLabel;
		GridLayout v_ctlLayout;
		Composite v_ctlContainer;

		try {
			v_ctlContainer = new Composite(prm_objParent, SWT.NULL);
			v_ctlLayout = new GridLayout();
			v_ctlContainer.setLayout(v_ctlLayout);
			v_ctlLayout.numColumns = 2;

			Button button = new Button(v_ctlContainer, SWT.PUSH);
			button.setText("Browse Project...");
			button.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					handleBrowse();
				}
			});

			this.g_txtProject = new Text(v_ctlContainer, SWT.BORDER | SWT.SINGLE);
			this.g_txtProject.setLayoutData(new GridData(513, 20));
			this.g_txtProject.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					dialogChanged();
				}
			});

			v_ctlLabel = new Label(v_ctlContainer, SWT.NULL);
			v_ctlLabel.setText("Model Name:");
			this.g_txtModelName = new Text(v_ctlContainer, SWT.SINGLE | SWT.BORDER | SWT.WRAP);
			this.g_txtModelName.setLayoutData(new GridData(513, 20));
			v_ctlLabel = new Label(v_ctlContainer, SWT.NULL );
			v_ctlLabel.setText("Command: ");
			this.g_txtCommand = new Text(v_ctlContainer, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
			this.g_txtCommand.setLayoutData(new GridData(500, 400));
			
			v_ctlLabel = new Label(v_ctlContainer, SWT.NULL);
			v_ctlLabel.setText("");
			v_objCreateFiles = new Button(v_ctlContainer, SWT.PUSH);
			v_objCreateFiles.setText("Generate Files");
			v_objCreateFiles.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent prm_evtEvent) {
					generateFiles();
				}
			});

			v_ctlLabel = new Label(v_ctlContainer, SWT.NULL);
			v_ctlLabel.setText("Exceptions : ");
			this.g_txtExceptions = new Text(v_ctlContainer, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
			this.g_txtExceptions.setLayoutData(new GridData(500, 100));

			this.initialize();
			this.setControl(v_ctlContainer);
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
		}
	}
	public void log (String prm_slog) {
		this.g_txtExceptions.setText(this.g_txtExceptions.getText() + "\n" + prm_slog);
	}

	public String getContainerName() {
		return this.g_txtProject.getText();
	}
	public String getFileName() {
		return this.g_txtModelName.getText();
	}
	private void dialogChanged() {
		IResource container = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(new Path(getContainerName()));
		String fileName = getFileName();

		if (getContainerName().length() == 0) {
			updateStatus("File container must be specified");
			return;
		}
		if (container == null || (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus("File container must exist");
			return;
		}
		if (!container.isAccessible()) {
			updateStatus("Project must be writable");
			return;
		}
		if (fileName.length() == 0) {
			updateStatus("File name must be specified");
			return;
		}
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			updateStatus("File name must be valid");
			return;
		}
		int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = fileName.substring(dotLoc + 1);
			if (ext.equalsIgnoreCase("mpe") == false) {
				updateStatus("File extension must be \"mpe\"");
				return;
			}
		}
		updateStatus(null);
	}

	private void generateFiles () {
		try {
			CommandParserController.getInstane().generateFiles(this.g_txtModelName.getText(), this.g_txtCommand.getText(), ResourcesPlugin.getWorkspace().getRoot().getFullPath().toOSString() + this.g_txtProject.getText());
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
		}
	}
	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */

	private void handleBrowse() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
				"Select new file container");
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				this.g_txtProject.setText(this.getProjectPath());
			}
		}
	}
	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */
	private void initialize () {
		try {
			if (this.g_objSelection != null && this.g_objSelection.isEmpty() == false && this.g_objSelection instanceof IStructuredSelection) {
				IStructuredSelection ssel = (IStructuredSelection) this.g_objSelection;
				if (ssel.size() > 1)
					return;
				Object obj = ssel.getFirstElement();
				if (obj instanceof IResource) {
					IContainer container;
					if (obj instanceof IContainer)
						container = (IContainer) obj;
					else
						container = ((IResource) obj).getParent();
					this.g_txtProject.setText(container.getLocation().toString());
				}
			}
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
		}
	}
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
	private String getProjectPath () {
		String v_Return = "";
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
			Object firstElement = selection.getFirstElement();
			if (firstElement instanceof IAdaptable) {
				IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
				IPath path = project.getLocation();
				v_Return = path.toOSString();
			}
		}
		return v_Return;
	}
}