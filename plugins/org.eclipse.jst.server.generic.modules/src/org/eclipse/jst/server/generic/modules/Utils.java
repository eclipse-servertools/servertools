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

package org.eclipse.jst.server.generic.modules;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


public class Utils {

	public static boolean isValidEarModule(IFolder folder) throws Exception {
		String ePath = "/META-INF/application.xml";
		String ePath2 = "/meta-inf/application.xml";
		return (pathExistsIn(folder, ePath) || pathExistsIn(folder, ePath2));
	}

	public static boolean isValidEjbModule(IFolder folder) throws Exception {
		String ePath = "/META-INF/ejb-jar.xml";
		String ePath2 = "/meta-inf/ejb-jar.xml";
		String bPath = "/META-INF/beans.xml";
		return (pathExistsIn(folder, ePath) || pathExistsIn(folder, ePath2) || pathExistsIn(
				folder, bPath));
	}

	public static boolean isValidModule(IFolder folder) {
		try {
			return (isValidWebModule(folder) || isValidEarModule(folder) || isValidEjbModule(folder));
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isValidWebModule(IFolder folder) throws Exception {
		String wPath = "/WEB-INF/web.xml";
		String wPath2 = "/web-inf/web.xml";
		return (pathExistsIn(folder, wPath) || pathExistsIn(folder, wPath2));
	}

	private static boolean pathExistsIn(IFolder folder, String addToPath) {
		IProject project = folder.getProject();
		IPath folderPath = folder.getProjectRelativePath();
		Path contP = new Path(folderPath.toString() + addToPath);
		IResource er = project.findMember(contP);
		return (er != null && er.exists());
	}

}
