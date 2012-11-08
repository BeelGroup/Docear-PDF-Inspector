package org.docear.pdf.image;

import org.docear.pdf.util.HashUtililities;

import de.intarsys.pdf.content.CSBasicDevice;
import de.intarsys.pdf.pd.PDImage;

public class UniqueImageHashExtractor implements IDocearPdfImageHandler {
	
	private String uniqueHash;

	public void handleImage(CSBasicDevice device, PDImage image) {
		byte[] buffer = image.getBytes();
		
//		long time = System.currentTimeMillis();
//		this.uniqueHash = Long.toString(HashUtililities.hashFNV64(buffer), 16);
//		System.out.println("time FNV64: "+(System.currentTimeMillis()-time));
//		System.out.println(this.uniqueHash);
			
//		time = System.currentTimeMillis();
//		this.uniqueHash = Long.toString(HashUtililities.hashBerkeleyDB64(buffer), 16);
//		System.out.println("time BerkeleyDB64: "+(System.currentTimeMillis()-time));
//		System.out.println(this.uniqueHash);
		
//		time = System.currentTimeMillis();
//		this.uniqueHash = Integer.toHexString(Arrays.deepHashCode(new Object[]{buffer}));
//		System.out.println("time default: "+(System.currentTimeMillis()-time));
//		System.out.println(this.uniqueHash);
		
		this.uniqueHash = HashUtililities.hashSHA2(buffer);
	}

	public String getUniqueHash() {
		return uniqueHash;
	}

}
