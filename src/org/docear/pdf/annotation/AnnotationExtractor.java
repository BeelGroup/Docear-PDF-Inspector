package org.docear.pdf.annotation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.docear.pdf.feature.APDMetaObject;
import org.docear.pdf.feature.APDObjectDestination;
import org.docear.pdf.feature.CachedPDMetaObjectExtractor;
import org.docear.pdf.feature.PageDestination;

import de.intarsys.pdf.pd.PDAnnotation;
import de.intarsys.pdf.pd.PDAnnotationTools;
import de.intarsys.pdf.pd.PDAnyAnnotation;
import de.intarsys.pdf.pd.PDDocument;
import de.intarsys.pdf.pd.PDHighlightAnnotation;
import de.intarsys.pdf.pd.PDSquigglyAnnotation;
import de.intarsys.pdf.pd.PDStrikeOutAnnotation;
import de.intarsys.pdf.pd.PDTextAnnotation;
import de.intarsys.pdf.pd.PDTextMarkupAnnotation;
import de.intarsys.pdf.pd.PDUnderlineAnnotation;

public class AnnotationExtractor extends CachedPDMetaObjectExtractor {
	private boolean ignoreComments = false;
	private boolean ignoreHighlights = false;
	
	/***********************************************************************************
	 * CONSTRUCTORS
	 **********************************************************************************/
	public AnnotationExtractor(PDDocument document) {
		super(document);
	}
	/***********************************************************************************
	 * METHODS
	 **********************************************************************************/	
	public boolean ignoreComments() {
		return this.ignoreComments;
	}
	
	public void setIgnoreComments(boolean ignore) {
		this.ignoreComments = ignore;
	}
	
	public boolean ignoreHighlights() {
		return this.ignoreHighlights;
	}
	
	public void setIgnoreHighlights(boolean ignore) {
		this.ignoreHighlights = ignore;
	}
	
	public void getMetaObjects(List<APDMetaObject> annotations) throws IOException {
		getAnnotations(annotations);
	}
	
	private void getAnnotations(List<APDMetaObject> annotations) throws IOException {
		if (annotations == null) {
			annotations = new ArrayList<APDMetaObject>();
		}
		
		String lastString = "";
		List<PDAnnotation> annotationList = document.getAnnotations();
		for (PDAnnotation pdAnnotation : annotationList) {
			APDMetaObject metaObj = getMetaObject(pdAnnotation, lastString);
			if(metaObj != null) {
				annotations.add(metaObj);
			}
		}
	}
	
	private APDMetaObject getMetaObject(PDAnnotation annotation, String lastString) {
		// Avoid empty entries
		// support repligo highlights
		if (annotation.getClass() == PDHighlightAnnotation.class) {
			// ignore Highlight if Subject is "Highlight" and Contents is ""
			if (((PDHighlightAnnotation) annotation).getSubject() != null && ((PDHighlightAnnotation) annotation).getSubject().length() > 0
					&& ((PDHighlightAnnotation) annotation).getSubject().equals("Highlight") && annotation.getContents().equals("")) {
				return null;
			}
		} else if (!(annotation.getClass() == PDSquigglyAnnotation.class) && !(annotation.getClass() == PDUnderlineAnnotation.class)
				&& !(annotation.getClass() == PDStrikeOutAnnotation.class) && !(annotation.getClass() == PDTextMarkupAnnotation.class)) {
			// ignore annotations with Contents is ""
			if ("".equals(annotation.getContents())/* && !annotation.isMarkupAnnotation() */) {
				return null;
			}
			
			// Avoid double entries (Foxit Reader)
			if (annotation.getContents().equals(lastString)) {
				return null;
			}
			lastString = annotation.getContents();
		}
		
		APDMetaObject metaObject = getComment(annotation);
		if(metaObject == null) {
			metaObject = getHighlight(annotation);
		}
		return metaObject;

	}
	
	private APDMetaObject getComment(PDAnnotation annotation) {
		if ((annotation.getClass() == PDAnyAnnotation.class || annotation.getClass() == PDTextAnnotation.class) && !ignoreComments()) {
			Integer objectNumber = annotation.cosGetObject().getIndirectObject().getObjectNumber();
			APDMetaObject meta = new CommentAnnotation(getOrCreateUID(annotation));
			meta.setObjectNumber(objectNumber);
			meta.setText(annotation.getContents());
			if (keepObjectReference()) {
				meta.setObjectReference(annotation);
			}
			meta.setDestination(getDestination(annotation));
			return meta;
		}
		return null;
	}
	
	private APDMetaObject getHighlight(PDAnnotation annotation) {
		if ((annotation.getClass() == PDTextMarkupAnnotation.class 
				|| annotation.getClass() == PDHighlightAnnotation.class
				|| annotation.getClass() == PDStrikeOutAnnotation.class 
				|| annotation.getClass() == PDUnderlineAnnotation.class 
				|| annotation.getClass() == PDSquigglyAnnotation.class)
				&& !ignoreHighlights()) {
			Integer objectNumber = annotation.cosGetObject().getIndirectObject().getObjectNumber();
			APDMetaObject meta = new HighlightAnnotation(getOrCreateUID(annotation));
			meta.setObjectNumber(objectNumber);
			// String text = extractAnnotationText(pdPage, (PDTextMarkupAnnotation)annotation);
			// prefer Title from Contents (So updates work)
			if (annotation.getContents() != null && annotation.getContents().length() > 0) {
				meta.setText(annotation.getContents());
			}
			// then try to extract the text from the bounding rectangle
			/*
			 * else if(!text.isEmpty()){ pdfAnnotation.setTitle(text); }
			 */
			else {
				// support repligo highlights
				// set Title to Subject per repligo
				if (annotation.getClass() == PDHighlightAnnotation.class) {
					String subject = ((PDHighlightAnnotation) annotation).getSubject();
					if (subject != null && subject.length() > 0) {
						if (!subject.equalsIgnoreCase("Highlight") && !subject.equalsIgnoreCase("Hervorheben")) {
							meta.setText(((PDHighlightAnnotation) annotation).getSubject());
						}
						else {
							return null;
						}
					}
				}
			}

			if (keepObjectReference()) {
				meta.setObjectReference(annotation);
			}
			
			if (meta.getText() == null) {
				return null;
			}
			meta.setDestination(getDestination(annotation));
			return meta;
		}
		return null;
	}
	
	private APDObjectDestination getDestination(PDAnnotation annotation) {
		return new PageDestination(PDAnnotationTools.getPage(annotation).getNodeIndex() + 1);
	}
	
	/***********************************************************************************
	 * REQUIRED METHODS FOR INTERFACES
	 **********************************************************************************/
}
