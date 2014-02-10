# PDF Inspector

[Docear’s PDF Inspector](https://www.docear.org/software/add-ons/docears-pdf-inspector/) is a JAVA library that extracts titles from a PDF file not from the PDF’s metadata but from its full-text. More precisely, Docear’s PDF Inspector extracts the full-text of the first page of a PDF and looks for the largest text in the upper third of that page. This text is returned as title. Of course, this does not always deliver the correct title (e.g. sometimes the journal name is formatted in a larger font size than an article’s title) but in about 70% you will get the correct one.  

The main features of Docear’s PDF Inspector are

* Extracts titles from PDF files with good accuracy (~70%) and excellent run-time (few milliseconds per PDF in batch mode)
* Usable as JAVA library (other tools such as reference managers can easily integrate Docear’s PDF Extractor to extract titles from PDFs.
* Usable as stand-alone command-line application (returns a PDFs’ title on the command line)
* Usable in batch mode (stores the extracted titles into a CSV file)
* Reads all PDF versions (other tools such as SciPlore Xtract or ParsCit are using PDFBox for processing the PDFs. However, PDFBox sometimes has problems extracting text from PDFs not being 100% compliant to the PDF standard – Docear’s PDF Inspector is based on jPod, which is more tolerant)
* Written entirely in JAVA 1.6. Hence, Docear’s PDF Inspector runs on any major operating system, including Windows, Linux, and Mac OS, without any other tools required (besides the JAVA run time environment, of course)
* Completely independent of further tools – you only need Docear’s PDF Inspector, that’s it (e.g. SciPlore Xtract requires pdftohtml to be installed)
* Released under the [GNU General Public License (GPL) 2 or later](http://www.gnu.org/licenses/gpl-2.0.html), which means it is completely free to use and its source code can be downloaded and modified by anyone.

Please, visit [https://www.docear.org/software/add-ons/docears-pdf-inspector/](https://www.docear.org/software/add-ons/docears-pdf-inspector/)
for further details.

