package org.docear.pdf.annotation;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.docear.pdf.feature.APDMetaObject;
import org.docear.pdf.feature.APDObjectDestination;
import org.docear.pdf.feature.COSObjectContext;
import org.docear.pdf.feature.CachedPDMetaObjectExtractor;
import org.docear.pdf.feature.PageDestination;

import de.intarsys.pdf.cds.CDSRectangle;
import de.intarsys.pdf.content.CSDeviceBasedInterpreter;
import de.intarsys.pdf.content.text.CSTextExtractor;
import de.intarsys.pdf.pd.PDAnnotation;
import de.intarsys.pdf.pd.PDAnnotationTools;
import de.intarsys.pdf.pd.PDAnyAnnotation;
import de.intarsys.pdf.pd.PDDocument;
import de.intarsys.pdf.pd.PDHighlightAnnotation;
import de.intarsys.pdf.pd.PDPage;
import de.intarsys.pdf.pd.PDSquigglyAnnotation;
import de.intarsys.pdf.pd.PDStrikeOutAnnotation;
import de.intarsys.pdf.pd.PDTextAnnotation;
import de.intarsys.pdf.pd.PDTextMarkupAnnotation;
import de.intarsys.pdf.pd.PDUnderlineAnnotation;
import de.intarsys.pdf.tools.kernel.PDFGeometryTools;

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
			COSObjectContext context = new COSObjectContext(annotation);
			APDMetaObject meta = new CommentAnnotation(getOrCreateUID(context), context);
			meta.setObjectNumber(objectNumber);
			meta.setText(annotation.getContents());
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
			COSObjectContext context = new COSObjectContext(annotation);
			APDMetaObject meta = new HighlightAnnotation(getOrCreateUID(context), context);
			meta.setObjectNumber(objectNumber);
			
			PDTextMarkupAnnotation markupAnnotation = (PDTextMarkupAnnotation)annotation;
			float[] quadpoints = markupAnnotation.getQuadPoints();				
			if(quadpoints.length % 8 == 0){
				StringBuilder sb = new StringBuilder();
				for(int i = 0; i < quadpoints.length / 8; i++){
					CDSRectangle rect = new CDSRectangle(quadpoints[8*i+4], quadpoints[8*i+5], quadpoints[8*i+2], quadpoints[8*i+3]);
					CSTextExtractor textExtractor = new CSTextExtractor();
					textExtractor.setBounds(rect.toRectangle());
					PDPage page = annotation.getPage();
		            AffineTransform pageTx = new AffineTransform();
		            PDFGeometryTools.adjustTransform(pageTx, page);
		            textExtractor.setDeviceTransform(pageTx);
		            CSDeviceBasedInterpreter interpreter = new CSDeviceBasedInterpreter(null, textExtractor);
		            interpreter.process(page.getContentStream(), page.getResources());
		            sb.append(textExtractor.getContent());
		            if(i + 1 < quadpoints.length / 8){
		            	sb.append(System.getProperty("line.separator"));
		            }
				}
				if(!sb.toString().isEmpty()) 
					meta.setText(sb.toString());
			}								
			// String text = extractAnnotationText(pdPage, (PDTextMarkupAnnotation)annotation);
			// prefer Title from Contents (So updates work)
			if (annotation.getContents() != null && annotation.getContents().length() > 0 && meta.getText().isEmpty()) {
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
