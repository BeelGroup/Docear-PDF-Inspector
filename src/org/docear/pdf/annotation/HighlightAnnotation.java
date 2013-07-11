package org.docear.pdf.annotation;

import org.docear.pdf.feature.APDMetaObject;
import org.docear.pdf.feature.AObjectType;

public class HighlightAnnotation extends APDMetaObject {

	public static final AObjectType HIGHTLIGHTED_TEXT = new AObjectType() {
		public String toString() {
			return "HIGHTLIGHTED_TEXT";
		}
	};
	/***********************************************************************************
	 * CONSTRUCTORS
	 **********************************************************************************/
	protected HighlightAnnotation(long uid) {
		super(uid);
		// TODO Auto-generated constructor stub
	}
	
	/***********************************************************************************
	 * METHODS
	 **********************************************************************************/

	/***********************************************************************************
	 * REQUIRED METHODS FOR INTERFACES
	 **********************************************************************************/
	public AObjectType getType() {
		return HIGHTLIGHTED_TEXT;
	}
}
