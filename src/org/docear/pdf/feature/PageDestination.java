package org.docear.pdf.feature;

public class PageDestination extends APDObjectDestination {
	
	private final int pageNumber;

	/***********************************************************************************
	 * CONSTRUCTORS
	 **********************************************************************************/
	public PageDestination(int pageNumber) {
		if(pageNumber < 0) {
			throw new IllegalArgumentException("page numbers lower than 0 are not allowed");
		}
		this.pageNumber = pageNumber;
	}
	/***********************************************************************************
	 * METHODS
	 **********************************************************************************/
	
	public int getPage() {
		return this.pageNumber;
	}
	/***********************************************************************************
	 * REQUIRED METHODS FOR INTERFACES
	 **********************************************************************************/
}
