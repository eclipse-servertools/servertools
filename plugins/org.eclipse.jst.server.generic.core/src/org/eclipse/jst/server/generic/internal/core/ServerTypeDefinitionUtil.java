package org.eclipse.jst.server.generic.internal.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.server.generic.core.CorePlugin;
import org.eclipse.jst.server.generic.internal.xml.ClasspathItem;
import org.eclipse.jst.server.generic.internal.xml.ServerTypeDefinition;
import org.eclipse.wst.server.core.IRuntime;

/**
 * Utility for working with rthe server type definition. 
 * This class has only static methods.
 * 
 * @author Gorkem Ercan
 */
public class ServerTypeDefinitionUtil 
{
	/**
	 * 
	 * @param runtime
	 * @return
	 */
	public static ServerTypeDefinition getServerTypeDefinition(IRuntime runtime)
	{
		String serverType = runtime.getAttribute(GenericServerRuntime.SERVER_DEFINITION_ID,(String)null);
		Map properties = runtime.getAttribute(GenericServerRuntime.SERVER_INSTANCE_PROPERTIES,(Map)null);
		ServerTypeDefinition definition = 
			CorePlugin.getDefault().getServerTypeDefinitionManager().getServerRuntimeDefinition(serverType,properties);
		return definition;
	}
	
	public static IClasspathEntry[] getServerClassPathEntry(IRuntime runtime)
	{
		ServerTypeDefinition definition = getServerTypeDefinition(runtime);		
		List cpathList =definition.getServerClassPath();
		ArrayList entryList = new ArrayList();
		for (int i = 0; i < cpathList.size(); i++){
			String item = (String) cpathList.get(i);
			IClasspathEntry entry = JavaCore.newLibraryEntry(new Path(item),null,null );
			entryList.add(entry);
		}
	
		return (IClasspathEntry[])entryList.toArray(new IClasspathEntry[entryList.size()]);
	}
	
}
