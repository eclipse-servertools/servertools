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

public class ServerTypeDefinitionProperty {
	public static final int TYPE_STRING = 0;
	public static final int TYPE_FILE = 1;
	public static final int TYPE_DIRECTORY = 2;
	public static final int TYPE_BOOLEAN = 3;

	private String id;
	private String label;
	private int type;
	private String defaultValue;

	/**
	 * @return String
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @return String
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return String
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return int
	 */
	public int getType() {
		return type;
	}

	/**
	 * Sets the defaultValue.
	 * @param defaultValue The defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Sets the id.
	 * @param id The id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Sets the label.
	 * @param label The label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Sets the type.
	 * @param type The type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
	public void setTypeFromStr(String str) {
		if ("string".equals(str))
			this.type = TYPE_STRING;
		else if ("directory".equals(str))
			this.type = TYPE_DIRECTORY;
		else if ("file".equals(str))
			this.type = TYPE_FILE;
		else if ("boolean".equals(str))
			this.type = TYPE_BOOLEAN;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "\n<property id=\""
			+ this.getId()
			+ "\"\n\tlabel=\""
			+ this.getLabel()
			+ "\"\n\ttype=\""
			+ (type == TYPE_DIRECTORY
				? "directory"
				: (type == TYPE_STRING
					? "string"
					: (type == TYPE_FILE
						? "file"
						: (type == TYPE_BOOLEAN ? "boolean" : "undefined"))))
			+ "\"\n\tdefault=\""
			+ this.getDefaultValue()
			+ "\" />";
	}

}
