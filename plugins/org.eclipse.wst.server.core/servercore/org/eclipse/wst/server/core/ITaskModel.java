package org.eclipse.wst.server.core;
/**
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface ITaskModel {
	public static final String TASK_RUNTIME = "runtime";
	public static final String TASK_SERVER = "server";
	public static final String TASK_SERVER_CONFIGURATION = "server-configuration";

	public Object getObject(String id);
	
	public void putObject(String id, Object obj);
}