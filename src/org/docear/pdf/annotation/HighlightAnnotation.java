package org.docear.pdf.annotation;

import org.docear.pdf.feature.APDMetaObject;
import org.docear.pdf.feature.AObjectType;
import org.docear.pdf.feature.COSObjectContext;

public class HighlightAnnotation extends APDMetaObject {

	public static final AObjectType HIGHTLIGHTED_TEXT = new AObjectType() {
		public String toString() {
			return "HIGHTLIGHTED_TEXT";
		}
	};
	/***********************************************************************************
	 * CONSTRUCTORS
	 * @param context 
	 **********************************************************************************/
	protected HighlightAnnotation(long uid, COSObjectContext context) {
		super(uid, context);
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
