package org.docear.pdf.feature;

import de.intarsys.pdf.cos.COSBasedObject;

public class COSObjectContext {

	private final COSBasedObject object;
	private boolean createdID = false;

	/***********************************************************************************
	 * CONSTRUCTORS
	 **********************************************************************************/
	
	public COSObjectContext(COSBasedObject object) {
		this.object = object;
	}
	
	/***********************************************************************************
	 * METHODS
	 **********************************************************************************/

	public COSBasedObject getCOSObject() {
		return this.object;
	}
	
	public void setCreatedID(boolean isNew) {
		this.createdID  = isNew;
	}
	
	public boolean isNewID() {
		return this.createdID;
	}
	/***********************************************************************************
	 * REQUIRED METHODS FOR INTERFACES
	 **********************************************************************************/
}
