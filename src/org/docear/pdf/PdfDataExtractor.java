package org.docear.pdf;


import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.docear.pdf.image.CSImageExtractor;
import org.docear.pdf.image.IDocearPdfImageHandler;
import org.docear.pdf.image.UniqueImageHashExtractor;
import org.docear.pdf.ocr.OCRTextExtractor;
import org.docear.pdf.text.CSFormatedTextExtractor;
import org.docear.pdf.text.PdfTextEntity;
import org.docear.pdf.util.CharSequenceFilter;
import org.docear.pdf.util.ReplaceLigaturesFilter;

import de.intarsys.pdf.content.CSDeviceBasedInterpreter;
import de.intarsys.pdf.content.CSException;
import de.intarsys.pdf.content.text.CSTextExtractor;
import de.intarsys.pdf.cos.COSInfoDict;
import de.intarsys.pdf.cos.COSRuntimeException;
import de.intarsys.pdf.parser.COSLoadException;
import de.intarsys.pdf.pd.PDDocument;
import de.intarsys.pdf.pd.PDPage;
import de.intarsys.pdf.pd.PDPageNode;
import de.intarsys.pdf.pd.PDPageTree;
import de.intarsys.pdf.tools.kernel.PDFGeometryTools;
import de.intarsys.tools.locator.FileLocator;

public class PdfDataExtractor {
	private CharSequenceFilter filter = new ReplaceLigaturesFilter();
	private final File file; 
	private String uniqueHash = null;
	
	public PdfDataExtractor(URI filePath) {
		this(new File(filePath));		
	}
	
	public PdfDataExtractor(File file) {
		if(file == null) {
			throw new IllegalArgumentException("NULL");
		}
		this.file = file;
	}
	
	public String extractPlainText() throws IOException {
		PDDocument document;
		try {
			document = getPDDocument();
		} catch (Exception ex) {
			throw new IOException(ex);
		}	
		StringBuilder sb = new StringBuilder();
		try {
			extractText(document.getPageTree(), sb);
		} finally {
			document.close();
		}
		return sb.toString();
	}
	
	private void extractText(PDPageTree pageTree, StringBuilder sb) {
		for (Iterator<?> it = pageTree.getKids().iterator(); it.hasNext();) {
			PDPageNode node = (PDPageNode) it.next();
			if (node.isPage()) {
				try {
					CSTextExtractor extractor = new CSTextExtractor();
					PDPage page = (PDPage) node;
					AffineTransform pageTx = new AffineTransform();
					PDFGeometryTools.adjustTransform(pageTx, page);
					extractor.setDeviceTransform(pageTx);
					CSDeviceBasedInterpreter interpreter = new CSDeviceBasedInterpreter(null, extractor);
					interpreter.process(page.getContentStream(), page.getResources());
					sb.append(extractor.getContent());
				} catch (CSException e) {
					e.printStackTrace();
				}
			} else {
				extractText((PDPageTree) node, sb);
			}
		}
	}
		
	public String extractTitle() throws IOException {
		int TITLE_MIN_LENGTH = 2;
		String title = null;
		PDDocument document;
		try {
			document = getPDDocument();
		} catch (Exception ex) {
			throw new IOException(ex);
		}
		try {			
			PDPage page = document.getPageTree().getFirstPage();
			
			if (page.isPage()) {
				try {
					if(!page.cosGetContents().basicIterator().hasNext()) {
						page = page.getNextPage();
					}
									
					TreeMap<PdfTextEntity, StringBuilder> map = tryTextExtraction(page);
					Entry<PdfTextEntity, StringBuilder> entry = map.firstEntry();
					if(entry == null) {
						OCRTextExtractor handler = new OCRTextExtractor(file);
						//tryImageExtraction(page, handler);
						map = handler.getMap();
						entry = map.firstEntry();
						if(entry == null) {
							COSInfoDict info = document.getInfoDict();
							title = info.getTitle();
						}
					}
					else {
						title = entry.getValue().toString().trim();
						while(title.trim().length() < TITLE_MIN_LENGTH || isNumber(title)) {
							entry = map.higherEntry(entry.getKey());
							if(entry == null) {
								break;
							}
							title = entry.getValue().toString().trim();
						}
						if(title.trim().length() < TITLE_MIN_LENGTH || isNumber(title)) {
							title = null;
						}
					}
					//System.out.println(map);
				}
				catch (Exception ex) {
					COSInfoDict info = document.getInfoDict();
					if (info != null) {
						title = info.getTitle();
					}
				}
			
				
			}				
		}
		finally {
			document.close();
		}
		if(title != null) {
			try {
				title = filter.filter(title);
			} catch (IOException e) {
			}
		}
		return title;
	}

	private void onlyHashExtraction() throws IOException {
		PDDocument document;
		try {
			document = getPDDocument();
		} catch (Exception ex) {
			throw new IOException(ex);
		}
		try {			
			PDPage page = document.getPageTree().getFirstPage();			
			if (page.isPage()) {
				try {					
					if(!page.cosGetContents().basicIterator().hasNext()) {
						page = page.getNextPage();
					}
					TreeMap<PdfTextEntity, StringBuilder> map = tryTextExtraction(page);
					Entry<PdfTextEntity, StringBuilder> entry = map.firstEntry();
					if(entry == null) {
						UniqueImageHashExtractor handler = new UniqueImageHashExtractor();
						tryImageExtraction(page, handler);
						uniqueHash = handler.getUniqueHash();
						
					}
				}
				catch (Exception ex) {
				}		
			}				
		}
		finally {
			document.close();
		}
	}

	private TreeMap<PdfTextEntity, StringBuilder> tryTextExtraction(PDPage page) {
		CSFormatedTextExtractor extractor = new CSFormatedTextExtractor();
								
		AffineTransform pageTx = new AffineTransform();
		PDFGeometryTools.adjustTransform(pageTx, page);
		extractor.setDeviceTransform(pageTx);
		CSDeviceBasedInterpreter interpreter = new CSDeviceBasedInterpreter(null, extractor);
		interpreter.process(page.getContentStream(), page.getResources());
		TreeMap<PdfTextEntity, StringBuilder> map = extractor.getMap();
		uniqueHash = extractor.getHash();
		return map;
	}
	
	private void tryImageExtraction(PDPage page, IDocearPdfImageHandler imageHandler) {
		CSImageExtractor ocrExtractor = new CSImageExtractor(imageHandler);
		CSDeviceBasedInterpreter interpreter = new CSDeviceBasedInterpreter(null, ocrExtractor);
		interpreter.process(page.getContentStream(), page.getResources());
	}
		
	public String getUniqueHashCode() throws IOException {
		if(uniqueHash == null) {
			onlyHashExtraction();
		}
		if(this.uniqueHash == null) {
			return null;
		}
		return this.uniqueHash.toUpperCase();
	}
	
	private boolean isNumber(String title) {
		try {
			Double.parseDouble(title.trim());
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}

	private PDDocument getPDDocument() throws IOException,	COSLoadException, COSRuntimeException {
		FileLocator locator = new FileLocator(this.file);		
		PDDocument document = PDDocument.createFromLocator(locator);
		locator = null;
		return document;
	}
}
