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
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
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

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;

public class ClasspathItem {
	public static final int VARIABLE = 0;
	public static final int ABSOLUTE = 1;

	int type;
	String classpath;

	/**
	 * @return String
	 */
	public String getClasspath() {
		return classpath;
	}

	/**
	 * @return int
	 */
	public int getType() {
		return type;
	}

	/**
	 * Sets the classpath.
	 * @param classpath The classpath to set
	 */
	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}

	/**
	 * Sets the type.
	 * @param type The type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
	public void setTypeStr(String typeStr) {
		if ("variable".equals(typeStr)) {
			type = VARIABLE;
		} else if ("absolute".equals(typeStr)) {
			type = ABSOLUTE;
		}

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "<jar type=\""
			+ (type == 0 ? "variable" : "absolute")
			+ "\">"
			+ this.getClasspath()
			+ "</jar>";
	}

	public boolean exists(ServerTypeDefinition def) {
		if (getType() == VARIABLE) {
			String resolved = def.resolveProperties(this.getClasspath());
			IClasspathEntry entry =
				JavaCore.newVariableEntry(new Path(resolved), null, null);

			IClasspathEntry res = JavaCore.getResolvedClasspathEntry(entry);
			return (res != null);
		} else {
			File f = new File(def.resolveProperties(this.getClasspath()));
			return f.exists();
		}
	}

	public void setRawClasspathFor(ServerTypeDefinition def, String rawPath) {
		File f = new File(rawPath);
		if (!f.exists())
			return;
		try {
			String path = f.getCanonicalPath().replace('\\', '/');
			String var = def.getClasspathVariable();
			int start = path.indexOf(var);
			if (start >= 0) {
				path =
					"${classPathVariableName}"
						+ path.substring(start + var.length());
				setType(VARIABLE);
				setClasspath(path);

			} else {
				setType(ABSOLUTE);
				setClasspath(path);
			}
		} catch (IOException e) {
			setType(ABSOLUTE);
			setClasspath(rawPath);
		}

	}

}
