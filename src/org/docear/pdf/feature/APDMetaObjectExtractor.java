package org.docear.pdf.feature;

import java.io.IOException;
import java.util.List;

import de.intarsys.pdf.cos.COSNull;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSString;
import de.intarsys.pdf.pd.PDDocument;

public abstract class APDMetaObjectExtractor {
	
	protected final PDDocument document;
	private boolean keepObjectReference = false;
	private boolean dirtyDocument = false;
	
	/***********************************************************************************
	 * CONSTRUCTORS
	 **********************************************************************************/

	public APDMetaObjectExtractor(PDDocument document) {
		if (document == null) {
			throw new IllegalArgumentException("NULL");
		}
		this.document = document;
	}
	/***********************************************************************************
	 * METHODS
	 **********************************************************************************/

	public void setKeepObjectReference(boolean enabled) {
		this.keepObjectReference = enabled;
	}

	public boolean keepObjectReference() {
		return this.keepObjectReference;
	}
	
	public PDDocument getDocument() {
		return this.document;
	}
	
	public void close() throws IOException {
		this.document.close();
	}
	
	protected long getOrCreateUID(COSObjectContext context) {
		context.setCreatedID(false);
		COSObject dcr_uid = context.getCOSObject().cosGetField(APDMetaObject.UNIQUE_IDENTIFIER);
		if (COSNull.NULL.equals(dcr_uid)) {
			dcr_uid = COSString.create(Long.toString(APDMetaObject.createUID()));
			context.getCOSObject().cosSetField(APDMetaObject.UNIQUE_IDENTIFIER, dcr_uid);
			setDocumentModified(true);
			context.setCreatedID(true);
		}
		return Long.parseLong(dcr_uid.stringValue());
	}
	
	public void setDocumentModified(boolean b) {
		dirtyDocument = b;
	}
	
	public boolean isDocumentModified() {
		return dirtyDocument ;
	}
	/***********************************************************************************
	 * REQUIRED METHODS FOR INTERFACES
	 **********************************************************************************/
	
	public abstract List<APDMetaObject> getMetaObjects() throws IOException;
}
