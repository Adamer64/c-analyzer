package ajoadamlukas.analyzer.tools;

import com.sun.xml.internal.fastinfoset.util.StringArray;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by adame on 13.10.2016.
 */
public class VariablesObfuscator {

    // STATIC VARIABLES
    private static File output;
    private static String patternVariables = "(\\t|\\s)*(int|char)(\\**)(\\s)(\\**)(\\w+)(\\s*)(=)(\\s*)((\\d+)|(\".*\"))(;)";
    private static String patternVariablesArray = "(\\t|\\s)*(double|char)(\\**)(\\s)(\\**)(\\w+)(\\[.+\\])(\\s*)(=)(\\s*)(\\{.*\\})(;)";

    // INSTANCE VARIABLES
    private List<String> lines;
    private List<String> includes = new ArrayList<>(); //test
    private List<String> variables = new ArrayList<>();
    private List<String> variableNames = new ArrayList<>();
    private List<String> newLines = new ArrayList<>();

    public VariablesObfuscator(String outputName) {
        output = new File(outputName);
    }

    public void obfuscateVariables() {

        // read file first
        try { readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String next = ""; // we need this for later

        List<String> newLinesAdded = new ArrayList<>();
        List<String> tempLines = new ArrayList<>();

        Iterator<String> iterator = lines.iterator(); // create iterator
        Iterator<String> varIterator = getVariableNames().iterator();
        String[] nextVar = new String[getVariableNames().size()];

        int k = 0;
        int i = 0;
        while(varIterator.hasNext()) {
            nextVar[i] = varIterator.next();
//                System.out.println(nextVar[i]);
            i++;
        }

        while(k < i) {
//                    String randomString = Long.toHexString(Double.doubleToLongBits(Math.random()));
            String randomString = "a"+Integer.toString(k);
            String str = "(.*)(\\s)(\\[*)(" + nextVar[k] + ")(\\]*)(\\s*)(.*)";
//                    String str = "(.*)"+nextVar[k]+"(.*)";
//                    String str2 = "("+nextVar[k]+")([^()\\]=\\s\\+\\-\\*]*[^\\(\\[])";
            String str2 = "("+nextVar[k]+")([^; \\w])"; //(?<!\w)

            while (iterator.hasNext()) { // is there a line to read
                next = iterator.next(); // as a variable because we would jump to the next line when using iterator.next() again

                if (next.matches(str)) { // match with defined pattern for include statements
                    Pattern p = Pattern.compile(str2); // initialize pattern
                    Matcher m = p.matcher(next); // and matcher
                    StringBuffer sBuffer = new StringBuffer();
                    boolean found = false;
                    while (found = m.find()) { // did we find anything?
                        String rest="";
//                                System.out.println("WTF: "+ m.group(1) );
                        rest = m.group(2);
                        m.appendReplacement(sBuffer, Matcher.quoteReplacement(randomString+rest));
                    }
//                            System.out.println("Replacing: ");
                    m.appendTail(sBuffer);
//                            System.out.println(sBuffer.toString());
                    newLinesAdded.add(sBuffer.toString());
                }
                else {
                    newLinesAdded.add(next);
                }
            }
            k++;
            tempLines.clear();
            tempLines.addAll(newLinesAdded);
            newLinesAdded.clear();
            iterator = tempLines.listIterator();
        }

        newLines.addAll(tempLines);

        // write to the file
        try {
            Files.write(Paths.get(output.getPath()), newLines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFile() throws IOException {
        lines = Files.readAllLines(Paths.get(output.getPath())); // read every line to the lines List

        List<String> newVariables = new ArrayList<>();
        List<String> newVariableNames = new ArrayList<>();

        String next = ""; // we need this for later
        Iterator<String> iterator = lines.iterator(); // create iterator

        while (iterator.hasNext()) { // is there a line to read
            next = iterator.next(); // as a variable because we would jump to the next line when using iterator.next() again
            try {
                if (next.matches(patternVariables)) { // else for Variables
                    newVariables.add(next); // add the whole line just to be safe
                    Pattern p = Pattern.compile(patternVariables); // initialize pattern
                    Matcher m = p.matcher(next); // and matcher

                    if (m.find()) { // did we find anything?
                        newVariableNames.add(m.group(6)); // if so, add group 5 (the variable name) to the list of variable names
                    }

                } else if (next.matches(patternVariablesArray)) {
                    newVariables.add(next);
                    Pattern p = Pattern.compile(patternVariablesArray); // initialize pattern
                    Matcher m = p.matcher(next); // and matcher

                    if (m.find()) { // did we find anything?
                        newVariableNames.add(m.group(6)); // if so, add group 5 (the variable name) to the list of variable names
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        variables.addAll(newVariables);
        variableNames.addAll(newVariableNames);
    }

    private List<String> getIncludes() { return includes; }

    private List<String> getNewLines() { return newLines;}

    private List<String> getVariables() { return variables; }

    private List<String> getVariableNames() { return variableNames; }

//    private void viewIncludes() {
//        System.out.println("Includes:");
//        Iterator<String> iterator = includes.iterator(); // iterate
//        while (iterator.hasNext()) {
//            System.out.println(iterator.next()); // print it
//        }
//        System.out.println();
//    }
//
//    private void viewVariableNames() {
//        System.out.println("Variable names:");
//        Iterator<String> iterator = variableNames.iterator(); // iterate
//        while (iterator.hasNext()) {
//            System.out.println(iterator.next()); // print it
//        }
//        System.out.println();
//    }
//
//    private void viewVariables() {
//        System.out.println("Variable lines:");
//        Iterator<String> iterator = variables.iterator(); // iterate
//        while (iterator.hasNext()) {
//            System.out.println(iterator.next()); // print it
//        }
//        System.out.println();
////        System.out.println(Long.toHexString(Double.doubleToLongBits(Math.random())));
//    }
//
//    private void viewNewLines() {
////        System.out.println("Output:");
//        Iterator<String> iterator = getNewLines().iterator(); // iterate
//        while (iterator.hasNext()) {
//            System.out.println(iterator.next()); // print it
//        }
//        System.out.println();
//    }
}
