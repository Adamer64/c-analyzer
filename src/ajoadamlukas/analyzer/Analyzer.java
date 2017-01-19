package ajoadamlukas.analyzer;

import com.sun.xml.internal.fastinfoset.util.StringArray;

import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by adame on 13.10.2016.
 */
public class Analyzer {
    protected List<String> lines;
    protected List<String> includes = new ArrayList<>();//test
    protected List<String> variables = new ArrayList<>();
    protected List<String> variableNames = new ArrayList<>();
    protected List<String> newLines = new ArrayList<>();
    protected String patternIncludes = "(#include)(.)+";
    protected String patternVariables = "(\\t|\\s)*(int|char)(\\**)(\\s)(\\**)(\\w+)(\\s*)(=)(\\s*)((\\d+)|(\".*\"))(;)";
    protected String patternVariablesArray = "(\\t|\\s)*(double|char)(\\**)(\\s)(\\**)(\\w+)(\\[.+\\])(\\s*)(=)(\\s*)(\\{.*\\})(;)";
    protected boolean firstRun = true;

    public void Analyzer() {
    }


    public void readFile(String inputFile) throws IOException {
        String next = ""; // we need this for later
        List<String> newIncludes = new ArrayList<>(); // we can't just add to the original List
        List<String> newVariables = new ArrayList<>();
        List<String> newVariableNames = new ArrayList<>();
        List<String> newLinesAdded = new ArrayList<>();
        lines = Files.readAllLines(Paths.get(inputFile)); // read every line to the lines List
        Iterator<String> iterator = lines.iterator(); // create iterator

        if(firstRun) {
            firstRun = false;
            while (iterator.hasNext()) { // is there a line to read
                next = iterator.next(); // as a variable because we would jump to the next line when using iterator.next() again
                try {
                    if (next.matches(patternIncludes)) { // match with defined pattern for include statements
                        newIncludes.add(next); // add every line to the list
                    } else if (next.matches(patternVariables)) { // else for Variables
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

                }
            }

            includes.addAll(newIncludes); // get all the new includes from the temporary List to the one we will use later
            variables.addAll(newVariables);
            variableNames.addAll(newVariableNames);
        }
        else
        {
            System.out.println("Second run:");
            lines = Files.readAllLines(Paths.get(inputFile)); // read every line to the lines List
            Iterator<String> varIterator = getVariableNames().iterator();
            List<String> tempLines = new ArrayList<>();
            int i=0;
            String[] nextVar = new String[getVariableNames().size()];
            while(varIterator.hasNext()) {
                nextVar[i] = varIterator.next();
                System.out.println(nextVar[i]);
                i++;
            }
            int k=0;

                while(k<i) {
//                    String randomString = Long.toHexString(Double.doubleToLongBits(Math.random()));
                    String randomString = "a"+Integer.toString(k);
                    String str = "(.*)(\\s)(\\[*)(" + nextVar[k] + ")(\\]*)(\\s*)(.*)"; // test
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
                                System.out.println("WTF: "+ m.group(1) );
                                rest = m.group(2);
                                m.appendReplacement(sBuffer, Matcher.quoteReplacement(randomString+rest));
                            }
                            System.out.println("Replacing: ");
                            m.appendTail(sBuffer);
                            System.out.println(sBuffer.toString());
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

            }
        }

    public void viewIncludes() {
        System.out.println("Includes:");
        Iterator<String> iterator = includes.iterator(); // iterate
        while (iterator.hasNext()) {
            System.out.println(iterator.next()); // print it
        }
        System.out.println();
    }

    public List<String> getIncludes() {
        return includes;
    }

    public List<String> getVariableNames() { return variableNames; }

    public List<String> getVariables() { return variables; }

    public List<String> getNewLines() { return newLines;}

    public void viewVariableNames() {
        System.out.println("Variable names:");
        Iterator<String> iterator = variableNames.iterator(); // iterate
        while (iterator.hasNext()) {
            System.out.println(iterator.next()); // print it
        }
        System.out.println();
    }

    public void viewVariables() {
        System.out.println("Variable lines:");
        Iterator<String> iterator = variables.iterator(); // iterate
        while (iterator.hasNext()) {
            System.out.println(iterator.next()); // print it
        }
        System.out.println();
//        System.out.println(Long.toHexString(Double.doubleToLongBits(Math.random())));
    }

    public void viewNewLines() {
        System.out.println("Output:");
        Iterator<String> iterator = getNewLines().iterator(); // iterate
        while (iterator.hasNext()) {
            System.out.println(iterator.next()); // print it
        }
        System.out.println();
    }
}
