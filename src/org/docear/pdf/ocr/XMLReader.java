package org.docear.pdf.ocr;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.docear.pdf.text.PdfTextEntity;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import sun.reflect.ReflectionFactory;

public class XMLReader {
	
	private TreeMap<PdfTextEntity, StringBuilder> map = new TreeMap<PdfTextEntity, StringBuilder>();
	private PdfTextEntity current = null; 

	/**********************************************************
	 * constructor
	 **********************************************************/

	

	/**********************************************************
	 * methods
	 * 
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 **********************************************************/
	public TreeMap<PdfTextEntity, StringBuilder> read(InputStream stream) throws ParserConfigurationException, SAXException, IOException {
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		XMLSerializableDomHandler confHandler = new XMLSerializableDomHandler();
		parser.parse(stream, confHandler);
		return map;
	}

	/**********************************************************
	 * required by interfaces
	 **********************************************************/

	private class XMLSerializableDomHandler extends DefaultHandler {
		private LinkedList<Object> openElementStack = new LinkedList<Object>();
		private LinkedList<Object> closedElementStack = new LinkedList<Object>();
		private HashMap<Object, Map<String, String>> elementAttributesMap = new HashMap<Object, Map<String, String>>();
		
		public void startDocument() throws SAXException {
			map = new TreeMap<PdfTextEntity, StringBuilder>();
		}

		public void endDocument() throws SAXException {

		}

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			try {				
				

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private Map<String, String> extractAttributes(Attributes attributes) {
			Map<String, String> attr = new HashMap<String, String>();
			for(int i=0; i < attributes.getLength(); i++) {
				attr.put(attributes.getQName(i), attributes.getValue(i));
			}
			return attr;
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			Object obj = openElementStack.pop();
			closedElementStack.add(obj);		
		}

		public void characters(char[] ch, int start, int length) throws SAXException {
			StringBuilder builder = map.get(current);
			if(builder == null) {
				builder = new StringBuilder();
				map.put(current, builder);
			}
			builder.append(ch, start, length);
		}
	}

}
