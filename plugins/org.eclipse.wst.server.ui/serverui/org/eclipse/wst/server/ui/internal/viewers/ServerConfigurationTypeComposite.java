package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.wst.server.core.IServerConfigurationType;
/**
 * 
 */
public class ServerConfigurationTypeComposite extends AbstractTreeComposite {
	protected IServerConfigurationType selection;
	protected ServerConfigurationTypeSelectionListener listener;
	
	public interface ServerConfigurationTypeSelectionListener {
		public void configurationTypeSelected(IServerConfigurationType type);
	}
	
	public ServerConfigurationTypeComposite(Composite parent, int style, ServerConfigurationTypeSelectionListener listener2) {
		super(parent, style);
		this.listener = listener2;
	
		treeViewer.setContentProvider(new ServerConfigurationTypeTreeContentProvider(RuntimeTreeContentProvider.STYLE_VENDOR));
		treeViewer.setLabelProvider(new ServerConfigurationTypeTreeLabelProvider());
		treeViewer.setInput(AbstractTreeContentProvider.ROOT);

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				Object obj = getSelection(event.getSelection());
				if (obj instanceof IServerConfigurationType) {
					selection = (IServerConfigurationType) obj;
					setDescription(selection.getDescription());
				} else {
					selection = null;
					setDescription("");
				}
				listener.configurationTypeSelected(selection);
			}
		});
	}
	
	protected String getDescriptionLabel() {
		return null; //ServerUIPlugin.getResource("%serverTypeCompDescription");
	}

	protected String getTitleLabel() {
		return "Select the server configuration type:";
	}

	protected String[] getComboOptions() {
		return new String[] {"Name", "Vendor", "Version"};
	}

	protected void viewOptionSelected(byte option) {
		ISelection sel = treeViewer.getSelection();
		treeViewer.setContentProvider(new ServerConfigurationTypeTreeContentProvider(option));
		treeViewer.setSelection(sel);
	}

	public IServerConfigurationType getSelectedServerConfigurationType() {
		return selection;
	}
}
