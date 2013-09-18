package org.docear.pdf.bookmark;

import org.docear.pdf.feature.APDMetaObject;
import org.docear.pdf.feature.AObjectType;
import org.docear.pdf.feature.COSObjectContext;

public class Bookmark extends APDMetaObject {
	public static final AObjectType BOOKMARK = new AObjectType() {
		public String toString() {
			return "BOOKMARK";
		}
	};
	
	public static final AObjectType BOOKMARK_WITH_URI = new AObjectType() {
		public String toString() {
			return "BOOKMARK_WITH_URI";
		}
	};
	
	public static final AObjectType BOOKMARK_WITHOUT_DESTINATION = new AObjectType() {
		public String toString() {
			return "BOOKMARK_WITHOUT_DESTINATION";
		}
	};

	
	private AObjectType type;
	
	/***********************************************************************************
	 * CONSTRUCTORS
	 * @param context 
	 **********************************************************************************/
	
	public Bookmark(long uid, COSObjectContext context) {
		super(uid, context);
		
	}
	
	/***********************************************************************************
	 * METHODS
	 **********************************************************************************/

	public void setType(AObjectType type) {
		if(type == null || !(BOOKMARK.equals(type) || BOOKMARK_WITHOUT_DESTINATION.equals(type) || BOOKMARK_WITH_URI.equals(type))) {
			throw new IllegalArgumentException("illegal IAnnotationType: "+type);
		}
		this.type = type;
	}
	
	/***********************************************************************************
	 * REQUIRED METHODS FOR INTERFACES
	 **********************************************************************************/
	
	@Override
	public AObjectType getType() {
		return type;
	}
}
