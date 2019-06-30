package pdfutil;

import java.io.File;

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

public class Dialog {
    public static void main(String[] args) {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);

        FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Document", "pdf");
        chooser.setFileFilter(filter);

        int newPage = chooser.showDialog(null, "Add Page(s)");
        File[] pages = chooser.getSelectedFiles();

        if(newPage == JFileChooser.APPROVE_OPTION) {
            try {
                PDFMergerUtility PDFmerger = new PDFMergerUtility();
                PDFmerger.setDestinationFileName("output/mergedPDF.pdf");

                for(int i = 0; i < pages.length; i++) {
                    PDFmerger.addSource(pages[i]);
                }

                PDFmerger.mergeDocuments(null);
                JOptionPane.showMessageDialog(null, "Success! PDF saved to output folder.");
            }
            catch (Exception e){
                JOptionPane.showMessageDialog(null, "Error reading files: " + e.getMessage());
            }
        }	
    }
}