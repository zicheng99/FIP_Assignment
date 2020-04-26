package fip_assignment;

import java.io.*;

public class assignment_2 {

    public static void main(String[] args) {
        String strInputFile = "src_yoda.raw"; // Example-1
        // String strInputFile = "src_bedroom.raw"; // Example-2
        // String strInputFile = "src_imgpro.raw"; // Example-3
        
        // System.out.println("Input File Name: " + strInputFile);
        // System.out.println("Output File Name: " + "a2_output_d2.raw");
        
        File objFile = new File(strInputFile);
        
        try {
            FileInputStream fis = new FileInputStream(objFile);
            File outputFile = new File("a2_output_patterning.raw");
            FileOutputStream fout = new FileOutputStream(outputFile);
            String fileName = objFile.getName();
            int fileSize = (int) objFile.length();
            
            // int orginalHeight = 600; // src_bedroom.raw
            // int originalWidth = 600; // src_bedroom.raw
            // int orginalHeight = 100; // src_imgpro.raw
            // int originalWidth = 122; // src_imgpro.raw            
            int orginalHeight = 62; // src_yoda.raw
            int originalWidth = 123; // src_yoda.raw
//            System.out.println("Image Height: " + orginalHeight);
//            System.out.println("Image Width: " + originalWidth);
            
            System.out.println("---------------- Source File Info ----------------");
            System.out.println("File Name: " + fileName);
            System.out.println("File Size: " + fileSize);
            System.out.println("---------------- Patterning Data ----------------");
            
            int index = 0;         
            int totalPatternSeq = (orginalHeight * 3) * (originalWidth * 3);
            int value;
            int[][] data = new int[orginalHeight * 3][originalWidth * 3];
            int rowCount = 0;
            int colCount = 0;
            while ((value = fis.read()) != -1) {
                int[] patternDataInByte = getPattern(value);
                int patternWriteIndex = 0;

                int rowIndex1 = rowCount * 3;
                int rowIndex2 = rowIndex1 + 1;
                int rowIndex3 = rowIndex2 + 1;
                int colIndex1 = colCount * 3;
                int colIndex2 = colIndex1 + 1;
                int colIndex3 = colIndex2 + 1;

                writeData(data, rowIndex1, colIndex1, patternDataInByte[0]);
                writeData(data, rowIndex1, colIndex2, patternDataInByte[1]);
                writeData(data, rowIndex1, colIndex3, patternDataInByte[2]);
                writeData(data, rowIndex2, colIndex1, patternDataInByte[3]);
                writeData(data, rowIndex2, colIndex2, patternDataInByte[4]);
                writeData(data, rowIndex2, colIndex3, patternDataInByte[5]);
                writeData(data, rowIndex3, colIndex1, patternDataInByte[6]);
                writeData(data, rowIndex3, colIndex2, patternDataInByte[7]);
                writeData(data, rowIndex3, colIndex3, patternDataInByte[8]);

                if (colCount == (originalWidth - 1) && rowCount < orginalHeight) {
                    colCount = 0;
                    rowCount++;
                } else if (colCount < originalWidth && rowCount < orginalHeight) {
                    colCount++;
                } else {
                    throw new Error("condition error");
                }

                index++;
            }

            if (index != fileSize || (index != (totalPatternSeq / 9))) {
                System.out.println("last index " + index);
                System.out.println("totalPatternSeq " + totalPatternSeq);
                System.out.println("colCount " + colCount);
                System.out.println("originalWidth " + originalWidth);
                System.out.println("rowCount " + rowCount);
                System.out.println("orginalHeight " + orginalHeight);
                throw new Error("Something went wrong...");
            }

            int writeCount = 0;

            for (int[] data1 : data) {
                for (int j = 0; j < data1.length; j++) {
                    fout.write(data1[j]);
                    writeCount++;
                }
            }

            fout.flush();
            fout.close();
            fis.close();

            System.out.println("---------------- Dithering D1 Data ----------------");
            FileInputStream fis_d1 = new FileInputStream(objFile);
            File outputFile_d1 = new File("a2_output_d1.raw");
            FileOutputStream fout_d1 = new FileOutputStream(outputFile_d1);
            int[] d1Model = {0, 128, 192, 64};
            int[][] d1Data = new int[orginalHeight][originalWidth];
            index = 0;
            colCount = 0;
            rowCount = 0;
            while ((value = fis_d1.read()) != -1) {

                if ((rowCount == 0 || rowCount % 2 == 0) && (colCount == 0 || colCount % 2 == 0)) {
                    // check 0
                    d1Data[rowCount][colCount] = value > d1Model[0] ? 255 : 0;
                } else if ((rowCount == 0 || rowCount % 2 == 0) && (colCount + 1) % 2 == 0) {
                    // check 128
                    d1Data[rowCount][colCount] = value > d1Model[1] ? 255 : 0;
                } else if (rowCount > 0 && (rowCount + 1) % 2 == 0 && (colCount == 0 || colCount % 2 == 0)) {
                    // check 192
                    d1Data[rowCount][colCount] = value > d1Model[2] ? 255 : 0;
                } else if (rowCount > 0 && (rowCount + 1) % 2 == 0) {
                    // check 64
                    d1Data[rowCount][colCount] = value > d1Model[3] ? 255 : 0;
                }

                if (colCount == (originalWidth - 1) && rowCount < orginalHeight) {
                    colCount = 0;
                    rowCount++;
                } else if (colCount < originalWidth && rowCount < orginalHeight) {
                    colCount++;
                } else {
                    throw new Error("condition error");
                }
                
                index++;
            } // End while

            for (int[] dataSet : d1Data) {
                for (int i = 0; i < dataSet.length; i++) {
                    fout_d1.write(dataSet[i]);
                }
            }

            fout_d1.flush();
            fout_d1.close();
            fis_d1.close();

            System.out.println("---------------- Dithering D1 Data ----------------");
            FileInputStream fis_d2 = new FileInputStream(objFile);
            File outputFile_d2 = new File("a2_output_d2.raw");
            FileOutputStream fout_d2 = new FileOutputStream(outputFile_d2);
            int[][] d2Model = {
                {   0, 128,  32, 160 },
                { 192,  64, 224,  96 },
                {  48, 176,  16, 144 },
                { 240, 112, 208,  80 },
            };
            int[][] d2Data = new int[orginalHeight][originalWidth];
            index = 0;
            colCount = 0;
            rowCount = 0;
            while ((value = fis_d2.read()) != -1) {
                
                int modelRowIndex;
                int modelColIndex;
                
                if ((rowCount + 1) % 4 == 0) {
                    modelRowIndex = 3;
                } else if ((rowCount + 1) % 3 == 0) {
                    modelRowIndex = 2;
                } else if ((rowCount + 1) % 2 == 0) {
                    modelRowIndex = 1;
                } else {
                    modelRowIndex = 0;
                }
                
                if ((colCount + 1) % 4 == 0) {
                    modelColIndex = 3;
                } else if ((colCount + 1) % 3 == 0) {
                    modelColIndex = 2;
                } else if ((colCount + 1) % 2 == 0) {
                    modelColIndex = 1;
                } else {
                    modelColIndex = 0;
                }
                
                d2Data[rowCount][colCount] = value > d2Model[modelRowIndex][modelColIndex] ? 255 : 0;
                  
                if (colCount == (originalWidth - 1) && rowCount < orginalHeight) {
                    colCount = 0;
                    rowCount++;
                } else if (colCount < originalWidth && rowCount < orginalHeight) {
                    colCount++;
                } else {
                    throw new Error("condition error");
                }

                index++;
            }

            for (int[] dataSet : d2Data) {
                for (int i = 0; i < dataSet.length; i++) {
                    fout_d2.write(dataSet[i]);
                } // End inner for
            } // End outer for

            fout_d2.flush();
            fout_d2.close();
            fis_d2.close();

        } catch (IOException ex) {
            System.out.println("File does not exist");
        } // End catch
    } // End main
    
    // getPattern()
    private static int[] getPattern(int colorDec) {
        int[] result = new int[9];
        double baseNum = 25.5;

        for (int i = 0; i < 10; i++) { // 10 types of pattern
            double start = i * baseNum;
            double end = start + baseNum;
            if (start <= colorDec && colorDec <= end) {
                result = getPatternArr(i);
                break;
            } // End if
        } // End for

        return result;
    } // End getPattern()
    
    // getPatternArr()
    private static int[] getPatternArr(int pattern) {
        int[] arr = new int[]{};

        switch (pattern) {
            case 0:
                arr = new int[]{
                    0, 0, 0,
                    0, 0, 0,
                    0, 0, 0};
                break;
            case 1:
                arr = new int[]{
                    0, 0, 0,
                    0, 0, 0,
                    0, 0, 255};
                break;
            case 2:
                arr = new int[]{
                    255, 0, 0,
                    0, 0, 0,
                    0, 0, 255};
                break;
            case 3:
                arr = new int[]{
                    255, 0, 255,
                    0, 0, 0,
                    0, 0, 255};
                break;
            case 4:
                arr = new int[]{
                    255, 0, 255,
                    0, 0, 0,
                    255, 0, 255};
                break;
            case 5:
                arr = new int[]{
                    255, 0, 255,
                    0, 0, 0,
                    255, 255, 255};
                break;
            case 6:
                arr = new int[]{
                    255, 0, 255,
                    255, 0, 0,
                    255, 255, 255};
                break;
            case 7:
                arr = new int[]{
                    255, 255, 255,
                    255, 0, 0,
                    255, 255, 255};
                break;
            case 8:
                arr = new int[]{
                    255, 255, 255,
                    255, 0, 255,
                    255, 255, 255};
                break;
            case 9:
                arr = new int[]{
                    255, 255, 255,
                    255, 255, 255,
                    255, 255, 255};
                break;
        }

        return arr;
    } // End getPatternArr()
    
    // isBetween()
    private static boolean isBetween(int x, int start, int end) {
        return start <= x && x <= end;
    } // End isBetween()
    
    // writeData()
    private static void writeData(int[][] data, int x, int y, int value) {
        if (data[x][y] == 0) {
            data[x][y] = value;
        } else {
            throw new Error("Error in writeData()");
        }
    } // End writeData()
    
} // End class
