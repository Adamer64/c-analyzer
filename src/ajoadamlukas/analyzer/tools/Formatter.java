package ajoadamlukas.analyzer.tools;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by AB on 20.1.2017.
 */
public class Formatter {

    // STATIC VARIABLES
    private static File output;
    private static String patternComments = "(.*)(//)(.*)";

    //INSTANCE VARIABLES
    private List<String> comments = new ArrayList<>();
    private List<String> lines;

    public Formatter (String outputName) {
        output = new File(outputName);
    }

    public void deleteComments(){
        List<String> newComments = new ArrayList<>();

        try {
            lines = Files.readAllLines(Paths.get(output.getPath())); // read every line to the lines List

            Iterator<String> iterator = lines.iterator(); // create iterator
            String next; // we need this for later
            while (iterator.hasNext()) { // is there a line to read
                next = iterator.next(); // as a variable because we would jump to the next line when using iterator.next() again
                try {
                    // find all lines containing comments
                    if (next.matches(patternComments)){
                        newComments.add(next);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            comments.addAll(newComments); // add lines to collection

        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> fileContent;
        try {
            fileContent = new ArrayList<>(Files.readAllLines(Paths.get(output.getPath()), StandardCharsets.UTF_8));  //obtain file content

            // check all lines for comments and handle remove them
            for (int i = 0; i < fileContent.size(); i++) {

                Iterator<String> iterator = comments.iterator(); // create iterator

                String next;
                while (iterator.hasNext()) { // is there a line to read
                    next = iterator.next(); // as a variable because we would jump to the next line when using iterator.next() again

                    if (fileContent.get(i).equals(next)) {
                        String newLine = "";

                        // check for comment
                        Pattern pattern = Pattern.compile("(\\s*//.*)");
                        Matcher matcher = pattern.matcher(next);
                        if (matcher.find()) {
                            String comment = matcher.group(1);
                            if (next.contains(comment)){
                                newLine = next.replace(comment, ""); // remove comments
                            }
                        }

                        fileContent.set(i, newLine);
                        break;
                    }
                }
            }

            Files.write(Paths.get(output.getPath()), fileContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeWhitespace() {

        List<String> fileContent;
        try {
            fileContent = new ArrayList<>(Files.readAllLines(Paths.get(output.getPath()), StandardCharsets.UTF_8));  // get current file content

            // put the file content into one line
            StringBuilder sb = new StringBuilder();
            for (String s : fileContent) {
                sb.append(s).append("\n");
            }
            String wholeFile = new String(sb);

            wholeFile = wholeFile.replaceAll("\\n", "");  // get rid of all whitespaces and terminators
            wholeFile = wholeFile.replaceAll("\\s+", " ");

            // input linefeed on every 50th position
            int length = 50;
            int i = 1;
            while ( i * length < wholeFile.length()) {
                wholeFile = insertCharAt(wholeFile, '\n', i * length);
                i++;
            }

            // write changes to output
            PrintWriter out = new PrintWriter(output.getName());
            out.println(wholeFile);
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String insertCharAt(String st, char ch, int index){

        // null string case
        if (st == null){
            throw new NullPointerException("Null string!");
        }

        // invalid index case
        if (index < 0 || index > st.length())
        {
            throw new IndexOutOfBoundsException("Try to insert at negative location or outside of string");
        }

        // return string with inserted char
        return st.substring(0, index)+ch+st.substring(index, st.length());
    }
}