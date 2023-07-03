package myu;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.Scanner;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


public class ScheduleBuilder {
    public static void main(String args[]) throws IOException {
        
    }
    public ScheduleBuilder(File file) {
        try {
            parse(file);
        } catch(Exception e) {
            System.out.println("Schedule build failure");
        }
    }

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
                break;
            } else if (current.equals("break")) {
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
                // System.out.println(line);
                // System.out.println(reverse(line) + "\n");
            }
        }
        input.close();
        return allCourses;
    }

    protected static String reverse(String str) {
        String reverse = "";
        while (str.indexOf(" ") != -1) {
            reverse += str.substring(str.lastIndexOf(" "));
            str = str.substring(0, str.lastIndexOf(" "));
        }
        reverse += " " + str;
        return reverse.trim();
    }

    //check if have a dash problem in the class times
    //return true if have something like 130- 220 
    private static boolean checkDash(String str) {
        int i = str.lastIndexOf("-"); 
        return (i != -1 && str.substring(i-1, i).matches("\\d+")
                && str.substring(i+1, i+2).equals(" "));
    }

}
