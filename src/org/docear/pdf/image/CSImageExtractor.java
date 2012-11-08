package org.docear.pdf.image;

import java.awt.geom.Rectangle2D;


import de.intarsys.pdf.content.CSException;
import de.intarsys.pdf.content.ICSInterpreter;
import de.intarsys.pdf.content.text.CSCharacterParser;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.font.PDGlyphs;
import de.intarsys.pdf.pd.PDImage;

public class CSImageExtractor extends CSCharacterParser {
	final IDocearPdfImageHandler handler;

	public CSImageExtractor(IDocearPdfImageHandler imageHandler) {
		super();
		this.handler = imageHandler;
		
	}

	@Override
	protected void doImage(COSName name, PDImage image) throws CSException {
		if(handler == null) {
			return;
		}
		handler.handleImage(this, image);
	}

	@Override
	protected void onCharacterFound(PDGlyphs glyphs, Rectangle2D rect) {
		
	}

	@Override
	public void open(ICSInterpreter pInterpreter) {
		super.open(pInterpreter);
	}

}
