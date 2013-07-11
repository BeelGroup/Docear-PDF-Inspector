package org.docear.pdf.annotation;

import org.docear.pdf.feature.APDMetaObject;
import org.docear.pdf.feature.AObjectType;

public class CommentAnnotation extends APDMetaObject {

	public static final AObjectType COMMENT = new AObjectType() {
		public String toString() {
			return "COMMENT";
		}
	};
	
	/***********************************************************************************
	 * CONSTRUCTORS
	 **********************************************************************************/
	protected CommentAnnotation(long uid) {
		super(uid);
	}
	
	/***********************************************************************************
	 * METHODS
	 **********************************************************************************/

	/***********************************************************************************
	 * REQUIRED METHODS FOR INTERFACES
	 **********************************************************************************/
	public AObjectType getType() {
		return COMMENT;
	}
}
