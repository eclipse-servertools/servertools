package org.eclipse.wst.server.ui.internal.publish;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 *
 **********************************************************************/
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressIndicator;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IPublishListener;
import org.eclipse.wst.server.core.IPublishStatus;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.internal.ContextIds;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * A modal dialog for publishing.
 */
public class PublishDialog extends Dialog implements IRunnableContext {
	// name to use for task when normal task name is empty string.
	protected static final String DEFAULT_TASKNAME = JFaceResources.getString("ProgressMonitorDialog.message"); //$NON-NLS-1$

	// The progress indicator control.
	protected ProgressIndicator progressIndicator;

	// The label control for the task.
	protected Label taskLabel;

	// The label control for the subtask.
	protected Label subTaskLabel;

	// list of previous tasks
	protected ArrayList taskList;

	protected Table statusTable;
	protected TableItem currentTableItem = null;

	// buttons
	protected Button cancel;
	protected Button ok;
	protected Button detailsButton;

	protected Composite details;
	protected boolean detailsVisible;
	protected static IStatus globalStatus;

	protected boolean remainOpen = false;
	protected boolean showCancel = false;

	protected WorkbenchLabelProvider workbenchLabelProvider;

	// The progress monitor.
	protected ProgressMonitor progressMonitor = new ProgressMonitor();

	// The name of the current task (used by ProgressMonitor).
	protected String task;

	// The number of currently running runnables.
	private int runningRunnables;

	// The cursors used.
	private Cursor cancelArrowCursor;

	// The cursor used in the shell;
	private Cursor waitCursor;

	protected List publishInfo = new ArrayList();

	// Internal progress monitor implementation.
	private class ProgressMonitor implements IProgressMonitor {

		private String fSubTask= "";//$NON-NLS-1$
		private boolean fIsCanceled;

		public void beginTask(String name, int totalWork) {
			if (progressIndicator.isDisposed())
				return;

			if (name == null)
				task = "";//$NON-NLS-1$
			else
				task = name;

			String s = task;
			if (s.length() <= 0)
				s = DEFAULT_TASKNAME;
			taskLabel.setText(s);

			if (totalWork == UNKNOWN)
				progressIndicator.beginAnimatedTask();
			else
				progressIndicator.beginTask(totalWork);
		}

		public void done() {
			if (!progressIndicator.isDisposed()) {
				progressIndicator.sendRemainingWork();
				//progressIndicator.done();
				ok.setEnabled(true);
				ok.setFocus();
			}
		}

		public void setTaskName(final String name) {
			if (taskLabel.isDisposed())
				return;
			if (name == null)
				task = "";//$NON-NLS-1$
			else
				task = name;

			String s = task;
			if (s.length() <= 0)
				s= DEFAULT_TASKNAME;
			taskLabel.setText(s);
		}
	
		public boolean isCanceled() {
			return fIsCanceled;
		}

		public void setCanceled(boolean b) {
			fIsCanceled = b;
		}

		public void subTask(final String name) {
			if (subTaskLabel.isDisposed())
				return;
				
			if (name == null)
				fSubTask = "";//$NON-NLS-1$
			else
				fSubTask = name;

			subTaskLabel.setText(fSubTask);
		}

		public void worked(int work) {
			internalWorked(work);
		}

		public void internalWorked(double work) {
			if (!progressIndicator.isDisposed())
				progressIndicator.worked(work);
		}
	}

	/**
	 * Creates a progress monitor dialog under the given shell.
	 * The dialog has a standard title and no image. 
	 * <code>open</code> is non-blocking.
	 *
	 * @param parent the parent shell
	 */
	public PublishDialog(Shell parent, boolean keepOpen) {
		super(parent);
		setShellStyle(SWT.BORDER | SWT.TITLE | SWT.APPLICATION_MODAL | SWT.RESIZE); // no close button
		setBlockOnOpen(false);
		taskList = new ArrayList();
	
		remainOpen = keepOpen;
		if (!remainOpen) // part of a bigger task
			showCancel = true;
		
		workbenchLabelProvider = new WorkbenchLabelProvider();
	}

	/**
	 * Add a single publish event to the UI.
	 *
	 * @param status org.eclipse.core.runtime.IStatus
	 */
	protected void addPublishEvent(final IModule module, final IStatus status) {
		Trace.trace(Trace.FINEST, "PublishDialog.addPublishEvent: " + module + " " + status);
		if (status != null && !status.isOK())
			remainOpen = true;
	
		if (module == null) {
			if (status != null)
				globalStatus = status;
			return;
		}
	
		if (status == null) {
			getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					currentTableItem = new TableItem(statusTable, SWT.NONE);
					currentTableItem.setImage(new Image[] {ServerUICore.getLabelProvider().getImage(module)});
					currentTableItem.setText(new String[] {ServerUICore.getLabelProvider().getText(module), ServerUIPlugin.getResource("%publishingBusy")});
				}
			});
		} else {
			getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					currentTableItem.setImage(1, getStatusImage(status));
					currentTableItem.setText(1, getStatusText(status));
				}
			});
		}
	}

	/* (non-Javadoc)
	 * Method declared on Dialog.
	 * Handles the pressing of the Ok or Details button in this dialog.
	 * If the Ok button was pressed then close this dialog.  If the Details
	 * button was pressed then toggle the displaying of the error details area.
	 * Note that the Details button will only be visible if the error being
	 * displayed specifies child details.
	 */
	protected void buttonPressed(int id) {
		if (id == IDialogConstants.DETAILS_ID) // was the details button pressed?
			toggleDetailsArea();
		else
			super.buttonPressed(id);
	}
	
	/* (non-Javadoc)
	 * Method declared on Window.
	 */
	public boolean close() {
		if (runningRunnables <= 0) {
			workbenchLabelProvider.dispose();
		
			cancel.setCursor(null);
			getShell().setCursor(null);
			if (cancelArrowCursor != null)
				cancelArrowCursor.dispose();
			cancelArrowCursor = null;
	
			// Make sure that all events in the asynchronous event queue
			// are dispatched.
			Display display = getShell().getDisplay();
			display.syncExec(new Runnable() {
				public void run() {
					// do nothing
				}
			});
					
			// Stop event dispatching
			Cursor temp = waitCursor;
			waitCursor = null;
					
			// Force the event loop to return from sleep () so that
			// it stops event dispatching.
			display.asyncExec(null);
	
			try {
				synchronized (temp) {
					temp.notifyAll();
				}
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error notifying publish dialog closure", e);
			}
			if (temp != null)
				temp.dispose();
			return super.close();
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * Method declared in Window.
	 */
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(ServerUIPlugin.getResource("%publishingTitle"));
		if (waitCursor == null)
			waitCursor = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
		shell.setCursor(waitCursor);
	}
	
	/* (non-Javadoc)
	 * Method declared on Dialog.
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		ok = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
		//ok.setCursor(arrowCursor);
		ok.setEnabled(false);
		
		// cancel button
		cancel = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
		if (cancelArrowCursor == null)
			cancelArrowCursor = new Cursor(cancel.getDisplay(), SWT.CURSOR_ARROW);
		cancel.setCursor(cancelArrowCursor);
		cancel.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				remainOpen = true;
				cancel.setEnabled(false);
				progressMonitor.setCanceled(true);
			}
		});
	
		detailsButton = createButton(parent, IDialogConstants.DETAILS_ID, IDialogConstants.SHOW_DETAILS_LABEL, false);
		detailsButton.setEnabled(false);
		WorkbenchHelp.setHelp(detailsButton, ContextIds.PUBLISH_DETAILS_DIALOG_DETAILS_BUTTON);
	}
	
	/**
	 * Create this dialog's drop-down list component.
	 *
	 * @param parent the parent composite
	 * @return the drop-down list component
	 */
	protected Composite createDetails(Composite parent) {
		Color bg = getShell().getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
		Font font = JFaceResources.getFontRegistry().get(JFaceResources.BANNER_FONT);
	
		details = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		details.setLayout(layout);
	
		GridData data = new GridData(
			GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL |
			GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL);
		data.heightHint = 300;
		details.setLayoutData(data);
		
		// Create the title area which will contain
		// a title, message, and image.
		ScrolledComposite sc = new ScrolledComposite(details, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		data = new GridData(
			GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL |
			GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL);
		sc.setLayoutData(data);
	
		Composite infoArea = new Composite(sc, SWT.NONE);
		layout = new GridLayout();
		layout.marginHeight = 5;
		layout.verticalSpacing = 0;
		layout.numColumns = 2;
		infoArea.setLayout(layout);
		data = new GridData(
			GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL |
			GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL);
		infoArea.setLayoutData(data);
		infoArea.setBackground(bg);
		WorkbenchHelp.setHelp(infoArea, ContextIds.PUBLISH_DETAILS_DIALOG_DETAILS);
	
		// fill in the details
		if (globalStatus != null) {
		IStatus[] children = globalStatus.getChildren();
		if (children != null) {
			int size = children.length;
			for (int i = 0; i < size; i++) {
				IStatus status = children[i];
	
				if (status != null) {
	
				if (status instanceof IPublishStatus) {
					IPublishStatus ps = (IPublishStatus) status;
					createPublishStatusDetails(ps, infoArea);
				} else {
					String title = null;
					title = status.getMessage();
			
					Label label = new Label(infoArea, SWT.NONE);
					label.setBackground(bg);
	
					label = new Label(infoArea, SWT.NONE);
					label.setText(title);
					label.setFont(font);
					label.setBackground(bg);
	
					IStatus[] status2 = status.getChildren();
					if (status2 != null && status2.length > 0) {
						label = new Label(infoArea, SWT.NONE); // spacer
		
						StyledText styledText = new StyledText(infoArea, SWT.MULTI | SWT.READ_ONLY);
						styledText.setCursor(null);
						styledText.getCaret().setVisible(false);
						styledText.setBackground(bg);
	
						StringBuffer sb = new StringBuffer();
						int size2 = status2.length;
						for (int j = 0; j < size2; j++)
							if (status2[j] != null)
								sb.append(status2[j].getMessage() + "\n");
	
						styledText.setText(sb.toString());
						data = new GridData(GridData.FILL_HORIZONTAL);
						styledText.setLayoutData(data);
					}
	
					// add final status, indented
					label = new Label(infoArea, SWT.NONE); // spacer
		
					Composite statusComp = new Composite(infoArea, SWT.NONE);
					layout = new GridLayout();
					layout.numColumns = 2;
					layout.marginHeight = 4;
					layout.marginWidth = 0;
					layout.verticalSpacing = 0;
					layout.horizontalSpacing = 4;
					statusComp.setLayout(layout);
					statusComp.setBackground(bg);
	
					label = new Label(statusComp, SWT.NONE);
					label.setBackground(bg);
					label.setImage(getStatusImage(status));
	
					label = new Label(statusComp, SWT.NONE);
					label.setBackground(bg);
					label.setText(getStatusText(status));
				}
				}
			}
		}
		}
	
		detailsVisible = true;
	
		sc.setContent(infoArea);
		Point p = infoArea.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		sc.setMinHeight(p.y);
		sc.setMinWidth(p.x);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
	
		return details;
	}
	
	/* (non-Javadoc)
	 * Method declared on Dialog.
	 */
	protected Control createDialogArea(Composite parent) {
		Composite c = (Composite) super.createDialogArea(parent);
		((GridLayout) c.getLayout()).numColumns = 2;
		((GridData) c.getLayoutData()).widthHint = 450;
		WorkbenchHelp.setHelp(c, ContextIds.PUBLISH_DETAILS_DIALOG);
	
		// icon
		Label iconLabel = new Label(c, SWT.LEFT);
		GridData gd = new GridData();
		iconLabel.setLayoutData(gd);
		iconLabel.setFont(parent.getFont());
		Image i = iconLabel.getDisplay().getSystemImage(SWT.ICON_INFORMATION);
		if (i != null)
			iconLabel.setImage(i);
		else
			iconLabel.setText(JFaceResources.getString("Image_not_found")); //$NON-NLS-1$
	
		// label on right hand side of icon
		taskLabel = new Label(c, SWT.LEFT | SWT.WRAP);
		taskLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		taskLabel.setText(DEFAULT_TASKNAME);
		taskLabel.setFont(parent.getFont());
	
		statusTable = new Table(c, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY | SWT.FULL_SELECTION);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL);
		gd.heightHint = 55;
		gd.horizontalSpan = 2;
		statusTable.setLayoutData(gd);
		statusTable.setHeaderVisible(false);
		statusTable.setLinesVisible(false);
		WorkbenchHelp.setHelp(statusTable, ContextIds.PUBLISH_DETAILS_DIALOG_STATUS);
	
		// add columns
		TableColumn column = new TableColumn(statusTable, SWT.SINGLE);
		column.setWidth(175);
	
		column = new TableColumn(statusTable, SWT.SINGLE);
		column.setWidth(225);
	
		// progress indicator
		progressIndicator = new ProgressIndicator(c);
		gd = new GridData();
		gd.heightHint = 15;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 2;
		progressIndicator.setLayoutData(gd);
	
		// label showing current task
		subTaskLabel = new Label(c, SWT.LEFT | SWT.WRAP);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 40;	
		gd.horizontalSpan = 2;
		subTaskLabel.setLayoutData(gd);
		subTaskLabel.setFont(parent.getFont());
		
		Dialog.applyDialogFont(c);
	
		return c;
	}
	
	/**
	 * 
	 * @param comp org.eclipse.swt.widgets.Composite
	 */
	protected void createPublishStatusDetails(IPublishStatus status, Composite infoArea) {
		Color bg = getShell().getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
		Font font = JFaceResources.getFontRegistry().get(JFaceResources.BANNER_FONT);
	
		String title = null;
		Image image = null;
		title = status.getMessage();
		image = ServerUICore.getLabelProvider().getImage(status.getModule());
	
		Label label = new Label(infoArea, SWT.NONE);
		label.setImage(image);
		label.setBackground(bg);
	
		Composite comp = new Composite(infoArea, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 2;
		layout.marginWidth = 0;
		layout.verticalSpacing = 2;
		layout.horizontalSpacing = 0;
		comp.setLayout(layout);
		comp.setBackground(bg);
	
		label = new Label(comp, SWT.NONE);
		label.setText(title);
		label.setFont(font);
		label.setBackground(bg);
	
		IStatus[] status2 = status.getChildren();
		if (status2 != null) {
			int size2 = status2.length;
			for (int j = 0; j < size2; j++) {
				if (status2[j] != null) {
					label = new Label(infoArea, SWT.NONE); // spacer
		
					StyledText styledText = new StyledText(infoArea, SWT.MULTI | SWT.READ_ONLY);
					styledText.setCursor(null);
					styledText.getCaret().setVisible(false);
					styledText.setBackground(bg);
					String text = status2[j].getMessage();
					//if (!status2[j].isOK()) {
						IStatus[] children = status2[j].getChildren();
						if (children != null && children.length > 0)
							for (int k = 0; k < children.length; k++) {
								text += "\n\t" + children[k].getMessage();
							}
						styledText.setForeground(getStatusColor(status2[j]));
					//}
		
					styledText.setText(text);
					GridData data = new GridData(GridData.FILL_HORIZONTAL);
					styledText.setLayoutData(data);
				}
			}
		}
	
		// add final status, indented
		label = new Label(infoArea, SWT.NONE); // spacer
	
		Composite statusComp = new Composite(infoArea, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 4;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 4;
		statusComp.setLayout(layout);
		statusComp.setBackground(bg);
	
		label = new Label(statusComp, SWT.NONE);
		label.setBackground(bg);
		label.setImage(getStatusImage(status));
	
		label = new Label(statusComp, SWT.NONE);
		label.setBackground(bg);
		String text = getStatusText(status);
	
		long time = status.getTime();
		if (time > 5) {
			NumberFormat format = NumberFormat.getNumberInstance();
			format.setMinimumFractionDigits(2);
			format.setMaximumFractionDigits(2);
			text += " (" + ServerUIPlugin.getResource("%publishingTime", format.format(time / 1000.0)) + ")";
		}
	
		label.setText(text);
	}

	/**
	 * Returns the progress monitor to use for operations run in 
	 * this progress dialog.
	 *
	 * @return the progress monitor
	 */
	public IProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}
	
	/**
	 * 
	 * @return java.lang.String
	 * @param status org.eclipse.core.runtime.IStatus
	 */
	protected Color getStatusColor(IStatus status) {
		switch (status.getSeverity()) {
			case IStatus.OK:
				return getShell().getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND);
			case IStatus.INFO:
				return getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE);
			case IStatus.WARNING:
				return getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_MAGENTA);
			case IStatus.ERROR:
				return getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_RED);
		}
		return null;
	}
	
	/**
	 * 
	 * @return java.lang.String
	 * @param status org.eclipse.core.runtime.IStatus
	 */
	protected static Image getStatusImage(IStatus status) {
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		switch (status.getSeverity()) {
			case IStatus.OK:
				return sharedImages.getImage(ISharedImages.IMG_OBJS_INFO_TSK);
			case IStatus.INFO:
				return sharedImages.getImage(ISharedImages.IMG_OBJS_INFO_TSK);
			case IStatus.WARNING:
				return sharedImages.getImage(ISharedImages.IMG_OBJS_WARN_TSK);
			case IStatus.ERROR:
				return sharedImages.getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
		}
		return null;
	}
	
	/**
	 * 
	 * @return java.lang.String
	 * @param status org.eclipse.core.runtime.IStatus
	 */
	protected static String getStatusText(IStatus status) {
		if (status == null)
			return "n/a";
	
		switch (status.getSeverity()) {
			case IStatus.OK:
				return ServerUIPlugin.getResource("%publishingOK");
			case IStatus.INFO:
				return ServerUIPlugin.getResource("%publishingInfo");
			case IStatus.WARNING:
				return ServerUIPlugin.getResource("%publishingWarning");
			case IStatus.ERROR:
				return ServerUIPlugin.getResource("%publishingError");
		}
		return "getStatusText() invalid";
	}
	
	/**
	 * Publish to the given server instance control using the current publisher.
	 *
	 * @param control org.eclipse.wst.server.core.IServerControl
	 * @param keepOpen boolean
	 */
	public static IStatus publish(Shell shell, final IServer server2, boolean keepOpen) {
		globalStatus = null;
	
		// create listener
		IPublishListener listener = null;
	
		// publish the code
		try {
			final PublishDialog dialog = new PublishDialog(shell, keepOpen);
	
			listener = new IPublishListener() {
				public void moduleStateChange(IServer server, IModule[] parents, IModule module) {
					// do nothing
				}
	
				public void publishStarting(IServer server, List[] parents, IModule[] modules) {
					// do nothing
				}
				
				public void publishStarted(IServer server) {
					dialog.addPublishEvent(null, null);
				}
	
				public void moduleStarting(IServer server, IModule[] parents, IModule module) {
					dialog.addPublishEvent(module, null);
				}
	
				public void moduleFinished(IServer server, IModule[] parents, IModule module, IPublishStatus status) {
					dialog.addPublishEvent(module, status);
				}
	
				public void publishFinished(IServer server, IPublishStatus globalStatus2) {
					dialog.addPublishEvent(null, globalStatus2);
				}
			};
	
			server2.addPublishListener(listener);
	
			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					globalStatus = server2.publish(monitor);
					Trace.trace(Trace.FINEST, "Publishing done: " + globalStatus);
				}
			};
	
			dialog.run(true, true, runnable);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error in publishing dialog", e);
			return new Status(IStatus.ERROR, ServerUIPlugin.PLUGIN_ID, 0, ServerUIPlugin.getResource("%errorPublishing"), e);
		} finally {
			server2.removePublishListener(listener);
		}
	
		return globalStatus;
	}
	
	/* (non-Javadoc)
	 * Method declared on IRunnableContext.
	 * Runs the given <code>IRunnableWithProgress</code> with the progress monitor for this
	 * progress dialog.  The dialog is opened before it is run, and closed after it completes.
	 */
	public void run(boolean fork, boolean cancelable, IRunnableWithProgress runnable) throws InvocationTargetException, InterruptedException {
		open();
		try {
			runningRunnables++;
			ModalContext.run(runnable, fork, getProgressMonitor(), getShell().getDisplay());
		} finally {	
			runningRunnables--;
			if (remainOpen || ServerUICore.getPreferences().getShowPublishingDetails()) {
				try {
					updateTaskLabel();
					cancel.setEnabled(showCancel);
					ok.setEnabled(true);
					detailsButton.setEnabled(true);
					subTaskLabel.setText("");
					progressIndicator.setEnabled(false);
					getShell().setCursor(null);
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Error reseting publishing dialog", e);
				}
			} else
				close();
	
			if (waitCursor != null)
				waitForClose();
		}
	}
	
	/**
	 * Toggles the unfolding of the details area.  This is triggered by
	 * the user pressing the details button.
	 */
	private void toggleDetailsArea() {
		Point windowSize = getShell().getSize();
		Point windowComputeSize = getContents().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		
		if (detailsVisible) {
			details.dispose();
			detailsVisible = false;
			detailsButton.setText(IDialogConstants.SHOW_DETAILS_LABEL);
		} else {
			details = createDetails((Composite)getContents());
			detailsButton.setText(IDialogConstants.HIDE_DETAILS_LABEL);
		}
	
		Point newSize = getContents().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		int height = Math.max(newSize.y, windowSize.y + (newSize.y - windowComputeSize.y));
		
		getShell().setSize(new Point(windowSize.x, height));
	}
	
	/**
	 * 
	 */
	protected void updateTaskLabel() {
		if (getProgressMonitor().isCanceled())
			taskLabel.setText(ServerUIPlugin.getResource("%publishingCancelled"));
		else
			taskLabel.setText(getStatusText(globalStatus));
	}
	
	/**
	 * 
	 */
	private void waitForClose() {
		// keep the dialog open...
		
		Display display = getShell().getDisplay();
		if (display == Display.getCurrent()) {
			while (waitCursor != null) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		}
	}
}
