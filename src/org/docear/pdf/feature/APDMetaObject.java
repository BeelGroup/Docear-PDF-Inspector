package org.docear.pdf.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.pd.PDObject;


public abstract class APDMetaObject {
	public static final COSName UNIQUE_IDENTIFIER = COSName.constant("dcr_uid");
	public static final Random random = new Random();
	private final long uid;
	private final COSObjectContext context;
	private int objectNumber = -1;
	private String text;
	private APDObjectDestination destination;
	private List<APDMetaObject> children;
	
	
	protected APDMetaObject(long uid, COSObjectContext context) {
		this.uid  = uid;
		this.context = context;
	}
	public abstract AObjectType getType();
	
	public static long createUID() {
		return ((long)random.nextInt(Integer.MAX_VALUE) << 32) + random.nextInt(Integer.MAX_VALUE);
	}
	
	public long getUID() {
		return this.uid;
	}
	
	public void setObjectNumber(int number) {
		this.objectNumber = number;
	}
	
	public int getObjectNumber() {
		return this.objectNumber;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public PDObject getObjectReference() {
		if(context == null) {
			return null;
		}
		return (PDObject) context.getCOSObject();
	}
	
	public void setDestination(APDObjectDestination dest) {
		this.destination = dest;
	}
	
	public APDObjectDestination getDestination() {
		return this.destination;
	}
	
	public boolean hasChildren() {
		return children != null && children.size() > 0;
	}
	
	public int getChildCount() {
		return children == null ? 0 : children.size();
	}
	
	public List<APDMetaObject> getChildren() {
		if(children == null) {
			children = new ArrayList<APDMetaObject>();
		}
		return children;
	}
	
	public COSObjectContext getContext() {
		return this.context;
	}
}
