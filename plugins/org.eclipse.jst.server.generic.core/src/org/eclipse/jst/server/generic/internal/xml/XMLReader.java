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
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Builds the config objects from their XML definitions
 * 
 * @author B. Görkem ERCAN
 */
public class XMLReader extends DefaultHandler
{
	private IXMLTagProcessor config = null;
	private CharArrayWriter contents = new CharArrayWriter();
	public XMLReader(IXMLTagProcessor buildConfig)
	{
		super();
		this.config = buildConfig;
	}

	/**
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length)
		throws SAXException
	{
		contents.write(ch, start, length);
	}

	/**
	 * @see org.xml.sax.ContentHandler#endElement(String, String, String)
	 */
	public void endElement(String nameSpaceURI, String localName, String qName)
		throws SAXException
	{
		String tagName = ( localName != null && localName.length() > 0) ? localName : qName;
		config.assesTagEnd(tagName, contents.toString());
	}

	/**
	 * @see org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)
	 */
	public void startElement(
		String nameSpaceURI,
		String localName,
		String qName,
		Attributes attributes)
		throws SAXException
	{
		contents.reset();
		String tagName = ( localName != null && localName.length() > 0) ? localName : qName;
		config.assesTagStart(tagName,attributes);
	}

	/**
	 * Method getConfigObject.
	 * @return Object
	 */
	public Object getConfigObject()
	{
		return this.config.getConfigObject();
	}

	public static ServerTypeDefinition getServerDefinition(String fileName)
	{
		try
		{
			return (ServerTypeDefinition) doParse(fileName, new XMLConfiguration());
		}
		catch (Exception e)
		{
			e.printStackTrace();//J2EEPlugin.log(e);
			return null;
		}

	}

		
	private static Object doParse(String fileName, IXMLTagProcessor cf)
		throws
			FileNotFoundException,
			IOException, ParserConfigurationException, SAXException
	{
      
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();;
		saxParserFactory.setValidating(false);
		javax.xml.parsers.SAXParser parser =  saxParserFactory.newSAXParser();
		parser.getXMLReader().setFeature("http://xml.org/sax/features/validation", false);
		ContentHandler contentHandler = new XMLReader(cf);
		parser.getXMLReader().setContentHandler(contentHandler);
		InputSource source = new InputSource(new FileReader(fileName));
		parser.parse(source, new XMLReader(cf));
		return cf.getConfigObject();
	}
	
	private static Object parseString(String content, IXMLTagProcessor cf)
		throws
			FileNotFoundException,
			IOException, ParserConfigurationException, SAXException
	{
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();;
		javax.xml.parsers.SAXParser parser =  saxParserFactory.newSAXParser();
		saxParserFactory.setValidating(false);
		parser.getXMLReader().setFeature("http://xml.org/sax/features/validation", false);
		InputSource source = new InputSource(new CharArrayReader(content.toCharArray()));
		parser.parse(source, new XMLReader(cf));
		return cf.getConfigObject();
	}
	
}
