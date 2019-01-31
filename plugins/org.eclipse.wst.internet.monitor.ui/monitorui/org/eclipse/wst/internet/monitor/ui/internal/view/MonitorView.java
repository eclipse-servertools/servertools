/*******************************************************************************
 * Copyright (c) 2003, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.ui.internal.view;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wst.internet.monitor.core.internal.IContentFilter;
import org.eclipse.wst.internet.monitor.core.internal.MonitorPlugin;
import org.eclipse.wst.internet.monitor.core.internal.http.ResendHTTPRequest;
import org.eclipse.wst.internet.monitor.core.internal.provisional.IRequestListener;
import org.eclipse.wst.internet.monitor.core.internal.provisional.Request;
import org.eclipse.wst.internet.monitor.ui.internal.ContextIds;
import org.eclipse.wst.internet.monitor.ui.internal.Messages;
import org.eclipse.wst.internet.monitor.ui.internal.MonitorUIPlugin;
import org.eclipse.wst.internet.monitor.ui.internal.Trace;
/**
 * View of TCP/IP activity.
 */
public class MonitorView extends ViewPart {
	protected Tree tree;
	protected TreeViewer treeViewer;
	protected MonitorTreeContentProvider contentProvider;

	protected IRequestListener listener;
	protected ViewerManager vm;
	protected List requestViewers;
	protected List responseViewers;

	protected static SimpleDateFormat format;
	protected static final String VIEW_ID = "org.eclipse.wst.internet.monitor.view";
	protected static final String DEFAULT_VIEWER = "org.eclipse.wst.internet.monitor.viewers.byte";

	protected IAction httpHeaderAction;
	protected IAction preferenceAction;
	
	public static MonitorView view;
	
	protected Request currentRequest = null;
	protected StructuredSelection currentSelection = null;

	/**
	 * MonitorView constructor comment.
	 */
	public MonitorView() {
		super();
		view = this;
		
		// try specified format, and fall back to standard format
		try {
			format = new SimpleDateFormat(Messages.viewDateFormat);
		} catch (Exception e) {
			format = new SimpleDateFormat("h:mm.s.S a");
		}
	}

	public void doRequestAdded(final Request rr) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!(rr instanceof ResendHTTPRequest)) {
				  treeViewer.refresh(MonitorTreeContentProvider.ROOT);
				  if (!MonitorUIPlugin.getPinViewPreference())
					  treeViewer.setSelection(new StructuredSelection(rr), true);
				}
			}
		});
	}

	public void doRequestChanged(final Request rr) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (treeViewer == null)
					return;
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
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		SashForm sashFparent = new SashForm(parent, SWT.VERTICAL);
		sashFparent.setBackground(sashFparent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		
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
		data.widthHint = 350;
		detailsPanel.setLayoutData(data);

		final Text detailsTimeText = new Text(detailsPanel, SWT.READ_ONLY);
		detailsTimeText.setText(NLS.bind(Messages.viewTime, ""));
		detailsTimeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		final Text detailsResponseTimeText = new Text(detailsPanel, SWT.READ_ONLY);
		detailsResponseTimeText.setText(NLS.bind(Messages.viewResponseTime, ""));
		detailsResponseTimeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
		
		final Text detailsViewTimeText = new Text(detailsPanel, SWT.READ_ONLY);
		detailsViewTimeText.setText(NLS.bind(Messages.viewType, ""));
		detailsViewTimeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
		
		// create center and right panels
		SashForm sashFchild = new SashForm(sashFparent, SWT.HORIZONTAL);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.horizontalSpacing = 2;
		layout.verticalSpacing = 4;
		sashFchild.setLayout(layout);
		sashFparent.setWeights(new int[] { 30, 70 });
		
		// request panel
		Composite request = new Composite(sashFchild, SWT.NONE);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		request.setLayout(layout);
		request.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite requestHeader = new Composite(request, SWT.NONE);
		layout = new GridLayout();
		layout.verticalSpacing = 1;
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 0;
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		requestHeader.setLayout(layout);
		requestHeader.setLayoutData(data);
		
		final Label requestViewerComboLabel = new Label(requestHeader, SWT.NONE);
		requestViewerComboLabel.setText(NLS.bind(Messages.viewRequestType, ""));
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.verticalSpan = 1;
		data.horizontalIndent=3;
		requestViewerComboLabel.setLayoutData(data);
		
		final Combo requestViewerCombo = new Combo(requestHeader, SWT.DROP_DOWN | SWT.READ_ONLY);
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.verticalSpan = 1;
		requestViewerCombo.setLayoutData(data);
		
		final Text requestLabel = new Text(requestHeader, SWT.READ_ONLY);
		requestLabel.setText(NLS.bind(Messages.viewRequest, ""));	
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.verticalSpan = 1;
		data.horizontalSpan = 2;
		requestLabel.setLayoutData(data);		
		
		final Text requestSizeLabel = new Text(requestHeader, SWT.READ_ONLY);
		requestSizeLabel.setText(NLS.bind(Messages.viewSize, ""));
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.verticalSpan = 1;
		data.horizontalSpan = 2;
		requestSizeLabel.setLayoutData(data);
		
		// response panel
		Composite response = new Composite(sashFchild, SWT.NONE);
		layout = new GridLayout();
		layout.verticalSpacing = 3;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		response.setLayout(layout);
		response.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite responseHeader = new Composite(response, SWT.NONE);
		layout = new GridLayout();
		layout.verticalSpacing = 1;
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 0;
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		responseHeader.setLayout(layout);
		responseHeader.setLayoutData(data);
				
		final Label responseViewerComboLabel = new Label(responseHeader, SWT.NONE);
		responseViewerComboLabel.setText(NLS.bind(Messages.viewResponseType, ""));
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.verticalSpan = 1;
		data.horizontalIndent=3;
		responseViewerComboLabel.setLayoutData(data);		
		
		final Combo responseViewerCombo = new Combo(responseHeader, SWT.DROP_DOWN | SWT.READ_ONLY);
		data = new GridData(GridData.HORIZONTAL_ALIGN_END);
		data.verticalSpan = 1;
		responseViewerCombo.setLayoutData(data);
		
		final Text responseLabel = new Text(responseHeader, SWT.READ_ONLY);
		responseLabel.setText(NLS.bind(Messages.viewResponse, ""));
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.verticalSpan = 1;
		data.horizontalSpan = 2;
		requestLabel.setLayoutData(data);			
		
		final Text responseSizeLabel = new Text(responseHeader, SWT.READ_ONLY);
		responseSizeLabel.setText(NLS.bind(Messages.viewSize, ""));
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.verticalSpan = 1;
		data.horizontalSpan = 2;
		responseSizeLabel.setLayoutData(data);
		
		// viewer manager
		vm = new ViewerManager(request, response);
		requestViewers = vm.getRequestViewers();
		responseViewers = vm.getResponseViewers();
		
		// set up the viewer combo boxes
		Iterator iterator = requestViewers.iterator();
		int ctr = 0;
		while (iterator.hasNext()) {
			Viewer viewer = (Viewer) iterator.next();
			requestViewerCombo.add(viewer.getLabel(), ctr);
			if (viewer.getId().equals(DEFAULT_VIEWER)) {
				requestViewerCombo.select(ctr); 
				vm.setRequestViewer(viewer);
			}
			ctr++;
		}
		requestViewerCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				Viewer viewer = (Viewer) requestViewers.get(requestViewerCombo.getSelectionIndex());
				if (currentRequest != null && viewer != null)
					currentRequest.setProperty("request-viewer", viewer.getId());
				vm.setRequestViewer(viewer);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}
		});
		requestHeader.layout(true);
		
		iterator = responseViewers.iterator();
		ctr = 0;
		while (iterator.hasNext()) {
			Viewer viewer = (Viewer) iterator.next();
			responseViewerCombo.add(viewer.getLabel(), ctr);
			if (viewer.getId().equals(DEFAULT_VIEWER)) {
				responseViewerCombo.select(ctr); 
				vm.setResponseViewer(viewer);
			}
			ctr++;
		}
		responseViewerCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				Viewer viewer = (Viewer) responseViewers.get(responseViewerCombo.getSelectionIndex());
				if (currentRequest != null && viewer != null)
					currentRequest.setProperty("response-viewer", viewer.getId());
				vm.setResponseViewer(viewer);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}
		});
		responseHeader.layout(true);

		// selection listener
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();

				currentRequest = null;
				if (selection != null && !selection.isEmpty()) {
					StructuredSelection sel = (StructuredSelection) selection;
					currentSelection = sel;
					Object obj = sel.iterator().next();
					if (obj instanceof Request)
						currentRequest = (Request) obj;
				}
	
				if (currentRequest != null) {
					detailsTimeText.setText(NLS.bind(Messages.viewTime, format.format(currentRequest.getDate())));
	
					if (currentRequest.getResponseTime() == -1)
						detailsResponseTimeText.setText(NLS.bind(Messages.viewResponseTime, ""));
					else {
						String time = NLS.bind(Messages.viewResponseTimeFormat, currentRequest.getResponseTime() + "");
						detailsResponseTimeText.setText(NLS.bind(Messages.viewResponseTime, time));
					}
					detailsViewTimeText.setText(NLS.bind(Messages.viewType, currentRequest.getProtocol()));
	
					// request information
					requestLabel.setText(NLS.bind(Messages.viewRequest, "localhost:" + currentRequest.getLocalPort()));
					requestSizeLabel.setText(getSizeString(currentRequest.getRequest(Request.CONTENT), currentRequest.getRequest(Request.ALL)));
	
					// response information
					responseLabel.setText(NLS.bind(Messages.viewResponse, currentRequest.getRemoteHost() + ":" + currentRequest.getRemotePort()));
					responseSizeLabel.setText(getSizeString(currentRequest.getResponse(Request.CONTENT), currentRequest.getResponse(Request.ALL)));
					
					vm.setRequest(currentRequest);
					
					Viewer viewer = vm.findViewer((String) currentRequest.getProperty("request-viewer"));
					if (viewer == null)
						viewer = vm.findViewer(DEFAULT_VIEWER);
					if (viewer != null) {
						vm.setRequestViewer(viewer);
						requestViewerCombo.select(requestViewers.indexOf(viewer));
					}
					
					viewer = vm.findViewer((String) currentRequest.getProperty("response-viewer"));
					if (viewer == null && currentRequest.getName() != null)
						viewer = vm.getDefaultViewer(currentRequest.getName());
					if (viewer != null) {
						vm.setResponseViewer(viewer);
						responseViewerCombo.select(responseViewers.indexOf(viewer));
					}
				} else {
					detailsTimeText.setText(NLS.bind(Messages.viewTime, ""));
					detailsResponseTimeText.setText(NLS.bind(Messages.viewResponseTime, ""));
					requestLabel.setText(NLS.bind(Messages.viewRequest, ""));
					requestSizeLabel.setText(NLS.bind(Messages.viewSize, ""));
					responseLabel.setText(NLS.bind(Messages.viewResponse, ""));
					responseSizeLabel.setText(NLS.bind(Messages.viewSize, ""));
					vm.setRequest(currentRequest);
				}
			}
		});
		treeViewer.expandToLevel(2);
		
		// create a menu manager for a context menu
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager menu) {
				menu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
				menu.add(preferenceAction);
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
		String size = NLS.bind(Messages.viewSizeFormat, new Object[] { aa, bb});
		return NLS.bind(Messages.viewSize, size);
	}

	public void dispose() {
		super.dispose();
		treeViewer = null;
		view = null;
	}

	/**
	 * Initialize the tool-bar and menu actions.
	 */
	public void initializeActions() {
		final IAction pinAction = new Action() {
			public void run() {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						boolean pin = MonitorUIPlugin.getPinViewPreference();
						MonitorUIPlugin.setPinViewPreference(!pin);
					}
				});
			}
		};
		pinAction.setChecked(false);
		pinAction.setToolTipText(Messages.actionPin);
		pinAction.setImageDescriptor(MonitorUIPlugin.getImageDescriptor(MonitorUIPlugin.IMG_ELCL_PIN));
		pinAction.setHoverImageDescriptor(MonitorUIPlugin.getImageDescriptor(MonitorUIPlugin.IMG_CLCL_PIN));
		pinAction.setDisabledImageDescriptor(MonitorUIPlugin.getImageDescriptor(MonitorUIPlugin.IMG_DLCL_PIN));
		
		
		final IAction sortByResponseTimeAction = new Action() {
			public void run() {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (treeViewer.getComparator() == null) {
							setChecked(true);
							treeViewer.setComparator(new RequestComparator());
						} else {
							setChecked(false);
							treeViewer.setComparator(null);
						}
					}
				});
			}
		};
		sortByResponseTimeAction.setChecked(false);
		sortByResponseTimeAction.setToolTipText(Messages.actionSortByResponseTime);
		sortByResponseTimeAction.setImageDescriptor(MonitorUIPlugin.getImageDescriptor(MonitorUIPlugin.IMG_ELCL_SORT_RESPONSE_TIME));
		sortByResponseTimeAction.setHoverImageDescriptor(MonitorUIPlugin.getImageDescriptor(MonitorUIPlugin.IMG_CLCL_SORT_RESPONSE_TIME));
		sortByResponseTimeAction.setDisabledImageDescriptor(MonitorUIPlugin.getImageDescriptor(MonitorUIPlugin.IMG_DLCL_SORT_RESPONSE_TIME));
	
		IAction clearAction = new Action() {
			public void run() {
				MonitorUIPlugin.getInstance().clearRequests();
				clear();
			}
		};
		clearAction.setToolTipText(Messages.actionClearToolTip);
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
		httpHeaderAction.setText(Messages.actionShowHeader);

		preferenceAction = new Action() {
			public void run() {
				showPreferencePage();
			}
		};
		preferenceAction.setText(Messages.actionProperties);
		
		
		IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
		tbm.add(sortByResponseTimeAction);
		tbm.add(clearAction);
		tbm.add(pinAction);
		
		IContentFilter[] filters = MonitorPlugin.getInstance().getContentFilters();
		IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
		menuManager.add(httpHeaderAction);
		for (IContentFilter cf : filters) {
			FilterAction action = new FilterAction(vm, cf);
			menuManager.add(action);
		}
		menuManager.add(new Separator());
		menuManager.add(preferenceAction);
	}

	protected boolean showPreferencePage() {
		PreferenceManager manager = PlatformUI.getWorkbench().getPreferenceManager();
		IPreferenceNode node = manager.find("org.eclipse.debug.ui.DebugPreferencePage").findSubNode("org.eclipse.wst.internet.monitor.preferencePage");
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
	 * Open a request.
	 * 
	 * @param request the request
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
					
					if (view != null && !MonitorUIPlugin.getPinViewPreference())
						view.setSelection(request);
				} catch (Exception e) {
					if(Trace.SEVERE) {
						Trace.trace(Trace.STRING_SEVERE, "Error opening TCP/IP view", e);
					}
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
