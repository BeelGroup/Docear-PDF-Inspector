package org.docear.pdf.options;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Configuration {
	public static String NL = System.getProperty("line.separator");
	
	private String delimiter = "|";	
	private boolean includeExecutionTime = false;
	private boolean extractHash = false;
	private boolean extractTitle = false;
	private boolean includeFilename = false;	
	private boolean outAppend = false;
	private File outFile;
	
	
	private PrintStream printStream = System.out;
	
	private List<File> inFiles = new ArrayList<File>();
	
	public void cleanUp() {
		if (printStream != null) {
			try {
				printStream.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (delimiter != null) {
			sb.append(" delimiter: ").append(delimiter).append(NL);
		}
		if (extractHash) {
			sb.append(" extract Hash from pdf file(s)").append(NL);
		}
		if (extractTitle) {
			sb.append(" extract Titles from pdf file(s)").append(NL);
		}
		if (includeFilename) {
			sb.append(" include the file name of the processed pdf file").append(NL);
		}
		if (outAppend) {
			sb.append(" do not overwrite but append to output file").append(NL);
		}
		if (outFile != null) {
			sb.append(" write output to file: ").append(outFile).append(NL);
		}
		
		if (inFiles.size() > 0) {
			sb.append(" pdf files to inspect");
			for (File f : inFiles) {
				sb.append(" ").append(f);
			}
		}
		
		
		return sb.toString();
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public boolean isIncludeExecutionTime() {
		return includeExecutionTime;
	}

	public void setIncludeExecutionTime(boolean executionTime) {
		this.includeExecutionTime = executionTime;
	}

	public boolean isExtractHash() {
		return extractHash;
	}

	public void setExtractHash(boolean extractHash) {
		this.extractHash = extractHash;
	}

	public boolean isExtractTitle() {
		return extractTitle;
	}

	public void setExtractTitle(boolean extractTitle) {
		this.extractTitle = extractTitle;
	}

	public boolean isIncludeFilename() {
		return includeFilename;
	}

	public void setIncludeFilename(boolean includeFilename) {
		this.includeFilename = includeFilename;
	}

	public boolean isOutAppend() {
		return outAppend;
	}

	public void setOutAppend(boolean outAppend) {
		this.outAppend = outAppend;
	}

	public File getOutFile() {
		return outFile;
	}

	public void setOutFile(File outFile) {
		this.outFile = outFile;		
	}
	
	public void setPrintStream(File file) {
		if (file == null) {
			printStream = System.out;			
		}
		else {
			try {
				printStream = new PrintStream(new FileOutputStream(file, outAppend));				
			}
			catch (FileNotFoundException e) {
				System.err.println("Could not open file "+outFile+" for writing.");
				System.exit(3);
			}
		}		
	}
	
	public PrintStream getPrintStream() {
		return this.printStream;
	}
	
	public List<File> getInFiles() {
		return inFiles;
	}
	
	public void addInFile(File inFile) {
		inFiles.add(inFile);
	}
	
}
