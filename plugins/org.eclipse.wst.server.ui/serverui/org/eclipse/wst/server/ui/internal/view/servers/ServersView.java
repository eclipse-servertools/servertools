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
package org.eclipse.wst.server.ui.internal.view.servers;

import java.util.Iterator;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.IDebugView;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.Trace;
import org.eclipse.wst.server.core.model.ServerDelegate;
import org.eclipse.wst.server.ui.internal.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.navigator.LocalSelectionTransfer;
/**
 * View of server, their configurations and status.
 */
public class ServersView extends ViewPart {
	private static final String TAG_COLUMN_WIDTH = "columnWidth";

	protected int[] cols;

	protected Tree treeTable;
	protected ServerTableViewer tableViewer;

	// actions on a server
	protected Action[] actions;
	protected MenuManager restartMenu;

	/**
	 * ServersView constructor comment.
	 */
	public ServersView() {
		super();
	}

	/**
	 * createPartControl method comment.
	 * 
	 * @param parent a parent composite
	 */
	public void createPartControl(Composite parent) {
		treeTable = new Tree(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.NONE);
		treeTable.setHeaderVisible(true);
		treeTable.setLinesVisible(false);
		treeTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		treeTable.setFont(parent.getFont());
		PlatformUI.getWorkbench().getHelpSystem().setHelp(treeTable, ContextIds.VIEW_SERVERS);
		
		// add columns
		TreeColumn column = new TreeColumn(treeTable, SWT.SINGLE);
		column.setText(Messages.viewServer);
		column.setWidth(cols[0]);
		
		TreeColumn column2 = new TreeColumn(treeTable, SWT.SINGLE);
		column2.setText(Messages.viewStatus);
		column2.setWidth(cols[1]);
		
		TreeColumn column3 = new TreeColumn(treeTable, SWT.SINGLE);
		column3.setText(Messages.viewSync);
		column3.setWidth(cols[2]);
		
		tableViewer = new ServerTableViewer(this, treeTable);
		initializeActions(tableViewer);
		
		treeTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					/*TableItem item = table.getSelection()[0];
					IServerResource resource = (IServerResource) item.getData();
					IServerResourceFactory factory = ServerUtil.getServerResourceFactory(resource);
					String label = ServerLabelProvider.getInstance().getText(factory);
					label += " (";
					label += ServerCore.getResourceManager().getServerResourceLocation(resource).getFullPath().toString().substring(1);
					label += ")";
					getViewSite().getActionBars().getStatusLineManager().setMessage(ServerLabelProvider.getInstance().getImage(factory), label);
					
					if (resource instanceof IServer) {
						IServer server = (IServer) resource;
						ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
						ILaunch[] launches = launchManager.getLaunches();
						int size = launches.length;
						for (int i = size-1; i >= 0; i--) {
							ILaunchConfiguration config = launches[i].getLaunchConfiguration();
							if (LAUNCH_CONFIGURATION_TYPE_ID.equals(config.getType().getIdentifier()) &&
									ServerCore.getServerRef(server).equals(config.getAttribute(SERVER_REF, (String)null))) {
								selectServerProcess(launches[i]);
								return;
							}
						}
					}*/
				} catch (Exception e) {
					getViewSite().getActionBars().getStatusLineManager().setMessage(null, "");
				}
			}
			public void widgetDefaultSelected(SelectionEvent event) {
				try {
					TreeItem item = treeTable.getSelection()[0];
					Object data = item.getData();
					if (!(data instanceof IServer))
						return;
					IServer server = (IServer) data;
					ServerUIPlugin.editServer(server);
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Could not open server", e);
				}
			}
		});
		
		MenuManager menuManager = new MenuManager("#PopupMenu");
		menuManager.setRemoveAllWhenShown(true);
		final Shell shell = treeTable.getShell();
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				fillContextMenu(shell, mgr);
			}
		});
		Menu menu = menuManager.createContextMenu(parent);
		treeTable.setMenu(menu);
		getSite().registerContextMenu(menuManager, tableViewer);
		getSite().setSelectionProvider(tableViewer);
		
		initDragAndDrop();
	}

	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		cols = new int[3];
		for (int i = 0; i < 3; i++) {
			cols[i] = 200;
			if (memento != null) {
				Integer in = memento.getInteger(TAG_COLUMN_WIDTH + i);
				if (in != null && in.intValue() > 5)
					cols[i] = in.intValue();
			}
		}
	}

	public void saveState(IMemento memento) {
		TreeColumn[] tc = treeTable.getColumns();
		for (int i = 0; i < 3; i++) {
			int width = tc[i].getWidth();
			if (width != 0)
				memento.putInteger(TAG_COLUMN_WIDTH + i, width);
		}
	}

	protected void selectServerProcess(Object process) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow() ;
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				IWorkbenchPart part = page.findView(IDebugUIConstants.ID_DEBUG_VIEW);
				if (part != null) {
					IDebugView view = (IDebugView)part.getAdapter(IDebugView.class);
					if (view != null) {
						Viewer viewer = view.getViewer();
						if (viewer != null) {
							viewer.setSelection(new StructuredSelection(process));
						}
					}
				}
			}
		}
	}

	/**
	 * Initialize actions
	 * 
	 * @param provider a selection provider
	 */
	public void initializeActions(ISelectionProvider provider) {
		Shell shell = getSite().getShell();

		// create the debug action
		Action debugAction = new StartAction(shell, provider, "debug", ILaunchManager.DEBUG_MODE);
		debugAction.setToolTipText(Messages.actionDebugToolTip);
		debugAction.setText(Messages.actionDebug);
		debugAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START_DEBUG));
		debugAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START_DEBUG));
		debugAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START_DEBUG));
	
		// create the start action
		Action runAction = new StartAction(shell, provider, "start", ILaunchManager.RUN_MODE);
		runAction.setToolTipText(Messages.actionStartToolTip);
		runAction.setText(Messages.actionStart);
		runAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START));
		runAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START));
		runAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START));
		
		// create the profile action
		Action profileAction = new StartAction(shell, provider, "profile", ILaunchManager.PROFILE_MODE);
		profileAction.setToolTipText(Messages.actionProfileToolTip);
		profileAction.setText(Messages.actionProfile);
		profileAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START_PROFILE));
		profileAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START_PROFILE));
		profileAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START_PROFILE));
	
		// create the restart menu
		restartMenu = new MenuManager(Messages.actionRestart);
		
		Action restartAction = new RestartAction(shell, provider, "restartDebug", ILaunchManager.DEBUG_MODE);
		restartAction.setToolTipText(Messages.actionDebugToolTip);
		restartAction.setText(Messages.actionDebug);
		restartAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START_DEBUG));
		restartAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START_DEBUG));
		restartAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START_DEBUG));
		restartMenu.add(restartAction);
		
		restartAction = new RestartAction(shell, provider, "restartRun", ILaunchManager.RUN_MODE);
		restartAction.setToolTipText(Messages.actionRestartToolTip);
		restartAction.setText(Messages.actionStart);
		restartAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START));
		restartAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START));
		restartAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START));
		restartMenu.add(restartAction);
		
		restartAction = new RestartAction(shell, provider, "restartProfile", ILaunchManager.PROFILE_MODE);
		restartAction.setToolTipText(Messages.actionRestartToolTip);
		restartAction.setText(Messages.actionProfile);
		restartAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START_PROFILE));
		restartAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START_PROFILE));
		restartAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START_PROFILE));
		restartMenu.add(restartAction);
		
		// create the restart action
		restartAction = new RestartAction(shell, provider, "restart");
		restartAction.setToolTipText(Messages.actionRestartToolTip);
		restartAction.setText(Messages.actionRestart);
		restartAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_RESTART));
		restartAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_RESTART));
		restartAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_RESTART));

		// create the stop action
		Action stopAction = new StopAction(shell, provider, "stop");
		stopAction.setToolTipText(Messages.actionStopToolTip);
		stopAction.setText(Messages.actionStop);
		stopAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_STOP));
		stopAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_STOP));
		stopAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_STOP));

		// create the disconnect action
		/*Action disconnectAction = new StopAction(shell, provider, "disconnect", IServerType.SERVER_STATE_SET_ATTACHED);
		disconnectAction.setToolTipText(Messages.actionStopToolTip2"));
		disconnectAction.setText(Messages.actionStop2"));
		disconnectAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_DISCONNECT));
		disconnectAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_DISCONNECT));
		disconnectAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_DISCONNECT));*/

		// create the publish action
		Action publishAction = new PublishAction(shell, provider, "publish");
		publishAction.setToolTipText(Messages.actionPublishToolTip);
		publishAction.setText(Messages.actionPublish);
		publishAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_PUBLISH));
		publishAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_PUBLISH));
		publishAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_PUBLISH));
		
		// create the module slosh dialog action
		Action addModuleAction = new ModuleSloshAction(shell, provider, "modules");
		addModuleAction.setToolTipText(Messages.actionModifyModulesToolTip);
		addModuleAction.setText(Messages.actionModifyModules);
		addModuleAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ETOOL_MODIFY_MODULES));
		addModuleAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CTOOL_MODIFY_MODULES));
		addModuleAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DTOOL_MODIFY_MODULES));
		
		actions = new Action[7];
		actions[0] = debugAction;
		actions[1] = runAction;
		actions[2] = profileAction;
		actions[3] = restartAction;
		actions[4] = stopAction;
		actions[5] = publishAction;
		actions[6] = addModuleAction;
		
		// add toolbar buttons
		IContributionManager cm = getViewSite().getActionBars().getToolBarManager();
		for (int i = 0; i < actions.length - 1; i++) {
			cm.add(actions[i]);
		}
	}

	protected void fillContextMenu(Shell shell, IMenuManager menu) {
		// get selection but avoid no selection or multiple selection
		IServer server = null;
		IModule[] module = null;
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		if (!selection.isEmpty()) {
			Iterator iterator = selection.iterator();
			Object obj = iterator.next();
			if (obj instanceof IServer)
				server = (IServer) obj;
			if (obj instanceof ModuleServer) {
				ModuleServer ms = (ModuleServer) obj;
				server = ms.server;
				module = ms.module;
			}
			if (iterator.hasNext()) {
				server = null;
				module = null;
			}
		}
		
		// new action
		MenuManager newMenu = new MenuManager(Messages.actionNew);
		ServerActionHelper.fillNewContextMenu(null, selection, newMenu);
		menu.add(newMenu);
		
		// open action
		if (server != null) {
			menu.add(new OpenAction(server));
			menu.add(new UpdateStatusAction(server));
			menu.add(new Separator());
			
			if (module == null)
				menu.add(new DeleteAction(shell, server));
			else if (module.length == 1)
				menu.add(new RemoveModuleAction(shell, server, module[0]));
			menu.add(new Separator());
			
			// server actions
			for (int i = 0; i < actions.length - 1; i++) {
				if (i == 3)
					menu.add(restartMenu);
				else
					menu.add(actions[i]);
			}
			
			// monitor
			if (server.getServerType() != null) {
				final MenuManager menuManager = new MenuManager(Messages.actionMonitor);
				
				final IServer server2 = server;
				final Shell shell2 = shell;
				menuManager.addMenuListener(new IMenuListener() {
					public void menuAboutToShow(IMenuManager manager) {
						menuManager.removeAll();
						if (server2.getAdapter(ServerDelegate.class) != null) {
							ServerPort[] ports = server2.getServerPorts(null);
							if (ports != null) {
								int size = ports.length;
								for (int i = 0; i < size; i++) {
									if (!ports[i].isAdvanced())
										menuManager.add(new MonitorServerPortAction(shell2, server2, ports[i]));
								}
							}
						}
						
						if (!menuManager.isEmpty())
							menuManager.add(new Separator());
						
						menuManager.add(new MonitorServerAction(shell2, server2));
					}
				});
				
				// add an initial menu item so that the menu appears correctly
				menuManager.add(new MonitorServerAction(shell, server));
				menu.add(menuManager);
			}
			menu.add(new SwitchServerLocationAction(server));
		}
		
		if (server != null && module != null) {
			menu.add(new Separator());
			menu.add(new RestartModuleAction(server, module));
		}
		
		menu.add(new Separator());
		menu.add(actions[6]);
		
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS+"-end"));
	}

	/**
	 * 
	 */
	public void setFocus() {
		if (treeTable != null)
			treeTable.setFocus();
	}

	/**
    * Adds drag and drop support to the Servers view.
    */
   protected void initDragAndDrop() {
		int ops = DND.DROP_COPY;
		Transfer[] transfers = new Transfer[] { LocalSelectionTransfer.getInstance(),
			ResourceTransfer.getInstance(), FileTransfer.getInstance() };
		//tableViewer.addDragSupport(ops, transfers, new ServersViewDragAdapter(viewer));
		tableViewer.addDropSupport(ops | DND.DROP_DEFAULT, transfers, new ServersViewDropAdapter(tableViewer));
   }
}