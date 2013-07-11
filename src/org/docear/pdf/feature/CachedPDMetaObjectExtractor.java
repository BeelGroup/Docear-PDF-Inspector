package org.docear.pdf.feature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.intarsys.pdf.pd.PDDocument;

public abstract class CachedPDMetaObjectExtractor extends APDMetaObjectExtractor {
	
	
	protected List<APDMetaObject> cacheList;
	
	/***********************************************************************************
	 * CONSTRUCTORS
	 **********************************************************************************/
	protected CachedPDMetaObjectExtractor(PDDocument document) {
		super(document);
	}
	
	/***********************************************************************************
	 * METHODS
	 **********************************************************************************/
	public void resetAll() {
		synchronized (this) {
			if(cacheList != null) {
				cacheList.clear();
				cacheList = null;
			}
		}
	}
	
	public APDMetaObject findByUID(long uid) throws IOException {
		for(APDMetaObject meta : getMetaObjects()) {
			if(meta.getUID() == uid) {
				return meta;
			}
		}
		return null;
	}
	
	public APDMetaObject findByObjectNumber(int objectNumber) throws IOException {
		for(APDMetaObject meta : getMetaObjects()) {
			if(meta.getObjectNumber() == objectNumber) {
				return meta;
			}
		}
		return null;
	}
	
	public List<APDMetaObject> getMetaObjects() throws IOException {
		synchronized (this) {
			if(cacheList == null) {
				cacheList = new ArrayList<APDMetaObject>();
			}
			getMetaObjects(cacheList);
			return Collections.unmodifiableList(cacheList);
		}
	}
	/***********************************************************************************
	 * REQUIRED METHODS FOR INTERFACES
	 **********************************************************************************/
	
	protected abstract void getMetaObjects(List<APDMetaObject> cache) throws IOException;
}
