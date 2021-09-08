# PDFmerger
Simple tool written in Java for merging multiple PDF files together. Supports multiple selection. Uses PDFBox and Swing.

* /dist - runnable JAR and build files
* /output - default location for merged PDFs
* /pdfbox - Adobe PDFBox API
* /src - source code folder

### 9/7/21 quality-of-life update: ###
- the "add pages" dialog now asks if you'd like to continue adding PDFs
- files are now checked for a ".pdf" extension
- you can now customize where the combined PDF will be saved
- individual PDFs are now checked for errors
- all error and info messages are much more descriptive now

- accidentally choosing all non-PDF files triggers the "add page(s)" dialog again
- much improved indentation
