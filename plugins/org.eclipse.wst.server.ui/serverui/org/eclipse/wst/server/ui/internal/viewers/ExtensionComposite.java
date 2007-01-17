/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.update.core.IFeature;
import org.eclipse.update.core.ISite;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.extension.ExtensionUtility;
/**
 * 
 */
public class ExtensionComposite extends Composite {
	public interface FeatureSelectionListener {
		public void featureSelected(IFeature feature);
	}

	protected Table table;
	protected TableViewer tableViewer;

	protected Font font;

	protected HashMap images;

	protected String progress;
	protected int totalWork;
	protected double currentWork;
	protected int count;

	public FeatureSelectionListener listener;

	public ExtensionComposite(Composite parent, int style, FeatureSelectionListener listener) {
		super(parent, style);
		this.listener = listener;
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 5;
		//layout.numColumns = 2;
		setLayout(layout);
		
		Font currentFont = getFont();
		FontData[] fd = currentFont.getFontData();
		int size2 = fd.length;
		for (int i = 0; i < size2; i++) {
			fd[i].setHeight(fd[i].getHeight() + 2);
			fd[i].setStyle(SWT.BOLD);
		}
		
		font = new Font(getDisplay(), fd);
		
		GridData data = new GridData(GridData.FILL_BOTH);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
		data.heightHint = 350;
		setLayoutData(data);
		
		table = new Table(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		//data.horizontalSpan = 2;
		//data.heightHint = 250;
		data.widthHint = 350;
		table.setLayoutData(data);
		table.setLinesVisible(true);
		tableViewer = new TableViewer(table);
		table.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				disposeResources();
			}
		});
		
		TableLayout tableLayout = new TableLayout();
		table.setLayout(tableLayout);
		
		/*tableLayout.addColumnData(new ColumnPixelData(90, false));
		//tableLayout.addColumnData(new ColumnWeightData(8, 80, true));
		TableColumn col2 = new TableColumn(table, SWT.NONE);
		col2.setText("null");
		*/
		tableLayout.addColumnData(new ColumnWeightData(30, 250, false));
		final TableColumn col = new TableColumn(table, SWT.NONE);
		col.setText("null");
		/*col.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				col.pack();
			}
		});*/
		
		//tableLayout.addColumnData(new ColumnWeightData(30, 250, false));
		//TableColumn col2 = new TableColumn(table, SWT.NONE);
		//col2.setText("null2");
		
		table.setHeaderVisible(false);
		//table.setLinesVisible(false);
		
		final int TEXT_MARGIN = 3;
		table.addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event event) {
				event.gc.setFont(font);
				Point size = event.gc.textExtent("A");
				int h = TEXT_MARGIN * 3 + size.y;
				event.gc.setFont(null);
				size = event.gc.textExtent("A");
				h += size.y;
				h = Math.max(h, 40 + TEXT_MARGIN * 2);
				event.width = 400;
				event.height = Math.max(event.height, h);
			}
		});
		table.addListener(SWT.EraseItem, new Listener() {
			public void handleEvent(Event event) {
				//if (event.index == 0)
				//	return;
				event.detail &= ~SWT.FOREGROUND;
				//event.detail &= ~SWT.BACKGROUND;
			}
		});
		table.addListener(SWT.PaintItem, new Listener() {
			public void handleEvent(Event event) {
				//if (event.index == 0)
				//	return;
				TableItem item = (TableItem) event.item;
				Object obj = item.getData();
				
				int width = table.getColumn(0).getWidth();
				GC gc = event.gc;
				
				if (obj instanceof String) {
					String s = (String) obj;
					if (progress != null)
						s = progress;
					int h = event.y + event.height / 2;
					Point size = event.gc.textExtent(s);
					gc.drawText(s, event.x + TEXT_MARGIN, h - size.y/2, true);
					
					int hh = event.y + event.height - 5 - TEXT_MARGIN - 1;
					
					gc.setLineWidth(2);
					Color color = getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION);
					gc.setBackground(color);
					
					int width2 = 0;
					int step = 9;
					if (totalWork > 0)
						width2 = (int) ((width - step * 2 - 15 - TEXT_MARGIN * 2) * currentWork / totalWork);
					
					for (int i = 0; i < width2; i+= step) {
						gc.fillRectangle(event.x + TEXT_MARGIN + i, hh, step - 2, 5);
					}
					
					return;
				}
				IFeature ei = (IFeature) obj;
				if (ei == null)
					return;
				
				
				//if ((event.detail & SWT.SELECTED) == 0) {
				//	//event.gc.setForeground();
				//	//event.gc.fillRectangle(0, 0, width, 5);
				//	event.gc.setBackground(getBackground());
				//	event.gc.setForeground(getForeground());
				//	event.gc.fillRectangle(event.x, event.y, width, event.height);
				//}
				//System.out.println(width + " " + event.width + " " + event.x);
				
				String name = ei.getLabel();
				String provider = "" + ei.getProvider();
				//String provider = "" + ei.getImage();
				//String provider = "" + ExtensionUtility.getDescription(ei);
				String version = "v" + ei.getVersionedIdentifier().getVersion().toString();
				
				Image image = getImage(ei.getImage());
				if (image == null)
					image = ImageResource.getImage(ImageResource.IMG_WIZBAN_NEW_SERVER);
				int iw = image.getBounds().width;
				int ih = image.getBounds().height;
				gc.drawImage(image, 0, 0, iw, ih, event.x + TEXT_MARGIN, event.y + TEXT_MARGIN, 40, 40);
				iw = 40 + TEXT_MARGIN * 2;
				
				int yOffset = TEXT_MARGIN;
				gc.setFont(font);
				gc.drawText(name, event.x + iw, event.y + yOffset, true);
				Point size = event.gc.textExtent(name);
				gc.setFont(null);
				
				yOffset += size.y + TEXT_MARGIN;
				gc.drawText(provider, event.x + iw, event.y + yOffset, true);
				
				size = event.gc.textExtent(version);
				gc.drawText(version, event.x + width - TEXT_MARGIN * 2 - size.x, event.y + yOffset, true);
			}
		});
		
		tableViewer.setSorter(new ViewerSorter() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				try {
					IFeature f1 = (IFeature) e1;
					IFeature f2 = (IFeature) e2;
					return (f1.getLabel().compareToIgnoreCase(f2.getLabel()));
				} catch (Exception e) {
					return 0;
				}
			}
		});
		
		/*Composite buttonComp = new Composite(this, SWT.NONE);
		buttonComp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		buttonComp.setLayout(layout);
		
		Button install = SWTUtil.createButton(buttonComp, "Find Updates");
		install.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//
			}
		});*/
		
		final Label description = new Label(this, SWT.WRAP);
		description.setText("Multi\nLine\nMessage");
		Dialog.applyDialogFont(this);
		Point p = description.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		description.setText("");
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
		data.horizontalSpan = 2;
		if (p.y > 10)
			data.heightHint = p.y;
		else
			data.heightHint = 42;
		description.setLayoutData(data);
		
		/*final Label download = new Label(this, SWT.NONE);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
		data.horizontalSpan = 2;
		download.setLayoutData(data);
		download.setText("Download size: ");*/
		
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection sel = (StructuredSelection) tableViewer.getSelection();
				Object obj = sel.getFirstElement();
				IFeature feature = null;
				if (obj instanceof IFeature)
					feature = (IFeature) obj;
				
				handleSelection(feature);
				if (feature != null)
					description.setText(ExtensionUtility.getDescription(feature));
				else
					description.setText("");
				/*long size = feature.getDownloadSize();
				String s = "<unknown>";
				if (size > 0)
					s = size + " Kb";
				download.setText("Download size: " + s);*/
			}
		});
		
		deferInitialization();
	}

	protected Image getImage(URL url) {
		if (url == null)
			return null;
		
		try {
			Image image = (Image) images.get(url.toString());
			if (image != null)
				return image;
		} catch (Exception e) {
			// ignore
		}
		try {
			ImageDescriptor id = ImageDescriptor.createFromURL(url);
			Image image = id.createImage();
			if (images == null)
				images = new HashMap();
			images.put(url.toString(), image);
			return image;
		} catch (Exception e) {
			Trace.trace(Trace.INFO, "Could not create image", e);
		}
		return null;
	}

	protected void disposeResources() {
		try {
			font.dispose();
			
			if (images != null) {
				Iterator iterator = images.values().iterator();
				while (iterator.hasNext()) {
					Image image = (Image) iterator.next();
					image.dispose();
				}
			}
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not dispose - possible resource leak", e);
		}
	}

	protected void handleSelection(IFeature feature) {
		listener.featureSelected(feature);
	}

	protected void deferInitialization() {
		Object[] obj = new Object[] { Messages.viewInitializing };
		tableViewer.setContentProvider(new ExtensionContentProvider(obj));
		tableViewer.setLabelProvider(new ExtensionTableLabelProvider());
		tableViewer.setInput(AbstractTreeContentProvider.ROOT);
		
		final Thread t = new Thread() {
			public void run() {
				deferredInitialize(new IProgressMonitor() {
					public void beginTask(String name, int totalWork2) {
						totalWork = totalWork2;
						progress = name;
					}

					public void setTaskName(String name) {
						progress = name;
					}

					public void subTask(String name) {
						progress = name;
					}

					public void done() {
						// 
					}

					public void internalWorked(double work) {
						currentWork += work;
					}

					public boolean isCanceled() {
						return false;
					}

					public void setCanceled(boolean value) {
						// 
					}

					public void worked(int work) {
						currentWork += work;
					}
				});
			}
		};
		t.setDaemon(true);
		t.start();
		
		final Display display = getDisplay();
		final int SLEEP = 200;
		final Runnable[] animator = new Runnable[1];
		animator[0] = new Runnable() {
			public void run() {
				if (t.isAlive()) {
					count++;
					try {
						Object[] rootElements = ((IStructuredContentProvider)tableViewer.getContentProvider()).getElements(null);
						//tableViewer.update(Messages.viewInitializing, null);
						tableViewer.update(rootElements, null);
					} catch (Exception e) {
						// ignore
					}
					display.timerExec(SLEEP, animator[0]);
				}
			}
		};
		display.timerExec(SLEEP, animator[0]);
	}

	public void deferredInitialize(IProgressMonitor monitor) {
		final List list = new ArrayList();
		ExtensionUtility.FeatureListener listener2 = new ExtensionUtility.FeatureListener() {
			public void featureFound(IFeature feature) {
				list.add(feature);
				int size = list.size();
				final Object[] obj = new Object[size+1];
				list.toArray(obj);
				if (progress != null)
					obj[size] = progress;
				else
					obj[size] = Messages.viewInitializing;
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (!table.isDisposed())
							tableViewer.setContentProvider(new ExtensionContentProvider(obj));
					}
				});
			}

			public void featureRemoved(IFeature feature) {
				list.remove(feature);
			}

			public void siteFailure(ISite site, CoreException ce) {
				// 
			}
		};
		
		String id = "org.eclipse.wst.server.core.serverAdapter";
		try {
			final IFeature[] ef = ExtensionUtility.getAllFeatures(id, listener2, monitor);
			
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (!table.isDisposed())
						tableViewer.setContentProvider(new ExtensionContentProvider(ef));
				}
			});
		} catch (CoreException ce) {
			ce.printStackTrace();
		}
	}

	protected Object getSelection(ISelection sel2) {
		IStructuredSelection sel = (IStructuredSelection) sel2;
		return sel.getFirstElement();
	}
}