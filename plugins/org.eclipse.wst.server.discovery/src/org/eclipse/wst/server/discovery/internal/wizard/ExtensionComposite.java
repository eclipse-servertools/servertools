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
package org.eclipse.wst.server.discovery.internal.wizard;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.osgi.util.NLS;
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
import org.eclipse.wst.server.discovery.internal.ExtensionUtility;
import org.eclipse.wst.server.discovery.internal.Messages;
import org.eclipse.wst.server.discovery.internal.Trace;
import org.eclipse.wst.server.discovery.internal.model.IExtension;
/**
 * 
 */
public class ExtensionComposite extends Composite {
	private static final String ROOT = "root";

	public interface ExtensionSelectionListener {
		public void extensionSelected(IExtension extension);
	}

	protected Table table;
	protected TableViewer tableViewer;

	protected Font font;

	protected HashMap<String, Image> images;

	protected String progress;
	protected int totalWork;
	protected double currentWork;
	protected int count;

	public ExtensionSelectionListener listener;

	public ExtensionComposite(Composite parent, int style, ExtensionSelectionListener listener) {
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
		//table.setLinesVisible(true);
		table.setHeaderVisible(false);
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
		tableLayout.addColumnData(new ColumnWeightData(10, 250, false));
		final TableColumn col = new TableColumn(table, SWT.NONE);
		col.setText("null");
		/*col.setWidth(300);
		col.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				col.pack();
			}
		});*/
		
		//tableLayout.addColumnData(new ColumnWeightData(30, 250, false));
		//TableColumn col2 = new TableColumn(table, SWT.NONE);
		//col2.setText("null2");
		
		final int TEXT_MARGIN = 3;
		table.addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event event) {
				//System.out.println(event.width);
				/*TableItem item = (TableItem) event.item;
				Object obj = item.getData();
				//System.out.println("measure " + obj);
				if (obj instanceof CoreException) {
					event.gc.setFont(font);
					Point size = event.gc.textExtent("A");
					event.gc.setFont(null);
					//event.height = Math.max(event.height, TEXT_MARGIN * 2 + size.y);
					event.height = TEXT_MARGIN * 2 + size.y;
					System.out.println(event.height);
					return;
				}*/
				event.gc.setFont(font);
				Point size = event.gc.textExtent("A");
				int h = TEXT_MARGIN * 3 + size.y;
				size = event.gc.textExtent("A");
				event.gc.setFont(null);
				h += size.y;
				h = Math.max(h, 40 + TEXT_MARGIN * 2);
				//event.width = 400;
				event.height = Math.max(event.height, h);
				//System.out.println(event.height);
				//event.width = 300;
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
				} else if (obj instanceof List) {
					List<String> list = (List<String>) obj;
					
					int size = list.size();
					String[] hosts = new String[size];
					list.toArray(hosts);
					
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < size; i++) {
						if (i > 0)
							sb.append(", ");
						sb.append(hosts[i]);
					}
					String s = NLS.bind(Messages.wizNewInstallableServerSiteError, sb.toString());
					gc.drawText(s, event.x + TEXT_MARGIN, event.y + TEXT_MARGIN, true);
					return;
				}
				IExtension ei = (IExtension) obj;
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
				
				String name = ei.getName();
				String provider = "" + ei.getProvider();
				//String provider = "" + ei.getImage();
				//String provider = "" + ExtensionUtility.getDescription(ei);
				String version = ei.getVersion().toString();
				/*int ind = ver.indexOf("_");
				if (ind >= 0)
					ver = ver.substring(ind+1);
				String version = "v" + ver;*/
				
				//Image image = getImage(ei.getImage());
				Image image = ei.getImage();
				if (image == null)
					image = ImageResource.getImage(ImageResource.IMG_WIZBAN_NEW_SERVER); // TODO
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
				gc.drawText(version, event.x + width - TEXT_MARGIN * 3 - size.x, event.y + yOffset, true);
			}
		});
		
		tableViewer.setSorter(new ViewerSorter() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				if ((e1 instanceof IExtension) && !(e2 instanceof IExtension))
					return -1;
				if (!(e1 instanceof IExtension) && (e2 instanceof IExtension))
					return 1;
				try {
					IExtension f1 = (IExtension) e1;
					IExtension f2 = (IExtension) e2;
					return (f1.getName().compareToIgnoreCase(f2.getName()));
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
				IExtension extension = null;
				if (obj instanceof IExtension)
					extension = (IExtension) obj;
				
				handleSelection(extension);
				if (extension != null)
					description.setText(extension.getDescription());
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
			Image image = images.get(url.toString());
			if (image != null)
				return image;
		} catch (Exception e) {
			// ignore
		}
		try {
			ImageDescriptor id = ImageDescriptor.createFromURL(url);
			Image image = id.createImage();
			if (images == null)
				images = new HashMap<String, Image>();
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

	protected void handleSelection(IExtension extension) {
		listener.extensionSelected(extension);
	}

	protected void deferInitialization() {
		final List<Object> list = Collections.synchronizedList(new ArrayList<Object>());
		list.add(Messages.viewInitializing);
		
		tableViewer.setContentProvider(new ExtensionContentProvider(list));
		tableViewer.setLabelProvider(new ExtensionTableLabelProvider());
		tableViewer.setInput(ROOT);
		
		final Thread t = new Thread("Deferred Initialization") {
			public void run() {
				deferredInitialize(list, new IProgressMonitor() {
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
		final int SLEEP = 100;
		final Runnable[] animator = new Runnable[1];
		animator[0] = new Runnable() {
			public void run() {
				if (t.isAlive()) {
					count++;
					if (!table.isDisposed())
						tableViewer.refresh(ROOT);
					display.timerExec(SLEEP, animator[0]);
				}
			}
		};
		display.timerExec(SLEEP, animator[0]);
	}

	public void deferredInitialize(final List<Object> list, IProgressMonitor monitor) {
		final List<String> failedSites = new ArrayList<String>();
		ExtensionUtility.ExtensionListener listener2 = new ExtensionUtility.ExtensionListener() {
			public void extensionFound(IExtension feature) {
				list.add(feature);
				if (progress != null)
					list.set(0, progress);
			}

			public void extensionRemoved(IExtension feature) {
				list.remove(feature);
			}

			public void siteFailure(String host) {
				synchronized (failedSites) {
					if (!list.contains(failedSites))
						list.add(failedSites);
					failedSites.add(host);
				}
			}
		};
		
		String id = "org.eclipse.wst.server.core.serverAdapter"; // TODO
		try {
			ExtensionUtility.getAllExtensions(id, listener2, monitor);
		} catch (CoreException ce) {
			Trace.trace(Trace.WARNING, "Error downloading server adapter info", ce);
		}
		
		list.remove(0);
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!table.isDisposed())
					tableViewer.refresh(ROOT);
			}
		});
	}

	protected Object getSelection(ISelection sel2) {
		IStructuredSelection sel = (IStructuredSelection) sel2;
		return sel.getFirstElement();
	}
}