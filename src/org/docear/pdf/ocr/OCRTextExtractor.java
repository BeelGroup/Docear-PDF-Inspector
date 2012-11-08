package org.docear.pdf.ocr;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.io.File;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.docear.pdf.image.IDocearPdfImageHandler;
import org.docear.pdf.text.PdfTextEntity;

import sun.awt.image.BytePackedRaster;

import de.intarsys.pdf.content.CSBasicDevice;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.pd.PDCSIndexed;
import de.intarsys.pdf.pd.PDColorSpace;
import de.intarsys.pdf.pd.PDImage;

public class OCRTextExtractor implements IDocearPdfImageHandler {
	TreeMap<PdfTextEntity, StringBuilder> map = new TreeMap<PdfTextEntity, StringBuilder>();
	private String tempFileName = "/tmp/image_"+System.currentTimeMillis()+".png";
	
	public OCRTextExtractor(File file) {
		this.tempFileName  = "/tmp/image_"+file.getName()+".png";
	}

	public void handleImage(CSBasicDevice device, PDImage image) {
		try {
			PDColorSpace colorSpace = image.getColorSpace();
			int colorCount = 1;
			byte[] colorBytes = new byte[2];
			if(colorSpace instanceof PDCSIndexed) {
				colorCount = ((PDCSIndexed)colorSpace).getColorCount();
				colorBytes = ((PDCSIndexed)colorSpace).getColorBytes();
			}
			if(image.getBitsPerComponent() != 1) {
				return;
			}
			//Point2D p = this.graphicsState.transform.transform(new Point2D.Float(0, 0.0f), null);
			boolean flipped = device.getGraphicsState().transform.getScaleY() < 0;
			byte[] buffer = image.getBytes();
			COSDictionary dict = image.cosGetDict();
			
			int width = dict.get(COSName.constant("Width")).asInteger().intValue();
			int height = dict.get(COSName.constant("Height")).asInteger().intValue();
			
			byte[] colorEncodingGray = new byte[]{0, (byte)255};
			int[] decodeI = image.getDecode();
			if(decodeI != null) {
				colorEncodingGray = new byte[]{(byte)(255*decodeI[0]), (byte)(255*decodeI[1])};
			}
			
			IndexColorModel model = new IndexColorModel(1, 2, colorEncodingGray, colorEncodingGray, colorEncodingGray);
			BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY, model);
			Raster raster = new BytePackedRaster(new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE, width, height, 1), new Point(0,0));
			//Graphics2D g2 = bImage.createGraphics();
			int idx = 0;
			
			int h = flipped ? height-2 : 0; 
			int w = 0;
			int scanLine = (int) Math.ceil(width/8f);
			DataBuffer dBuffer = raster.getDataBuffer();
			while(idx < buffer.length) {
					int idxLine = (h*scanLine)+(idx%scanLine);
					dBuffer.setElem(idxLine, (buffer[idx++]));
					
					w += 8;
					if(w >= width) {
						if(flipped) {
							h--;
							if(h < 0) {
								break;
							}
						}
						else {
							h++;
							if(h >= height) {
								break;
							}
						}
						w = 0;
					}				
			}
			bImage.setData(raster);
			File file = new File(this.tempFileName);
			ImageIO.write(bImage, "png", file);
			
			Runtime.getRuntime().exec(new String[]{"/Header_Extraction/ocr/Tesseract-ocr/tesseract.exe", file.getAbsolutePath(), file.getAbsolutePath(), "-l eng","hocr"}).waitFor();
			File htmlFile = new File(file.getParentFile(), file.getName()+".html");
			if(htmlFile.exists()) {
				System.out.println("Hooya");
			}
			//file.delete();
			//htmlFile.delete();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.out.println("holla");
	}

	public TreeMap<PdfTextEntity, StringBuilder> getMap() {
		return map;
	}

}
