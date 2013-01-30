package org.docear.pdf;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.docear.pdf.options.Configuration;
import org.docear.pdf.options.OptionParser;

public class Main {
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Configuration config = new OptionParser(args).getConfiguration();
		try {
			PrintStream out = config.getPrintStream();
						
			for (File file : config.getInFiles()) {		
				boolean empty = true;
				StringBuilder sb = new StringBuilder();
				
				PdfDataExtractor extractor = new PdfDataExtractor(file);
				
				if (config.isIncludeFilename()) {					
						sb.append(file.getAbsolutePath());
						empty = false;
				}
				if (config.isExtractHash()) {
					if (!empty) {
						sb.append(config.getDelimiter());
						empty = false;
					}
					try {
						String hash = extractor.getUniqueHashCode();
						sb.append(hash);
					}
					catch (IOException e) {
						System.err.println("Could not extract hash for " + file.getAbsolutePath() + ": " +e.getMessage());
					}
				}
				if (config.isExtractTitle()) {
					if (!empty) {
						sb.append(config.getDelimiter());
						empty = false;
					}
					try {
						String title = extractor.extractTitle();
						sb.append(title);
					}
					catch (IOException e) {
						System.err.println("Could not extract title for " + file.getAbsolutePath() + ": " +e.getMessage());
					}
				}
				
				out.println(sb.toString());
				
			}
    		System.out.println(config);
		}
		finally {
    		config.cleanUp();
		}
	}
	

}
