package pdfutil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.multipdf.PDFMergerUtility;

/*
 * Copyright 2009–2019 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class ParsedFiles {
    private ArrayList<File> pages;
    private int invalidFileCount;
    
    public ParsedFiles(ArrayList<File> pages, int invalidFileCount) {
        this.pages = pages;
        this.invalidFileCount = invalidFileCount;
    }
    
    public ArrayList<File> getPages() {
        return pages;
    }
    public int getInvalidFileCount() {
        return invalidFileCount;
    }
}

public class PDFmerger {
    private static final String DEFAULT_FILENAME = "output/Merged PDFs.pdf";
    
    private static final String TYPE = ".pdf";
    private static final int TYPE_LEN = 4;
    
    private static ParsedFiles parseSelectedFiles(File[] selectedFiles, ArrayList<File> pages) {
        int wrongTypes = 0;
        
        for (File page : selectedFiles) {
            String extension = "";
            String fileName = page.getName();

            int i = fileName.lastIndexOf('.');
            if (i > 0) {
                extension = fileName.substring(i + 1);
            }

            if ((extension.toLowerCase()).equals("pdf")) {
                pages.add(page);
            }
            else {
                String errorMsg = page.getAbsolutePath() + " doesn't appear to be a PDF!";
                JOptionPane.showMessageDialog(null,
                                              errorMsg,
                                              "Wrong file extension",
                                              JOptionPane.WARNING_MESSAGE);
                wrongTypes++;
            }
        }
        
        return new ParsedFiles(pages, wrongTypes);
    }
    
    private static String getDestinationPath(File destination) {
        String path = destination.getAbsolutePath();
        String suffix = path.substring(path.length() - TYPE_LEN).toLowerCase();
        
        if(!suffix.contentEquals(TYPE)) {
            destination = new File(path + TYPE);
            path = destination.getAbsolutePath();
        }
        
        return path;
    }
    
    private static int openSelectedPages(PDFMergerUtility PDFmerger, ArrayList<File> pages) {
        int errors = 0;
        for (File page : pages) {
            try {
                PDFmerger.addSource(page);
            }
            catch (FileNotFoundException e) {
                String errorMsg = "Couldn't open "
                                + page.getAbsolutePath()
                                + "! Check if the file was moved or deleted.";
                
                JOptionPane.showMessageDialog(null,
                                              errorMsg,
                                              "Failed to open PDF",
                                              JOptionPane.WARNING_MESSAGE);
                errors++;
            }
        }
        return errors;
    }
    
    public static void main(String[] args) {
        boolean anotherFile;

        do {
            anotherFile = false;
            
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(true);

            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Document", "pdf");
            chooser.setFileFilter(filter);

            boolean morePages = true;
            ArrayList<File> pages = new ArrayList<File>();

            do {
                int newPage = chooser.showDialog(null, "Add Page(s)");

                File[] selectedFiles = chooser.getSelectedFiles();
                int selectedFileCount = chooser.getSelectedFiles().length;

                ParsedFiles parsedFiles = parseSelectedFiles(selectedFiles, pages);
                
                pages = parsedFiles.getPages();
                int wrongTypes = parsedFiles.getInvalidFileCount();
                
                if (wrongTypes > 0 && wrongTypes == selectedFileCount) {
                    newPage = JFileChooser.ERROR_OPTION;
                }

                if (newPage == JFileChooser.APPROVE_OPTION) {
                    int notDone = JOptionPane.showConfirmDialog(null,
                                                                "Would you like to add more pages?",
                                                                "More Pages",
                                                                JOptionPane.YES_NO_OPTION);

                    if (notDone == JOptionPane.NO_OPTION) {
                        morePages = false;
                    }
                }
                else if (newPage != JFileChooser.ERROR_OPTION) {
                    morePages = false;
                }
            } while (morePages);

            if (pages.size() > 0) {
                chooser.setSelectedFile(new File(DEFAULT_FILENAME));
                int savePages = chooser.showSaveDialog(null);

                PDFMergerUtility PDFmerger = new PDFMergerUtility();

                if (savePages == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    String path = getDestinationPath(file);
                    PDFmerger.setDestinationFileName(path);
                    
                    int errors = openSelectedPages(PDFmerger, pages);

                    if(errors == pages.size()) {
                        JOptionPane.showMessageDialog(null,
                                                      "Couldn't open any of your PDFs! "
                                                    + "Make sure the files' folder wasn't moved "
                                                    + "or deleted.",
                                                      "Failed to open all PDFs",
                                                      JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        try {
                            PDFmerger.mergeDocuments(null);
                            String successMsg = "Success! PDF saved to "
                                              + PDFmerger.getDestinationFileName();
                            
                            JOptionPane.showMessageDialog(null,
                                                          successMsg,
                                                          "PDF successfully combined",
                                                          JOptionPane.INFORMATION_MESSAGE);
                        }
                        catch (IOException e) {
                            JOptionPane.showMessageDialog(null,
                                                          "Couldn't save your combined PDF file! "
                                                        + "Make sure you have permission and enough "
                                                        + "empty space to save files on your computer.",
                                                          "Failed to save PDF",
                                                          JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
            
            int createAnother = JOptionPane.showConfirmDialog(null,
                                                              "Would you like to create another PDF?",
                                                              "Merge again",
                                                              JOptionPane.YES_NO_OPTION);
            if (createAnother == JOptionPane.YES_OPTION) {
                anotherFile = true;
            }
        } while (anotherFile);
    }
}
