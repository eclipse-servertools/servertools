/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.ITask;
import org.eclipse.wst.server.core.ITaskModel;
import org.eclipse.wst.server.core.util.TaskModel;
import org.eclipse.wst.server.ui.internal.EclipseUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.wizard.page.WorkspaceRunnableAdapter;
/**
 * A wizard used to execute tasks.
 */
public class TaskWizard implements IWizard {
	private static final byte FINISH = 2;
	private static final byte CANCEL = 3;

	private List pages;
	private boolean addingPages;
	private Map fragmentData = new HashMap();
	protected ITaskModel taskModel = new TaskModel();
	
	private IWizardContainer container = null;
	
	private boolean needsProgressMonitor = false;
	
	private boolean forcePreviousAndNextButtons = false;

	private boolean isHelpAvailable = false;
	
	private Image defaultImage = null;
	
	private RGB titleBarColor = null;

	private String windowTitle = null;
	
	private IDialogSettings dialogSettings = null;
	
	private WizardFragment rootFragment;
	private WizardFragment currentFragment;
	
	private static TaskWizard current;

	class FragmentData {
		public TaskWizardPage page;
		public ITask finishTask;
		public ITask cancelTask;
		
		public FragmentData(WizardFragment fragment) {
			finishTask = fragment.createFinishTask();
			if (finishTask != null)
				finishTask.setTaskModel(taskModel);
			
			cancelTask = fragment.createCancelTask();
			if (cancelTask != null)
				cancelTask.setTaskModel(taskModel);
		}
	}
	
	/**
	 * TaskWizard constructor comment.
	 */
	public TaskWizard() {
		super();
		
		setNeedsProgressMonitor(true);
		setForcePreviousAndNextButtons(true);
		
		current = this;
	}

	/**
	 * TaskWizard constructor comment.
	 */
	public TaskWizard(WizardFragment rootFragment) {
		this();
		this.rootFragment = rootFragment;
	}
	
	public TaskWizard(String title) {
		this();
		setWindowTitle(title);
	}
	
	public TaskWizard(String title, WizardFragment rootFragment) {
		this(rootFragment);
		setWindowTitle(title);
	}
	
	public void setRootFragment(WizardFragment rootFragment) {
		this.rootFragment = rootFragment;
	}
	
	public WizardFragment getRootFragment() {
		return rootFragment;
	}

	/**
	 * Cancel the client selection.
	 *
	 * @return boolean
	 */
	public boolean performCancel() {
		final List list = getAllWizardFragments();
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					Iterator iterator = list.iterator();
					while (iterator.hasNext())
						executeTask((WizardFragment) iterator.next(), CANCEL, monitor);
				} catch (CoreException ce) {
					throw new InvocationTargetException(ce);
				}
			}
		};
		
		Throwable t = null;
		try {
			if (getContainer() != null)
				getContainer().run(true, true, runnable);
			else
				runnable.run(new NullProgressMonitor());
			return true;
		} catch (InvocationTargetException te) {
			t = te.getCause();
		} catch (Exception e) {
			t = e;
		}
		Trace.trace(Trace.SEVERE, "Error cancelling task wizard", t);
		
		if (t instanceof CoreException) {
			EclipseUtil.openError(t.getLocalizedMessage(), ((CoreException)t).getStatus());
		} else
			EclipseUtil.openError(t.getLocalizedMessage());
		
		return false;
		
	}

	public boolean performFinish() {
		if (currentFragment != null)
			currentFragment.exit();
		
		final WizardFragment cFragment = currentFragment;

		final List list = getAllWizardFragments();
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				// enter & exit the remaining pages
				int index = list.indexOf(cFragment);
				while (index > 0 && index < list.size() - 1) {
					WizardFragment fragment = (WizardFragment) list.get(++index);
					try {
						fragment.enter();
						fragment.exit();
					} catch (Exception e) {
						Trace.trace(Trace.WARNING, "Could not enter/exit page", e);
					}
				}
				
				if (useJob()) {
					class FinishWizardJob extends Job {
						public FinishWizardJob() {
							super(getJobTitle());
						}

						public IStatus run(IProgressMonitor monitor2) {
							try {
								Iterator iterator = list.iterator();
								while (iterator.hasNext())
									executeTask((WizardFragment) iterator.next(), FINISH, monitor2);
							} catch (CoreException ce) {
								Trace.trace(Trace.SEVERE, "Error finishing wizard job", ce);
								return new Status(IStatus.ERROR, ServerUIPlugin.PLUGIN_ID, 0, ce.getLocalizedMessage(), null);
							}
							return new Status(IStatus.OK, ServerUIPlugin.PLUGIN_ID, 0, "", null);
						}
					}
					
					FinishWizardJob job = new FinishWizardJob();
					job.setUser(true);
					job.schedule();
				} else {
					Iterator iterator = list.iterator();
					while (iterator.hasNext())
						executeTask((WizardFragment) iterator.next(), FINISH, monitor);
				}
			}
		};
		
		Throwable t = null;
		try {
			if (getContainer() != null)
				getContainer().run(true, true, new WorkspaceRunnableAdapter(runnable));
			else
				runnable.run(new NullProgressMonitor());
			return true;
		} catch (InvocationTargetException te) {
			Trace.trace(Trace.SEVERE, "Error finishing task wizard", te);
			t = te.getCause();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error finishing task wizard 2", e);
			t = e;
		}
		
		if (t instanceof CoreException) {
			EclipseUtil.openError(t.getLocalizedMessage(), ((CoreException)t).getStatus());
		} else
			EclipseUtil.openError(t.getLocalizedMessage());
		
		return false;
	}
	
	public void addPage(IWizardPage page) {
		pages.add(page);
		page.setWizard(this);
	}
	
	protected void executeTask(WizardFragment fragment, byte type, IProgressMonitor monitor) throws CoreException {
		if (fragment == null)
			return;
		
		FragmentData data = getFragmentData(fragment);
		if (type == FINISH && data.finishTask != null)
			data.finishTask.execute(monitor);
		else if (type == CANCEL && data.cancelTask != null)
			data.cancelTask.execute(monitor);
	}
	
	protected WizardFragment getCurrentWizardFragment() {
		return currentFragment;
	}
	
	protected void switchWizardFragment(WizardFragment newFragment) {
		List list = getAllWizardFragments();
		int oldIndex = list.indexOf(currentFragment);
		int newIndex = list.indexOf(newFragment);
		if (oldIndex == newIndex)
			return;
		
		//safeExecuteTask(currentFragment, DEPARTURE);
		if (currentFragment != null)
			currentFragment.exit();
		
		if (oldIndex < newIndex)
			oldIndex ++;
		else
			oldIndex --;
		
		while (oldIndex != newIndex) {
			WizardFragment fragment = (WizardFragment) list.get(oldIndex);
			//safeExecuteTask(fragment, ARRIVAL);
			//safeExecuteTask(fragment, DEPARTURE);
			fragment.enter();
			fragment.exit();
			if (oldIndex < newIndex)
				oldIndex ++;
			else
				oldIndex --;
		}
		
		currentFragment = newFragment;
		//safeExecuteTask(currentFragment, ARRIVAL);
		currentFragment.enter();
	}
	
	protected List getAllWizardFragments() {
		List list = new ArrayList();
		list.add(rootFragment);
		addSubWizardFragments(rootFragment, list);
		
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			WizardFragment fragment = (WizardFragment) iterator.next();
			if (!taskModel.equals(fragment.getTaskModel()))
				fragment.setTaskModel(taskModel);
		}
		return list;
	}

	protected void addSubWizardFragments(WizardFragment fragment, List list) {
		Iterator iterator = fragment.getChildFragments().iterator();
		while (iterator.hasNext()) {
			WizardFragment child = (WizardFragment) iterator.next();
			list.add(child);
			addSubWizardFragments(child, list);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		if (addingPages)
			return;
		
		try {
			addingPages = true;
			pages = new ArrayList();
			Iterator iterator = getAllWizardFragments().iterator();
			while (iterator.hasNext()) {
				WizardFragment fragment = (WizardFragment) iterator.next();
				FragmentData data = getFragmentData(fragment);
				if (fragment.hasComposite()) {
					if (data.page != null)
						addPage(data.page);
					else {
						TaskWizardPage page = new TaskWizardPage(fragment);
						data.page = page;
						addPage(page);
					}
				}	
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error adding fragments to wizard", e);
		} finally {
			addingPages = false;
		}
	}
	
	protected static void updateWizardPages() {
		try {
			current.updatePages();
			current.getContainer().updateButtons();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error updating wizard pages", e);
		}
	}
	
	protected FragmentData getFragmentData(WizardFragment fragment) {
		try {
			FragmentData data = (FragmentData) fragmentData.get(fragment);
			if (data != null)
				return data;
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error getting fragment data", e);
		}
		
		FragmentData data = new FragmentData(fragment);
		fragmentData.put(fragment, data);
		return data;
	}
	
	protected void updatePages() {
		addPages();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#canFinish()
	 */
	public boolean canFinish() {
		// Default implementation is to check if all pages are complete.
		for (int i= 0; i < pages.size(); i++) {
			if (!((IWizardPage)pages.get(i)).isPageComplete())
				return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#createPageControls(org.eclipse.swt.widgets.Composite)
	 */
	public void createPageControls(Composite pageContainer) {
		// the default behavior is to create all the pages controls
		for (int i = 0; i < pages.size(); i++){
			IWizardPage page = (IWizardPage) pages.get(i);
			page.createControl(pageContainer);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#dispose()
	 */
	public void dispose() {
		// notify pages
		for (int i = 0; i < pages.size(); i++){
			((IWizardPage)pages.get(i)).dispose();
		}

		// dispose of image
		if (defaultImage != null) {
			defaultImage.dispose();
			defaultImage = null;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#getContainer()
	 */
	public IWizardContainer getContainer() {
		return container;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#getDefaultPageImage()
	 */
	public Image getDefaultPageImage() {
		return defaultImage;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#getDialogSettings()
	 */
	public IDialogSettings getDialogSettings() {
		return dialogSettings;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	public IWizardPage getNextPage(IWizardPage page) {
		int index = pages.indexOf(page);
		if (index == pages.size() - 1 || index == -1)
			// last page or page not found
			return null;
		
		return (IWizardPage)pages.get(index + 1);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#getPage(java.lang.String)
	 */
	public IWizardPage getPage(String name) {
		for (int i= 0; i < pages.size(); i++) {
			IWizardPage page = (IWizardPage)pages.get(i);
			String pageName = page.getName();
			if (pageName.equals(name))
				return page;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#getPageCount()
	 */
	public int getPageCount() {
		return pages.size();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#getPages()
	 */
	public IWizardPage[] getPages() {
		return (IWizardPage[])pages.toArray(new IWizardPage[pages.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#getPreviousPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	public IWizardPage getPreviousPage(IWizardPage page) {
		int index = pages.indexOf(page);
		if (index == 0 || index == -1)
			// first page or page not found
			return null;
		return (IWizardPage)pages.get(index - 1);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#getStartingPage()
	 */
	public IWizardPage getStartingPage() {
		if (pages.size() == 0)
			return null;
		
		return (IWizardPage) pages.get(0);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#getTitleBarColor()
	 */
	public RGB getTitleBarColor() {
		return titleBarColor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#getWindowTitle()
	 */
	public String getWindowTitle() {
		return windowTitle;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#isHelpAvailable()
	 */
	public boolean isHelpAvailable() {
		return isHelpAvailable;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#needsPreviousAndNextButtons()
	 */
	public boolean needsPreviousAndNextButtons() {
		return forcePreviousAndNextButtons || pages.size() > 1;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#needsProgressMonitor()
	 */
	public boolean needsProgressMonitor() {
		return needsProgressMonitor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#setContainer(org.eclipse.jface.wizard.IWizardContainer)
	 */
	public void setContainer(IWizardContainer wizardContainer) {
		this.container = wizardContainer;
	}
	
	public void setDialogSettings(IDialogSettings settings) {
		dialogSettings = settings;
	}
	
	public void setNeedsProgressMonitor(boolean b) {
		needsProgressMonitor = b;
	}
	
	public void setForcePreviousAndNextButtons(boolean b) {
		forcePreviousAndNextButtons = b;
	}
	
	public void setWindowTitle(String title) {
		windowTitle = title;
	}

	protected boolean useJob() {
		return false;
	}

	protected String getJobTitle() {
		return getWindowTitle();
	}
}