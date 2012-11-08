package org.docear.pdf.image;

import de.intarsys.pdf.content.CSBasicDevice;
import de.intarsys.pdf.pd.PDImage;

public interface IDocearPdfImageHandler {
	public void handleImage(CSBasicDevice device, PDImage image);
}
