package org.docear.pdf.feature;

import java.io.File;
import java.io.IOException;

import de.intarsys.pdf.cos.COSDocument;
import de.intarsys.pdf.pd.PDDocument;
import de.intarsys.tools.locator.FileLocator;

public class ADocumentCreator {
	/***********************************************************************************
	 * CONSTRUCTORS
	 **********************************************************************************/

	/***********************************************************************************
	 * METHODS
	 **********************************************************************************/

	public static COSDocument getDocument(File file) throws IOException {
		FileLocator locator = new FileLocator(file);
		try {
			return COSDocument.createFromLocator(locator, null);
		}
		catch (Exception e) {
			throw new IOException(e);
		}
		finally {
			locator = null;
		}
	}
	
	public static PDDocument getPDDocument(COSDocument cosDoc) throws IOException {
		try {
			return PDDocument.createFromCos(cosDoc);
		}
		catch (Exception e) {
			if(cosDoc != null) {
				cosDoc.close();
			}
			throw new IOException(e);
		}
		
	}

	public static PDDocument getPDDocument(File file) throws IOException {
		return getPDDocument(getDocument(file));
	}
	
	/***********************************************************************************
	 * REQUIRED METHODS FOR INTERFACES
	 **********************************************************************************/
}
