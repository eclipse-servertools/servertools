package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
/**
 * 
 */
public class ServerComposite extends AbstractTreeComposite {
	protected IServer selection;
	protected ServerSelectionListener listener;
	protected ServerTreeContentProvider contentProvider;
	protected boolean initialSelection = true;
	protected byte viewOption;
	
	protected IModule module;
	protected String launchMode;
	
	public interface ServerSelectionListener {
		public void serverSelected(IServer server);
	}
	
	public ServerComposite(Composite parent, int style, ServerSelectionListener listener2, IModule module, String launchMode) {
		super(parent, style);
		this.module = module;
		this.launchMode = launchMode;
		
		this.listener = listener2;
		
		contentProvider = new ServerTreeContentProvider(ServerTreeContentProvider.STYLE_HOST, module, launchMode);
		viewOption = ServerTreeContentProvider.STYLE_HOST;
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setLabelProvider(new ServerTreeLabelProvider());
		treeViewer.setInput(AbstractTreeContentProvider.ROOT);
		treeViewer.expandToLevel(1);

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				Object obj = getSelection(event.getSelection());
				if (obj instanceof IServer) {
					selection = (IServer) obj;
					setDescription(selection.getServerType().getRuntimeType().getDescription());
				} else {
					selection = null;
					setDescription("");
				}
				listener.serverSelected(selection);
			}
		});
	}

	public ServerComposite(Composite parent, int style, ServerSelectionListener listener2) {
		this(parent, style, listener2, null, null);
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible && initialSelection) {
			initialSelection = false;
			if (contentProvider.getInitialSelection() != null)
				treeViewer.setSelection(new StructuredSelection(contentProvider.getInitialSelection()), true);
		}
	}

	public void refreshAll() {
		ISelection sel = treeViewer.getSelection();
		contentProvider = new ServerTreeContentProvider(viewOption, module, launchMode);
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setSelection(sel);
	}
	
	/*protected boolean hasDescription() {
		return true;
	}*/
	
	protected String getDescriptionLabel() {
		return null; //ServerUIPlugin.getResource("%serverTypeCompDescription");
	}
	
	protected String getTitleLabel() {
		return "Select the server that you want to use:";
	}

	protected String[] getComboOptions() {
		return new String[] { ServerUIPlugin.getResource("%name"), ServerUIPlugin.getResource("%host"), 
			ServerUIPlugin.getResource("%vendor"), ServerUIPlugin.getResource("%version")};
	}

	protected void viewOptionSelected(byte option) {
		ISelection sel = treeViewer.getSelection();
		viewOption = option;
		contentProvider = new ServerTreeContentProvider(option, module, launchMode);
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setSelection(sel);
	}
	
	public IServer getSelectedServer() {
		return selection;
	}
	
	public void setSelection(IServer server) {
		treeViewer.setSelection(new StructuredSelection(server), true);
	}
}