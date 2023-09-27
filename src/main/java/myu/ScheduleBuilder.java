package myu;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.Scanner;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/*
 * This class parses the PDF documents that contain a UW academic schedule 
 * (summer quarter is tenatively unsupported) and outputs the information 
 * necessary for creating the visual schedule (class name, times, days, etc)
 * to a string for the Schedule class to handle
 */
public class ScheduleBuilder {
    /*
     * @param: PDF file to read a schedule off of
     * @return: List of string format of each Course from parameter
     * @exception: IO Exception if issue with reading parameter file
     */
    protected static List<String> parse(File file) throws IOException {
        /*String currentDirectory = System.getProperty("user.dir");
        File currentDir = new File(currentDirectory);
        String absolutePath = currentDir.getAbsolutePath();
        System.out.println("Current Absolute Filepath: " + absolutePath); */

        PDDocument pd = PDDocument.load(file);
        PDFTextStripper reader = new PDFTextStripper();
        reader.setSortByPosition(true);
        String text = reader.getText(pd);
    
        int start = text.indexOf("Information", 0) + "Information".length();
        int end = text.indexOf("Display Textbooks");
        text = text.substring(start, end);
        text = text.replace("Researching?", "");
        text = text.replace("Start here", "break");
        List<String> allCourses = new ArrayList<>();
        Scanner input = new Scanner(text);
        
        while (input.hasNextLine()) 
        {
            String current = input.nextLine();
            if (current.indexOf("Total credits:") != -1) {
                double totalCredits = Double.parseDouble(current.substring
                                            ("Total credits: ".length()).trim());
                System.out.println("total credits: " + totalCredits);
                allCourses.add(String.valueOf(totalCredits));
                break;
            } else if (current.equals("break") || current.length() == 0) {
                //just skip
            } else {
                String line = current;
                //some classes have a line break in their name
                    //this length of <= 50 tries to guess for that 
                if (current.length() <= 50) {
                    line += " " + input.nextLine();
                }
                line = line.trim();
                if (checkDash(line)) {
                    int i = line.lastIndexOf("-");
                    line = line.substring(0, i+1) + 
                            line.substring(i+2);
                }
                allCourses.add(line);
            }
        }
        input.close();
        pd.close();
        return allCourses;
    }

    /*
     * @param: a String of words (whitespace delimiter), 
     *          is String representation of a course
     * @return: a String of the parameter with word order reversed 
     */
    protected static String reverse(String str) {
        String reverse = "";
        while (str.indexOf(" ") != -1) {
            reverse += str.substring(str.lastIndexOf(" "));
            str = str.substring(0, str.lastIndexOf(" "));
        }
        reverse += " " + str;
        return reverse.trim();
    }

    /*
     * check if have a dash problem in the class times
     *      return true if have something like 130- 220  
     * @param: a String of classtimes with a dash delimiter
     * @return: true if there is a space following the dash 
     *              in the parameter, false otherwise
     */
    private static boolean checkDash(String str) {
        int i = str.lastIndexOf("-"); 
        return (i != -1 && str.substring(i-1, i).matches("\\d+")
                && str.substring(i+1, i+2).equals(" "));
    }
}