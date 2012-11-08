package org.docear.pdf.text;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.TreeMap;

import org.docear.pdf.util.HashUtililities;


import de.intarsys.pdf.content.ICSInterpreter;
import de.intarsys.pdf.content.text.CSCharacterParser;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.font.PDFont;
import de.intarsys.pdf.font.PDGlyphs;

public class CSFormatedTextExtractor extends CSCharacterParser {
	TreeMap<PdfTextEntity, StringBuilder> map = new TreeMap<PdfTextEntity, StringBuilder>();
	StringBuilder current;
	StringBuilder hashRelevant = new StringBuilder();
	
	private double maxDX = 5;
	private double maxDY = 5;
	private int line = 0;
	private boolean foundAbstract = false;
	private int limit = 1024;

	
	public CSFormatedTextExtractor() {
		super();
	}

	private void append(char[] chars) {		
		if(hashRelevant.length() < limit) {
			hashRelevant.append(chars);
		}
		if(!foundAbstract  && hashRelevant.toString().toLowerCase().contains("abstract")) {
			hashRelevant = new StringBuilder();
			foundAbstract = true;
			//limit = 256;
		}
		
		if(current == null) {
			return;
		}
		for (char c : chars) {
			try {
				if(current.length() > 0 && current.charAt(current.length()-1) <= 32) {
					while(current.length() > 0 && current.charAt(current.length()-1) <= 32) {
						current.deleteCharAt(current.length()-1);
					}
					current.append(" ");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(c >= 32 ) {
				current.append(c);
			}
		}
		
	}

	private void append(String s) {
		append(s.toCharArray());
	}

	public TreeMap<PdfTextEntity, StringBuilder> getMap() {
		return map;
	}
	
	public String getHash() {
		if(hashRelevant != null) {
			String text = hashRelevant.toString().trim();
			if(!text.isEmpty()) {
				return HashUtililities.hashSHA2(text);
			}
		}
		return null;
	}

	@Override
	protected void onCharacterFound(PDGlyphs glyphs, Rectangle2D rect) {
		char[] chars = glyphs.getChars();
		if (chars == null) {
			chars = new char[] { ' ' };
		}
		
		double dX = Math.abs(lastStopX - lastStartX);
		double dY = Math.abs(lastStopY - lastStartY);		
		if (dX < maxDX) {
			if (dY > maxDY && current != null && current.length() > 0) {
				append(" ");
				line+= Math.round(dY/rect.getHeight());
				
			}
		} else {
			if (current != null && current.length() > 0) {
				if (dY < maxDY) {
					append(" ");
				} else {
					append(" ");
					line+= Math.round(dY/rect.getHeight());
				}
			}
		}
		if(rect.getWidth() <= 0) {
			return;
		}
		PdfTextEntity entity = new PdfTextEntity(glyphs.getFont(), rect.getHeight(), line);
		current = map.get(entity);
		if(current == null) {
			current = new StringBuilder();
			map.put(entity, current);
		}
		append(chars);
	}

	@Override
	public void open(ICSInterpreter pInterpreter) {
		super.open(pInterpreter);
		map = new TreeMap<PdfTextEntity, StringBuilder>();
		line = 0;
	}

	@Override
	public void textSetFont(COSName name, PDFont font, float size) {
		super.textSetFont(name, font, size);
		AffineTransform tx;
		tx = (AffineTransform) getDeviceTransform().clone();
		tx.concatenate(textState.globalTransform);
		maxDX = textState.fontSize * 0.2 * tx.getScaleX();
		maxDY = textState.fontSize * 0.6 * tx.getScaleY();
	
//		if(font instanceof PDFontType3) {
//			//current = null;
//		//	return;
//		}
//		PdfTextEntity entity = new PdfTextEntity(font, size, line);
//		current = map.get(entity);
//		if(current == null) {
//			current = new StringBuilder();
//			map.put(entity, current);
//		}
	}

	@Override
	public void textSetTransform(float a, float b, float c, float d, float e, float f) {
		super.textSetTransform(a, b, c, d, e, f);
		AffineTransform tx;
		tx = (AffineTransform) getDeviceTransform().clone();
		tx.concatenate(textState.globalTransform);
		maxDX = textState.fontSize * 0.2 * tx.getScaleX();
		maxDY = textState.fontSize * 0.6 * tx.getScaleY();
	}

}
