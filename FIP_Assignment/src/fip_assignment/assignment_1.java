package fip_assignment;

import java.io.*;

public class assignment_1 {

    public static String byteOrder = null;

    public static void main(String[] args) {
        try {
            String strFileName = "a1_src_yoda.tif"; // Example-1
            // String strFileName = "a1_src_imgpro.tif"; Example-2
            // String strFileName = "a1_src_lena.tif"; Example-3
            
            File objFile = new File(strFileName); // Create and assign a new file object
            FileInputStream objFileInputStream = new FileInputStream(objFile);
            
            System.out.println("Input File Name: " + objFile.getName());
            
            int value;
            int index = 0;
            int offsetNextIFD = -1;
            int stripOffsets = -1;
            int dataColumnCount = 0;
            String[][] dataEntryArr = null;
            int dataEntryIndex1 = 0;
            int totalDE = -1;
            byte[] rawBytes = null;
            int rawBytesCount = 0;
            File objOutputFile = new File("assignment-1.txt");
            FileOutputStream objFileOutputStream = new FileOutputStream(objOutputFile);
            
            while ((value = objFileInputStream.read()) != -1) {
                
                if (index == 0) { // Check LSB/MSB
                    int value2 = objFileInputStream.read();
                    String hex = getHexString(value) + getHexString(value2);
                    if (hex.equals("4949")) {
                        byteOrder = "LSB";
                    } else if (hex.equals("4D4D")) {
                        byteOrder = "MSB";
                    }
                    System.out.println("--------------------- Header File Data -------------------------");
                    System.out.println("Byte Order: " + byteOrder);
                    index++;
                } else if (index == 2) {
                    int value2 = objFileInputStream.read();
                    int[] nums = {value, value2};

                    String hexString = sumDecsToHexString(nums);
                    System.out.println("Version: " + hexString);

                    index++;
                } else if (index == 4) {
                    int value2 = objFileInputStream.read();
                    int value3 = objFileInputStream.read();
                    int value4 = objFileInputStream.read();
                    int[] nums = {value, value2, value3, value4};

                    String hexString = sumDecsToHexString(nums);
                    System.out.println("First Offset IFD: " + Integer.parseInt(hexString, 16));

                    index += 3;
                } else if (index == 8) {
                    int value2 = objFileInputStream.read();
                    int[] nums = {value, value2};

                    String hexString = sumDecsToHexString(nums);
                    totalDE = Integer.parseInt(hexString, 16);

                    System.out.println("--------------------- IFD Data -------------------------");
                    System.out.println("Total DE: " + totalDE);
                    dataEntryArr = new String[totalDE][8];
                    index++;
                } else if ((index >= 10 && index < (10 + 12 * totalDE)) && ((stripOffsets > 0 && index <= stripOffsets && ((index + 12) <= stripOffsets)) || (stripOffsets == -1))) {
                    int tag1 = value;
                    int tag2 = objFileInputStream.read();

                    int type1 = objFileInputStream.read();
                    int type2 = objFileInputStream.read();

                    int length1 = objFileInputStream.read();
                    int length2 = objFileInputStream.read();
                    int length3 = objFileInputStream.read();
                    int length4 = objFileInputStream.read();

                    int value1 = objFileInputStream.read();
                    int value2 = objFileInputStream.read();
                    int value3 = objFileInputStream.read();
                    int value4 = objFileInputStream.read();

                    int[] tagNameNums = { tag1, tag2 };
                    int[] tagTypeNums = { type1, type2 };
                    
                    String tagName = getTagName(tagNameNums);
                    String strTagTypeName = getTagTypeName(tagTypeNums);

                    if (tagName != null && strTagTypeName != null) {
                        String tagValue = sumDecsToHexString(new int[] {value1, value2, value3, value4});
                        String tagLength = sumDecsToHexString(new int[] {length1, length2, length3, length4});

                        int tagValueDec = Integer.parseInt(tagValue, 16);
                        int tagLengthDec = Integer.parseInt(tagLength, 16);
                        int wordLength1 = 29 - tagName.length();
                        int wordLength2 = 9 - strTagTypeName.length();

                        String wordSpaces1 = "";
                        String wordSpaces2 = "";

                        for (int i = 0; i < wordLength1; i++) {
                            wordSpaces1 += " ";
                        }

                        for (int i = 0; i < wordLength2; i++) {
                            wordSpaces2 += " ";
                        }
                        
                        if (tagName == "StripOffsets") {
                            stripOffsets = tagValueDec;
                            System.out.println("Size of IFD: " + stripOffsets);
                        }

                        dataEntryArr[dataEntryIndex1][0] = Integer.toString(Integer.parseInt(sumDecsToHexString(tagNameNums), 16));
                        dataEntryArr[dataEntryIndex1][1] = tagName;
                        dataEntryArr[dataEntryIndex1][2] = wordSpaces1;
                        dataEntryArr[dataEntryIndex1][3] = Integer.toString(Integer.parseInt(sumDecsToHexString(tagTypeNums), 16));
                        dataEntryArr[dataEntryIndex1][4] = strTagTypeName;
                        dataEntryArr[dataEntryIndex1][5] = wordSpaces2;
                        dataEntryArr[dataEntryIndex1][6] = Integer.toString(tagLengthDec);
                        dataEntryArr[dataEntryIndex1][7] = Integer.toString(tagValueDec);
                        dataEntryIndex1++;
                    }

                    index += 11;
                } else if (index == (10 + 12 * totalDE)) {
                    int value2 = objFileInputStream.read();
                    int[] nums = {value, value2};
                    String offsetNextIFDHex = sumDecsToHexString(nums);
                    offsetNextIFD = Integer.parseInt(offsetNextIFDHex, 16);
                    System.out.println("Consecutive Offset IFD (Offset of Next IFD): " + offsetNextIFD);
                    index++;
                } else if (index >= 10 && stripOffsets > 0 && index >= stripOffsets) {
                    if (index == (stripOffsets)) {

                        int totalRawBytesLength = (int) (objFile.length() - stripOffsets);
                        rawBytes = new byte[totalRawBytesLength];

                        System.out.println("--------------------- DE Data -------------------------");
                        System.out.println("Tag                                Type          Length     Value");
                        System.out.println("----------------------------------------------------------");

                        for (int i = 0; i < (dataEntryArr.length - 1); i++) {
                            if (dataEntryArr[i][0] != null) {
                                int tagDec = Integer.parseInt(dataEntryArr[i][0]);
                                String tagName = dataEntryArr[i][1];
                                String wordSpaces1 = dataEntryArr[i][2];
                                int intTagType_dec = Integer.parseInt(dataEntryArr[i][3]);
                                String strTagTypeName = dataEntryArr[i][4];
                                String wordSpaces2 = dataEntryArr[i][5];
                                int tagLengthDec = Integer.parseInt(dataEntryArr[i][6]);
                                int tagValueDec = Integer.parseInt(dataEntryArr[i][7]);
                                System.out.println(tagDec + " (" + tagName + ")" + wordSpaces1 + intTagType_dec + " (" + strTagTypeName + ")" + wordSpaces2 + tagLengthDec + "         " + tagValueDec);
                            }
                        }

                        System.out.println("--------------------- Data Table -------------------------");
                    }

                    String valueHex = String.format("%02X", value);
                    System.out.print(valueHex);
                    objFileOutputStream.write(value);

                    if (dataColumnCount == 15) {
                        System.out.println("");
                        dataColumnCount = 0;
                    } else {
                        if (dataColumnCount % 2 != 0) {
                            System.out.print(" ");
                        }
                        dataColumnCount++;
                    }
                }
                index++;
            } // End while (Finish reading the input file)
            objFileOutputStream.close(); // Close output file
            objFileInputStream.close(); // Close input stream
        } // End try 
        catch (IOException e) {
            System.out.println("File does not exist");
        } // End catch
    } // End main
    
    // sumDecsToHexString(int[])
    // 1. Flip the order if it is LSB
    // 2. Convert decimal into hexadecimal
    private static String sumDecsToHexString(int[] decNums) {
        String hexString = "";
        
        for (int i = 0; i < decNums.length; i++) {
            if (byteOrder == "LSB") {
                hexString += getHexString(decNums[decNums.length - (i + 1)]);
            } else if (byteOrder == "MSB") {
                hexString += getHexString(decNums[i]);
            }
        }
        
        return hexString;
    } // End sumDecsToHexString(int[])
    
    // getHexString(int)
    private static String getHexString(int decNum) {
        if (decNum < 10) {
            return "0" + decNum;
        } else {
            return String.format("%02X", decNum); // Convert decimal into hexadecimal
        }
    } // End getHexString(int)
    
    // Step 1: getTagTypeName(int[])
    private static String getTagTypeName(int[] tagTypeNums) {
        String tagTypeHex = sumDecsToHexString(tagTypeNums); // Convert decimal into hexadecimal (Flip the order if it is LSB)
        return getTagTypeName(tagTypeHex);
    } // End getTagTypeName(int[])
    
    // Step 2: getTagTypeName(int)
    private static String getTagTypeName(int intTagType_dec) {
        String strTagTypeName = null;
        
        switch (intTagType_dec) {
            case 1:
                strTagTypeName = "byte";
                break;
            case 2:
                strTagTypeName = "ASCII";
                break;
            case 3:
                strTagTypeName = "short";
                break;
            case 4:
                strTagTypeName = "long";
                break;
            case 5:
                strTagTypeName = "rational";
                break;
        }

        return strTagTypeName;
    }
    // End getTagTypeName()

    // Step 3: getTagTypeName(String) 
    private static String getTagTypeName(String tagTypeHex) {
        int tagType = Integer.parseInt(tagTypeHex, 16); // Convert hexadecimal into decimal
        return getTagTypeName(tagType); // e.g. byte, ASCII, short, long, rational
    }

    // Step 1: getTagName(int[])
    private static String getTagName(int[] tagNums) {
        String strTagHex = sumDecsToHexString(tagNums); // Convert decimal into hexadecimal (Flip the order if it is LSB)
        return getTagName(strTagHex);
    } // End getTagName(int[])
    
    // Step 2: getTagName(int) e.g. NewSubfileType, ImageWidth, ImageLength
    private static String getTagName(int tagType) {
        String tagName = null;

        switch (tagType) {
            case 254:
                tagName = "NewSubfileType";
                break;
            case 256:
                tagName = "ImageWidth";
                break;
            case 257:
                tagName = "ImageLength";
                break;
            case 258:
                tagName = "BitsPerSample";
                break;
            case 259:
                tagName = "Compression";
                break;
            case 262:
                tagName = "PhotometricInterpretation";
                break;
            case 273:
                tagName = "StripOffsets";
                break;
            case 277:
                tagName = "SamplesPerPixel";
                break;
            case 278:
                tagName = "RowsPerStrip";
                break;
            case 279:
                tagName = "StripByteCounts";
                break;
            case 282:
                tagName = "XResolution";
                break;
            case 283:
                tagName = "YResolution";
                break;
            case 296:
                tagName = "ResolutionUnit";
                break;
        } // End switch

        return tagName;
    } // End getTagName(int)
    
    // Step 3: getTagName(String)
    private static String getTagName(String strTagHex) {
        int tagType = Integer.parseInt(strTagHex, 16); // Convert hexadecimal into decimal
        return getTagName(tagType); // e.g. NewSubfileType, ImageWidth, ImageLength
    } // End getTagName()
    
} // End class

