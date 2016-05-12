/**********************************************************************
 * Copyright (c) 2003, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.discovery.Discovery;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ProgressUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
/**
 * Server UI utility methods.
 * <p>
 * This class provides all its functionality through static members.
 * It is not intended to be subclassed or instantiated.
 * </p>
 * @since 1.0
 */
public final class ServerUIUtil {
	/**
	 * Cannot instantiate ServerUIUtil - use static methods.
	 */
	private ServerUIUtil() {
		// can't create
	}

	/**
	 * Open the new runtime wizard. The given typeId and versionId are used to filter
	 * the set of runtimes displayed.
	 * 
	 * @param shell a shell to use when creating the wizard
	 * @param typeId a module type id, or null for any module type
	 * @param versionId a module version, or null for any version
	 * @return <code>true</code> if a runtime was created, or
	 *    <code>false</code> otherwise
	 */
	public static boolean showNewRuntimeWizard(Shell shell, String typeId, String versionId) {
		return ServerUIPlugin.showNewRuntimeWizard(shell, typeId, versionId, null);
	}

	/**
	 * Open the new runtime wizard. The given typeId, versionId, and runtimeTypeId are
	 * used to filter the set of runtimes displayed.
	 * 
	 * @param shell a shell to use when creating the wizard
	 * @param typeId a module type id, or null for any module type
	 * @param versionId a module version, or null for any version
	 * @param runtimeTypeId a server runtime type, or null for any type
	 * @return <code>true</code> if a runtime was created, or
	 *    <code>false</code> otherwise
	 * @since 2.0
	 */
	public static boolean showNewRuntimeWizard(Shell shell, String typeId, String versionId, String runtimeTypeId) {
		return ServerUIPlugin.showNewRuntimeWizard(shell, typeId, versionId, runtimeTypeId);		
	}

	/**
	 * Open the new server wizard.
	 * 
	 * @param shell a shell to use when creating the wizard
	 * @param typeId a module type id, or null for any module type
	 * @param versionId a module version, or null for any version
	 * @param serverTypeId a server runtime type, or null for any type
	 * @return <code>true</code> if a server was created, or
	 *    <code>false</code> otherwise
	 * @since 2.0
	 */
	public static boolean showNewServerWizard(Shell shell, String typeId, String versionId, String serverTypeId) {
		return ServerUIPlugin.showNewServerWizard(shell, typeId, versionId, serverTypeId);
	}
	
	
	public static Job refreshServerNode(final boolean userInitiated){
		class RefreshJob extends Job{
			public RefreshJob() {
				super(Messages.jobRefreshingServerAdapter);
			}
			
			public boolean belongsTo(Object family) {
				return "org.eclipse.wst.server.ui.family".equals(family);
			}
			protected IStatus run(IProgressMonitor monitor) {
				monitor = ProgressUtil.getMonitorFor(monitor);
				monitor.beginTask("", 1000);
				refreshButtonText = Messages.cacheUpdate_refreshing;
				if (cacheUpdateJobListener != null)
					cacheUpdateJobListener.start();
				Discovery.refreshServerAdapters(ProgressUtil.getSubMonitorFor(monitor, 500));
				monitor.worked(700);
				refreshButtonText = Messages.cacheUpdate_refreshNow;
				if (cacheUpdateJobListener!= null)
					cacheUpdateJobListener.done();
				if (monitor.isCanceled())
					return Status.CANCEL_STATUS;
				ServerCore.resetDownloadableServers();
				monitor.worked(900);
				return Status.OK_STATUS;
			}
		}
		if (!userInitiated){
			if (!ServerUIPlugin.getPreferences().getExtAdapter())
				return null;
			int cacheFrequency = ServerUIPlugin.getPreferences().getCacheFrequency();
			if (cacheFrequency == 0 /*Manual*/){
				// User will explicitly refresh
				return null;
			}
			String dateString = Discovery.getLastUpdatedDate();

			DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy kk:mm:ss zzz", Locale.ENGLISH);
			try {
				Date lastUpDatedDate = ((dateString == null) || (dateString.equals(Messages.cacheUpdate_never)))
						? new Date(0) : df.parse(dateString.trim());
				Date currentDate = new Date(System.currentTimeMillis());
				Calendar cal = Calendar.getInstance();
				cal.setTime(lastUpDatedDate);
				switch(cacheFrequency){
				case 1: //daily
					cal.add(Calendar.DATE, 1); // add 1 day
					if (cal.getTime().after(currentDate))
						return null;
					break;
				case 2: //weekly
					cal.add(Calendar.DATE, 7); // add 7 days
					if (cal.getTime().after(currentDate))
						return null;
					break;
				case 3: //monthly
					cal.add(Calendar.MONTH, 1); // add 1 Month
					if (cal.getTime().after(currentDate))
						return null;
					break;
				case 4: //quarterly
					cal.add(Calendar.MONTH, 3); // add 3 Months
					if (cal.getTime().after(currentDate))
						return null;
					break;
				}
			} catch (ParseException e) {
				// The last updated date is malformed. Continue further to do a refresh.
			}
		}

		RefreshJob job = new RefreshJob();
		job.schedule();
		job.setPriority(Job.SHORT);
		return job;

	}
	
	public static String refreshButtonText = Messages.cacheUpdate_refreshNow;
	static ICacheUpdateListener cacheUpdateJobListener;
	
	public static void setListener(ICacheUpdateListener listener){
		cacheUpdateJobListener = listener;
	}
}
