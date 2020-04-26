package fip_assignment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class assignment_3 {
    public static void main(String[] args) {
        String srcFileName = "src_yoda.raw"; // Example-1
        // String srcFileName = "src_bedroom.raw";  Example-2
        // String srcFileName = "src_imgpro.raw";  Example-3
        
//        System.out.println("Input File Name: " + srcFileName);
//        System.out.println("Output File Name: " + "a3_output_histogram.raw");
        
        File file = new File(srcFileName);
        try {
            FileInputStream fis = new FileInputStream(file);
            String fileName = file.getName();
            int fileSize = (int) file.length();
            
            //  int height = 600; // src_bedroom
            // int width = 600; // src_bedroom
            int height = 62; // src_yoda
            int width = 123; // src_yoda
            
            System.out.println("Image Height: " + height);
            System.out.println("Image Width: " + width);
            
            int[] originalImg = new int[width * height];
            int[] numberOfPixels = new int[256];
            
            System.out.println("File Name: " + fileName);
            System.out.println("File Size: " + fileSize);
            
            int value;
            int index = 0;
            
            while((value = fis.read()) != -1) {
                originalImg[index] = value;
                numberOfPixels[value]++;
                index++;
            }
            
            
            int[] runningSumNums = new int[256];
            int totalRunningSum = 0;
            
            for (int i = 0; i < numberOfPixels.length; i++) {
                int acc = 0;
                if (i > 0) {
                    acc = runningSumNums[i - 1];
                }
                totalRunningSum = runningSumNums[i] = acc + numberOfPixels[i];
//                System.out.println("Gray-Level: " + i + ", No. of Pixel: " + numberOfPixels[i] + ", Run Sum: " + runningSumNums[i]);
            }
            
            
            System.out.println("Total Running Sum: " + totalRunningSum);
            
//            System.out.println("---------------- Step 2: Normalise the values ----------------");
            
            double[] normalizedRunningSumNums = new double[256];
            
            for (int i = 0; i < runningSumNums.length; i++) {
                normalizedRunningSumNums[i] = (double) runningSumNums[i] / totalRunningSum;
//                System.out.println("Gray-Level: " + i + ", Running Sum Number: " + runningSumNums[i] + ", Normalized Number: " + normalizedRunningSumNums[i]);
            }
            
//            System.out.println("---------------- Step 3: Multiply the values ----------------");
            
            int[] multipliedRunningSumNums = new int[256];
            
            System.out.println("---------------- Histogram Equalized Values ----------------");
            System.out.println("Gray-Level   No of Pixel    Run Sum        Normalized             Multiply 255");
            System.out.println("------------------------------------------------------------");
            
            for (int i = 0; i < normalizedRunningSumNums.length; i++) {
                double multipliedNum = normalizedRunningSumNums[i] * 255;
                multipliedRunningSumNums[i] = (int)Math.round(multipliedNum);
                
                String space0 = "            ";
                String space1 = "               ";
                String space2 = "               ";
                String space3 = "                       ";
                
                if (i > 9 && i <= 99) {
                    space0 = "           ";
                } else if (i > 99) {
                    space0 = "          ";
                }
                
                for (int j = 0; j < Integer.toString(numberOfPixels[i]).length(); j++) {
                    space1 = space1.substring(0, space1.length() - 1);
                }
                
                for (int j = 0; j < Integer.toString(runningSumNums[i]).length(); j++) {
                    space2 = space2.substring(0, space2.length() - 1);
                }
                
                for (int j = 0; j < Double.toString(normalizedRunningSumNums[i]).length(); j++) {
                    space3 = space3.substring(0, space3.length() - 1);
                }
                
                System.out.println(i + space0 + numberOfPixels[i] + space1 + runningSumNums[i] + space2 + normalizedRunningSumNums[i] + space3 + multipliedRunningSumNums[i]);
            }
            
            File outputFile = new File("a3_output_histogram.raw");
            FileOutputStream fout = new FileOutputStream(outputFile);
            
            for (int i = 0; i < originalImg.length; i++) {
                fout.write(multipliedRunningSumNums[originalImg[i]]);
            }
            
            fout.flush();
            fout.close(); // close file output stream
            fis.close(); // close file input stream
//            System.out.println("---------------- Step 4: Map the gray-level to the result ----------------");
        } catch (Exception e) {
            // Catch something
        } // End catch
    } // End main
    
} // End class
