package org.eclipse.wst.server.ui.internal.wizard.page;

//import java.util.ArrayList;
//import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
/**
 * 
 */
public class FixedTableLayout extends Layout {
	//private List columns = new ArrayList();
	
	/*public void addColumnData(Integer data) {
		columns.add(data);
	}*/

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Layout#computeSize(org.eclipse.swt.widgets.Composite, int, int, boolean)
	 */
	protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
		Table table = (Table) composite;
		Point result = table.computeSize(wHint, hHint, flushCache);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Layout#layout(org.eclipse.swt.widgets.Composite, boolean)
	 */
	protected void layout(Composite composite, boolean flushCache) {
		Table table = (Table) composite;
		TableColumn[] tableColumns = table.getColumns();
		int width = table.getClientArea().width - 10;
		
		tableColumns[0].setWidth(width);
	}
}
