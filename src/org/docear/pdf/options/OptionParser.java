package org.docear.pdf.options;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class OptionParser {	
	private static Configuration config = new Configuration();

	public OptionParser(String[] args) {
		final Parser commandlineparser = new PosixParser();
		final Options options = createCommandLineOptions();
		CommandLine cl = null;
		try {
			cl = commandlineparser.parse(options, args, true);
		}
		catch (final ParseException exp) {
			System.err.println("Unexpected exception:" + exp.getMessage());
		}
		// Process command line and store parameter in attributes
		try {
			if (null != cl) {
				processCommandline(cl);
			}
		}
		catch (final IllegalArgumentException e) {
			outputCommandLineHelp(options);
			System.out.println("Illegal arguments on command line: " + e.getMessage());
			return;
		}
		if ((null != cl) && cl.hasOption("help") || args.length < 1) {
			outputCommandLineHelp(options);
			return;
		}
		
		testConfiguration();
	}

	public Configuration getConfiguration() {
		return config;
	}

	private Options createCommandLineOptions() {
		//execution time
		//full text
		final Options options = new Options();
		options.addOption("help", false, "print help and exit");
		options.addOption("delimiter", true, "use a specific delimiter String, using \"|\" by default");
		options.addOption("header", false, "include header into the data set");
		options.addOption("time", false, "include time needed extracting the data set");
		options.addOption("name", false, "include the file name");
		options.addOption("hash", false, "generate a unique hash for the PDF file that does not change even when creating annotations in the PDF");
		options.addOption("title", false, "extract the title of the PDF file");
		options.addOption("text", false, "extract plain text of the PDF file");
		options.addOption("out", true, "write to a file");
		options.addOption("outappend", false, "append to file, instead of overwriting it");
		return options;
	}

	private void outputCommandLineHelp(final Options options) {
		final HelpFormatter formater = new HelpFormatter();
		String usage = "java -jar DocearPdfInspector [OPTION]... [FILE]..." + Configuration.NL;
		usage += "Inspect and extract information of PDF file(s) and return them as a CSV file or on the command line. " +
				"Files may contain wildcards." + Configuration.NL + Configuration.NL;
		usage += "Selected fields are returned in the following order (if included): name, hash, title, text, time";
		formater.printHelp(usage, options);
	}

	private void processCommandline(final CommandLine cl) throws IllegalArgumentException {
		try {
			if ((null != cl) && cl.hasOption("delimiter")) {
				config.setDelimiter(cl.getOptionValue("delimiter"));
			}
			if ((null != cl) && cl.hasOption("time")) {
				config.setIncludeExecutionTime(true);
			}
			if ((null != cl) && cl.hasOption("hash")) {
				config.setExtractHash(true);
			}
			if ((null != cl) && cl.hasOption("header")) {
				config.setIncludeHeader(true);
			}
			if ((null != cl) && cl.hasOption("title")) {
				config.setExtractTitle(true);
			}
			if ((null != cl) && cl.hasOption("text")) {
				config.setExtractPlainText(true);
			}
			if ((null != cl) && cl.hasOption("name")) {
				config.setIncludeFilename(true);
			}			
			if ((null != cl) && cl.hasOption("out")) {
				if ((null != cl) && cl.hasOption("outappend")) {
					config.setOutAppend(true);
				}
				File file = new File(cl.getOptionValue("out"));
				config.setOutFile(file);
				config.setPrintStream(file);
			}			

			String[] remainder = cl.getArgs();
			for (String inFileString : remainder) {
				for (File inFile : wildcardResolution(inFileString)) {
					config.addInFile(inFile);
				}
			}			
		}
		catch (Exception e) {
			System.out.println("Illegal argument values: " + e.getMessage());
		}
	}
	
	protected File[] wildcardResolution(String inFileString) {
		try {
			File f = new File(inFileString).getAbsoluteFile();			
			File dir = f.getParentFile();
	    	FileFilter fileFilter = new WildcardFileFilter(f.getName());	    	
	    	return dir.listFiles(fileFilter);
		}
		catch(Exception e) {
			System.err.println("Could not recognize files in String: " + inFileString);
		}
		File[] ret = {};
		return ret;
	}
	
	private void testConfiguration() {
		int status = 0;
		if (!config.isExtractHash() && !config.isExtractTitle()) {
			System.err.println("WARNING: please select at least one field to extract from your PDF files");
			status = 1;
		}
		if (config.getInFiles().size() == 0) {
			System.err.println("WARNING: please select at least one file to process");
			status = 2;
		}
		
		if (status > 0) {
			System.exit(status);
		}
	}

}