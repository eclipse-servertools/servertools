/*******************************************************************************
 * Copyright (c) 2004 Eteration Bilisim A.S.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Naci M. Dai - initial API and implementation
 *     
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL ETERATION A.S. OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Eteration Bilisim A.S.  For more
 * information on eteration, please see
 * <http://www.eteration.com/>.
 ***************************************************************************/

package org.eclipse.jst.server.generic.internal.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jst.server.generic.servertype.definition.ArchiveType;
import org.eclipse.jst.server.generic.servertype.definition.Property;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;


public class Resolver {



	private Map fPropertyValues = new HashMap();
	private ServerRuntime server;

	/**
	 * @param impl
	 */
	public Resolver(ServerRuntime runtime) {
		this.server = runtime;
	}
	
	public List resolveClasspathProperties(List cpList)
	{
		ArrayList list = new ArrayList(cpList.size());
		for (int i = 0; i < cpList.size(); i++) {
			ArchiveType item = (ArchiveType) cpList.get(i);
			String cpath = resolveProperties(item.getPath());
			list.add(cpath);
		}
		return list;
	}	
	public String resolveProperties(String proppedString) {
		HashMap cache = new HashMap(getProperties().size());
		Iterator itr = getProperties().iterator();
		while (itr.hasNext()) {
			Property element =(Property) itr.next();
			String value = element.getDefault();
			if(fPropertyValues != null && fPropertyValues.containsKey(element.getId()))
			    value=(String)fPropertyValues.get(element.getId());
			 cache.put(element.getId(), value);
		}
		//String vmPath = install.getInstallLocation().getCanonicalPath();
		//vmPath = vmPath.replace('\\', '/');
		cache.put("jrePath", "JRE");

		String str = resolvePropertiesFromCache(proppedString, cache);
		str = fixPassthroughProperties(str);
		return str;
	}

	/**
	 * @return
	 */
	private List getProperties() {
		return this.server.getProperty();
	}

	/**
	 * @param str
	 * @return
	 */
	private String fixPassthroughProperties(String str) {
		String resolvedString = str;
		if (isPassPropertyLeft(resolvedString)) {
			resolvedString = fixParam(resolvedString);
			resolvedString = fixPassthroughProperties(resolvedString);
		}
		return resolvedString;
	}

	private String resolvePropertiesFromCache(
		String proppedString,
		HashMap cache) {
		String resolvedString = proppedString;
		if (isPropertyLeft(resolvedString)) {
			resolvedString = resolveProperty(resolvedString, cache);
			resolvedString = resolvePropertiesFromCache(resolvedString, cache);
		}
		return resolvedString;
	}

	private boolean isPropertyLeft(String str) {
		return str.indexOf("${") >= 0;
	}
	private boolean isPassPropertyLeft(String str) {
		return str.indexOf("%{") >= 0;
	}

	private String resolveProperty(String proppedString, HashMap cache) {
		String str = proppedString;
		int start = str.indexOf("${");
		int end = str.indexOf("}", start);
		String key = str.substring(start + 2, end);

		return str.substring(0, start)
			+ cache.get(key)
			+ str.substring(end + 1);
	}
	
	private String fixParam(String proppedString) {
		String str = proppedString;
		int start = str.indexOf("%{");
		return str.substring(0, start)
			+ "${"
			+ str.substring(start+2);
	}
	
	/**
	 * @return Returns the fPropertyValues.
	 */
	public Map getPropertyValues() {
		return fPropertyValues;
	}
	/**
	 * @param propertyValues The fPropertyValues to set.
	 */
	public void setPropertyValues(Map propertyValues) {
		fPropertyValues = propertyValues;
	}
}
