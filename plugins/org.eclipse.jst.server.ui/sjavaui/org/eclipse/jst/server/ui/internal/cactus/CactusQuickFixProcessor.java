/*******************************************************************************
 * Copyright (c) 2006 BEA Systems, Inc. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel R. Somerfield - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.ui.internal.cactus;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickFixProcessor;
import org.eclipse.jst.server.ui.internal.JavaServerUIPlugin;

public class CactusQuickFixProcessor implements IQuickFixProcessor {

	public boolean hasCorrections(ICompilationUnit unit, int problemId) {
		return IProblem.ImportNotFound == problemId;
	}

	public IJavaCompletionProposal[] getCorrections(IInvocationContext context,
			IProblemLocation[] locations) throws CoreException {
		if (isCactusProblem(context, locations))
			return new IJavaCompletionProposal[] { new CactusAddLibrariesProposal(context) };
		
		return new IJavaCompletionProposal[0];
	}

	private boolean isCactusProblem(IInvocationContext context, IProblemLocation[] locations) {
		ICompilationUnit unit = context.getCompilationUnit();
		for (int i = 0; i < locations.length; i++) {
			IProblemLocation location = locations[i];
			try {
				String s = getStringUntilWhiteSpaceOrEnd(location.getOffset(), unit);
				if ("ServletTestCase".equals(s) || s.indexOf("org.apache.cactus") >= 0)
					return true;
			} catch (JavaModelException e) {
				JavaServerUIPlugin.log(e);
			}
		}
		return false;
	}

	private String getStringUntilWhiteSpaceOrEnd(int offset, ICompilationUnit unit) throws JavaModelException {
		StringBuffer builder = new StringBuffer();
		IBuffer buffer = unit.getBuffer();
		int length = buffer.getLength();

		for (int index = offset; index < length; index++) {
			char c = buffer.getChar(index);
			if (Character.isWhitespace(c))
				return builder.toString();
			
			builder.append(c);
		}
		return builder.toString();
	}
}