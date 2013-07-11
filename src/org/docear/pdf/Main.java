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
		Long startTime = System.currentTimeMillis();
		final Configuration config = new OptionParser(args).getConfiguration();
		try {
			PrintStream out = config.getPrintStream();

			if (config.isIncludeHeader()) {				
				printHeader(config, out);
			}

			for (File file : config.getInFiles()) {
				Long time = System.currentTimeMillis();

				boolean empty = true;
				StringBuilder sb = new StringBuilder();

				PdfDataExtractor extractor = new PdfDataExtractor(file);
				try {
					if (config.isIncludeFilename()) {
						sb.append(file.getAbsolutePath());
						empty = false;
					}
					if (config.isExtractHash()) {
						if (!empty) {					
							sb.append(config.getDelimiter());						
						}
						try {
							empty = false;
							String hash = extractor.getUniqueHashCode();
							if (hash != null) {
								sb.append(hash);
							}
						}
						catch (IOException e) {
							System.err.println("Could not extract hash for " + file.getAbsolutePath() + ": " + e.getMessage());
						}
					}
					if (config.isExtractTitle()) {
						if (!empty) {
							sb.append(config.getDelimiter());
						}
						try {
							empty = false;
							String title = extractor.extractTitle();
							if (title != null) {
								sb.append(title);
							}
						}
						catch (IOException e) {
							System.err.println("Could not extract title for " + file.getAbsolutePath() + ": " + e.getMessage());
						}
					}
					if (config.isExtractPlainText()) {
						if (!empty) {
							sb.append(config.getDelimiter());
						}
						try {
							empty = false;
							String text = extractor.extractPlainText();
							if (text != null) {
								sb.append(text);
							}
						}
						catch (IOException e) {
							System.err.println("Could not extract text for " + file.getAbsolutePath() + ": " + e.getMessage());
						}
					}
					if (config.isIncludeDuration()) {
						if (!empty) {
							sb.append(config.getDelimiter());
						}
						empty = false;
						sb.append(System.currentTimeMillis() - time).append("ms");
					}
					out.println(sb.toString());
				}
				finally {
					extractor.close();
					extractor = null;
				}

			}
		}
		finally {
			config.cleanUp();
			System.out.println("total execution time: " + (System.currentTimeMillis() - startTime));
		}
	}

	private static void printHeader(Configuration config, PrintStream out) {
		StringBuilder sb = new StringBuilder();
		boolean empty = true;
	
		if (config.isIncludeFilename()) {			
			sb.append("file name");
			empty = false;
		}
		if (config.isExtractHash()) {
			if (!empty) {
				sb.append(config.getDelimiter());
				empty = false;
			}
			sb.append("hash");			
		}
		if (config.isExtractTitle()) {
			if (!empty) {
				sb.append(config.getDelimiter());
				empty = false;
			}
			sb.append("title");
		}
		if (config.isExtractPlainText()) {
			if (!empty) {
				sb.append(config.getDelimiter());
				empty = false;
			}
			sb.append("text");
			
		}
		if (config.isIncludeDuration()) {
			if (!empty) {
				sb.append(config.getDelimiter());
				empty = false;
			}
			sb.append("execution time");
		}
		
		out.println(sb.toString());
	}

}
