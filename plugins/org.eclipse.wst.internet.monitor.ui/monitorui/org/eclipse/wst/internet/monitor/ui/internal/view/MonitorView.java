/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.ui.internal.view;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.*;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wst.internet.monitor.core.*;
import org.eclipse.wst.internet.monitor.core.internal.http.ResendHTTPRequest;
import org.eclipse.wst.internet.monitor.ui.internal.*;
/**
 * View of TCP/IP activity.
 */
public class MonitorView extends ViewPart {
	protected Tree tree;
	protected TreeViewer treeViewer;
	protected MonitorTreeContentProvider contentProvider;

	protected IRequestListener listener;
	protected IViewerManager vm;
	protected List requestViewers;
	protected List responseViewers;

	protected static SimpleDateFormat format = new SimpleDateFormat(MonitorUIPlugin.getResource("%viewDateFormat"));
	protected static final String VIEW_ID = "org.eclipse.wst.internet.monitor.view";
	protected static final String DEFAULT_VIEWER = "org.eclipse.wst.internet.monitor.viewers.byteviewer";

	protected IAction httpHeaderAction;
	
	public static MonitorView view;
	
	protected Request currentRequest = null;
	protected StructuredSelection currentSelection = null;

	/**
	 * MonitorView constructor comment.
	 */
	public MonitorView() {
		super();
		view = this;
	}

	public void doRequestAdded(final Request rr) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!(rr instanceof ResendHTTPRequest)) {
				  Integer in = new Integer(rr.getLocalPort());
				  treeViewer.add(MonitorTreeContentProvider.ROOT, in);
				  treeViewer.add(in, rr); 
				  treeViewer.setSelection(new StructuredSelection(rr), true);
				}
			}
		});
	}

	public void doRequestChanged(final Request rr) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) treeViewer.getSelection();
				
				treeViewer.refresh(rr);
				if (!sel.isEmpty())
					treeViewer.setSelection(sel);
			}
		});
	}

	/**
	 * Clear the view.
	 */
	protected void clear() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				treeViewer.setSelection(null);
				treeViewer.setInput(MonitorTreeContentProvider.ROOT);
			}
		});
	}
	
	protected void setSelection(Request request) {
		if (treeViewer != null)
			treeViewer.setSelection(new StructuredSelection(request));
	}

	/**
	 * Returns the inner component in a desktop part.
	 */
	public void createPartControl(Composite parent) {
		SashForm sashFparent = new SashForm(parent, SWT.VERTICAL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.horizontalSpacing = 4;
		layout.verticalSpacing = 4;
		sashFparent.setLayout(layout);
		sashFparent.setLayoutData(new GridData(GridData.FILL_BOTH));
		PlatformUI.getWorkbench().getHelpSystem().setHelp(sashFparent, ContextIds.VIEW);
		
		// create tree panel
		Composite treePanel = new Composite(sashFparent, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		treePanel.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.heightHint = 110;
		data.horizontalSpan = 2;
		treePanel.setLayoutData(data);
	
		tree = new Tree(treePanel, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE);
		data = new GridData(GridData.FILL_BOTH);
		//data.widthHint = 120;
		tree.setLayoutData(data);
		treeViewer = new TreeViewer(tree);
		contentProvider = new MonitorTreeContentProvider();
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setInput(MonitorTreeContentProvider.ROOT);
		treeViewer.setLabelProvider(new TreeLabelProvider());

		PlatformUI.getWorkbench().getHelpSystem().setHelp(tree, ContextIds.VIEW_TREE);
	
		Composite detailsPanel = new Composite(treePanel, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 2;
		layout.marginWidth = 0;
		detailsPanel.setLayout(layout);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		data.widthHint = 200;
		detailsPanel.setLayoutData(data);

		final Label label = new Label(detailsPanel, SWT.NONE);
		label.setText(MonitorUIPlugin.getResource("%viewTime", ""));
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		final Label label2 = new Label(detailsPanel, SWT.NONE);
		label2.setText(MonitorUIPlugin.getResource("%viewResponseTime", ""));
		label2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		final Label label3 = new Label(detailsPanel, SWT.NONE);
		label3.setText(MonitorUIPlugin.getResource("%viewType", ""));
		label3.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		// create center and right panels
		SashForm sashFchild = new SashForm(sashFparent, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.horizontalSpacing = 2;
		layout.verticalSpacing = 4;
		sashFchild.setLayout(layout);
		sashFparent.setWeights(new int[] { 30, 70 });
	
		// request panel
		Composite request = new Composite(sashFchild, SWT.NONE);
		layout = new GridLayout();
		layout.verticalSpacing = 3;
		layout.marginHeight = 2;
		layout.marginWidth = 0;
		request.setLayout(layout);
		request.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite requestHeader = new Composite(request, SWT.NONE);
		layout = new GridLayout();
		layout.verticalSpacing = 0;
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 2;
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.heightHint = 30;
		requestHeader.setLayout(layout);
		requestHeader.setLayoutData(data);

		Composite requestHeaderLeft = new Composite(requestHeader, SWT.NONE);
		layout = new GridLayout();
		layout.verticalSpacing = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		data.heightHint = 30;
		requestHeaderLeft.setLayout(layout);
		requestHeaderLeft.setLayoutData(data);

		Label empty1 = new Label(requestHeaderLeft, SWT.NONE);
		empty1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		
		final Label requestLabel = new Label(requestHeaderLeft, SWT.NONE);
		requestLabel.setText(MonitorUIPlugin.getResource("%viewRequest", ""));
		requestLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_END));

		final Label requestSizeLabel = new Label(requestHeaderLeft, SWT.NONE);
		requestSizeLabel.setText(MonitorUIPlugin.getResource("%viewSize", ""));
		requestSizeLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_END));

		Composite requestHeaderRight = new Composite(requestHeader, SWT.NONE);
		layout = new GridLayout();
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.FILL_VERTICAL);
		data.heightHint = 30;
		requestHeaderRight.setLayout(layout);
		requestHeaderRight.setLayoutData(data);
		
		Label empty2 = new Label(requestHeaderRight, SWT.NONE);
		empty2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		
		Combo requestViewerCombo = new Combo(requestHeaderRight, SWT.DROP_DOWN | SWT.READ_ONLY);	
		requestViewerCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_END));

		// response panel
		Composite response = new Composite(sashFchild, SWT.NONE);
		layout = new GridLayout();
		layout.verticalSpacing = 3;
		layout.marginHeight = 2;
		layout.marginWidth = 0;
		response.setLayout(layout);
		response.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite responseHeader = new Composite(response, SWT.NONE);
		layout = new GridLayout();
		layout.verticalSpacing = 0;
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 2;
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.heightHint = 30;
		responseHeader.setLayout(layout);
		responseHeader.setLayoutData(data);

		Composite responseHeaderLeft = new Composite(responseHeader, SWT.NONE);
		layout = new GridLayout();
		layout.verticalSpacing = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		data.heightHint = 30;
		responseHeaderLeft.setLayout(layout);
		responseHeaderLeft.setLayoutData(data);

		Label empty3 = new Label(responseHeaderLeft, SWT.NONE);
		empty3.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
	
		final Label responseLabel = new Label(responseHeaderLeft, SWT.NONE);
		responseLabel.setText(MonitorUIPlugin.getResource("%viewResponse", ""));
		responseLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
	
		final Label responseSizeLabel = new Label(responseHeaderLeft, SWT.NONE);
		responseSizeLabel.setText(MonitorUIPlugin.getResource("%viewSize", ""));
		responseSizeLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_END));

		Composite responseHeaderRight = new Composite(responseHeader, SWT.NONE);
		layout = new GridLayout();
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.FILL_VERTICAL);
		data.heightHint = 30;
		responseHeaderRight.setLayout(layout);
		responseHeaderRight.setLayoutData(data);
		
		Label empty4 = new Label(responseHeaderRight, SWT.NONE);
		empty4.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		
		Combo responseViewerCombo = new Combo(responseHeaderRight, SWT.DROP_DOWN | SWT.READ_ONLY);	
		responseViewerCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_END));
	
		// viewer manager
		vm = new ViewerManager(request, request, response, response);
		requestViewers = vm.getRequestViewers();
		responseViewers = vm.getResponseViewers();

		// set up the viewer combo boxes
		Iterator iterator = requestViewers.iterator();
		int ctr = 0;
		while (iterator.hasNext()) {
			IConfigurationElement element = (IConfigurationElement) iterator.next();
			requestViewerCombo.add(element.getAttribute("label"), ctr);
			if (element.getAttribute("id").equals(DEFAULT_VIEWER)) {
				requestViewerCombo.select(ctr); 
				vm.setRequestViewer(element);
			}  
			ctr++;	
		}
		requestViewerCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				Combo rvCombo = (Combo) arg0.getSource();
				vm.setRequestViewer((IConfigurationElement) requestViewers.get(rvCombo.getSelectionIndex()));
			}
		});
		requestHeader.layout(true);
		
		iterator = responseViewers.iterator();
		ctr = 0;
		while(iterator.hasNext()) {
			IConfigurationElement element = (IConfigurationElement) iterator.next();
			responseViewerCombo.add(element.getAttribute("label"), ctr);
			if(element.getAttribute("id").equals(DEFAULT_VIEWER)) {
				responseViewerCombo.select(ctr); 
				vm.setResponseViewer(element);
			} 
			ctr++;
		}
		responseViewerCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				Combo rvCombo = (Combo) arg0.getSource();
				vm.setResponseViewer((IConfigurationElement) requestViewers.get(rvCombo.getSelectionIndex()));
			}
		});
		responseHeader.layout(true);

		// selection listener
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();

				Request req = null;
				if (selection != null && !selection.isEmpty()) {
					StructuredSelection sel = (StructuredSelection) selection;
					currentSelection = sel;
					Object obj = sel.iterator().next();
					if (obj instanceof Request)
						req = (Request) obj;
				}
	
				if (req != null) {
					label.setText(MonitorUIPlugin.getResource("%viewTime", format.format(req.getDate())));
	
					if (req.getResponseTime() == -1)
						label2.setText(MonitorUIPlugin.getResource("%viewResponseTime", ""));
					else {
						String time = MonitorUIPlugin.getResource("%viewResponseTimeFormat", req.getResponseTime() + "");
						label2.setText(MonitorUIPlugin.getResource("%viewResponseTime", time));
					}
					label3.setText(MonitorUIPlugin.getResource("%viewType", req.getProtocol()));
	
					// request information
					requestLabel.setText(MonitorUIPlugin.getResource("%viewRequest", "localhost:" + req.getLocalPort()));
					requestSizeLabel.setText(getSizeString(req.getRequest(Request.CONTENT), req.getRequest(Request.ALL)));
	
					// response information
					responseLabel.setText(MonitorUIPlugin.getResource("%viewResponse", req.getRemoteHost() + ":" + req.getRemotePort()));
					responseSizeLabel.setText(getSizeString(req.getResponse(Request.CONTENT), req.getResponse(Request.ALL)));

					vm.setRequest(req);
				} else {
					label.setText(MonitorUIPlugin.getResource("%viewTime", ""));
					label2.setText(MonitorUIPlugin.getResource("%viewResponseTime", ""));
					requestLabel.setText(MonitorUIPlugin.getResource("%viewRequest", ""));
					requestSizeLabel.setText(MonitorUIPlugin.getResource("%viewSize", ""));
					responseLabel.setText(MonitorUIPlugin.getResource("%viewResponse", ""));
					responseSizeLabel.setText(MonitorUIPlugin.getResource("%viewSize", ""));
					vm.setRequest(req);
				}
				currentRequest = req;
			}
		});
		treeViewer.expandToLevel(2);
		
		// create a menu manager for a context menu
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				mgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});

		// create the menu
		Menu menu = menuManager.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
		
		// register the menu with the platform
		getSite().registerContextMenu(menuManager, treeViewer);
        
		initializeActions();
	}
	
	protected String getSizeString(byte[] a, byte[] b) {
		String aa = "0";
		String bb = "0";
		if (a != null)
			aa = a.length + "";
		if (b != null)
			bb = b.length + "";
		String size = MonitorUIPlugin.getResource("%viewSizeFormat", new Object[] { aa, bb});
		return MonitorUIPlugin.getResource("%viewSize", size);
	}

	public void dispose() {
		super.dispose();
		treeViewer = null;
		view = null;
	}

	/**
	 * 
	 */
	public void initializeActions() {
		final IAction sortByResponseTimeAction = new Action() {
			public void run() {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						boolean b = contentProvider.getSortByResponseTime();
						contentProvider.setSortByResponseTime(!b);
						treeViewer.refresh();
						setChecked(!b);
					}
				});
			}
		};
		sortByResponseTimeAction.setChecked(false);
		sortByResponseTimeAction.setToolTipText(MonitorUIPlugin.getResource("%actionSortByResponseTime"));
		sortByResponseTimeAction.setImageDescriptor(MonitorUIPlugin.getImageDescriptor(MonitorUIPlugin.IMG_ELCL_SORT_RESPONSE_TIME));
		sortByResponseTimeAction.setHoverImageDescriptor(MonitorUIPlugin.getImageDescriptor(MonitorUIPlugin.IMG_CLCL_SORT_RESPONSE_TIME));
		sortByResponseTimeAction.setDisabledImageDescriptor(MonitorUIPlugin.getImageDescriptor(MonitorUIPlugin.IMG_DLCL_SORT_RESPONSE_TIME));
	
		IAction clearAction = new Action() {
			public void run() {
				MonitorUIPlugin.getInstance().clearRequests();
			}
		};
		clearAction.setToolTipText(MonitorUIPlugin.getResource("%actionClearToolTip"));
		clearAction.setImageDescriptor(MonitorUIPlugin.getImageDescriptor(MonitorUIPlugin.IMG_ELCL_CLEAR));
		clearAction.setHoverImageDescriptor(MonitorUIPlugin.getImageDescriptor(MonitorUIPlugin.IMG_CLCL_CLEAR));
		clearAction.setDisabledImageDescriptor(MonitorUIPlugin.getImageDescriptor(MonitorUIPlugin.IMG_DLCL_CLEAR));

		httpHeaderAction = new Action() {
			public void run() {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						boolean b = vm.getDisplayHeaderInfo();
						vm.setDisplayHeaderInfo(!b);
						setChecked(!b);
					}
				});
			}
		};
		httpHeaderAction.setChecked(vm.getDisplayHeaderInfo());
		httpHeaderAction.setText(MonitorUIPlugin.getResource("%actionShowHeader"));

		IAction preferenceAction = new Action() {
			public void run() {
				showPreferencePage();
			}
		};
		preferenceAction.setText(MonitorUIPlugin.getResource("%actionProperties"));
		
		
		IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
		tbm.add(sortByResponseTimeAction);
		tbm.add(clearAction);
		
		IContentFilter[] filters = MonitorCore.getContentFilters();
		IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
		menuManager.add(httpHeaderAction);
		int size = filters.length;
		for (int i = 0; i < size; i++) {
			FilterAction action = new FilterAction(vm, filters[i]);
			menuManager.add(action);
		}
		menuManager.add(preferenceAction);
	}

	protected boolean showPreferencePage() {
		PreferenceManager manager = PlatformUI.getWorkbench().getPreferenceManager();
		IPreferenceNode node = manager.find("org.eclipse.internet").findSubNode("org.eclipse.wst.internet.monitor.preferencePage");
		PreferenceManager manager2 = new PreferenceManager();
		manager2.addToRoot(node);
		
		final PreferenceDialog dialog = new PreferenceDialog(getSite().getShell(), manager2);
		final boolean[] result = new boolean[] { false };
		BusyIndicator.showWhile(getSite().getShell().getDisplay(), new Runnable() {
			public void run() {
				dialog.create();
				if (dialog.open() == Window.OK)
					result[0] = true;
			}
		});
		return result[0];
	}

	/**
	 * 
	 */
	public static void open(final Request request) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					IWorkbench workbench = MonitorUIPlugin.getInstance().getWorkbench();
					IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
	
					IWorkbenchPage page = workbenchWindow.getActivePage();
	
					IViewPart view2 = page.findView(VIEW_ID);
					
					if (view2 != null)
						page.bringToTop(view2);
					else
						page.showView(VIEW_ID);

					if (view != null)
						view.setSelection(request);
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Error opening TCP/IP view", e);
				}
			}
		});
	}

	/**
	 * 
	 */
	public void setFocus() {
		if (tree != null)
			tree.setFocus();
	}
}