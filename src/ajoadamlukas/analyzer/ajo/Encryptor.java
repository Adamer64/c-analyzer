package ajoadamlukas.analyzer.ajo;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Encryptor {

    //STATIC VARIABLES
    private static String patternStrings = "(.*)(\")([^%]*)(\")(.*)";
    private static String patternComments = "(.*)(//)(.*)";

    //INSTANCE VARIABLES
    private File output;
    private List<String> lines;
    private List<String> strings = new ArrayList<>();
    private List<String> comments = new ArrayList<>();

    public Encryptor (String inputFile) {
        String outputName = "./obfuscated_" + inputFile;  // outputfile name definition
        output = new File(outputName);

        //  copy input file to output file
        try {
            lines = Files.readAllLines(Paths.get(inputFile));
            PrintWriter pw = new PrintWriter(output);
            for (String line : lines) {
                pw.println(line);
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void obfuscate() throws IOException {
        deleteComments();
        encryptStrings();
        removeWhitespace();
        viewCode();
    }

    private void removeWhitespace() {

        List<String> fileContent;
        try {
            fileContent = new ArrayList<>(Files.readAllLines(Paths.get(output.getPath()), StandardCharsets.UTF_8));  // get current file content

            // put the file content into one line
            StringBuilder sb = new StringBuilder();
            for (String s : fileContent) {
                sb.append(s).append("\n");
            }
            String wholeFile = new String(sb);

            wholeFile = wholeFile.replaceAll("\\s", "");  // get rid of all whitespaces and terminators

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

    private void deleteComments(){
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

    private void encryptStrings() {

        List<String> newStrings = new ArrayList<>();
        try {
            lines = Files.readAllLines(Paths.get(Paths.get(output.getPath()).toString())); // read every line to the lines List

            Iterator<String> iterator = lines.iterator(); // create iterator
            String next; // we need this for later
            while (iterator.hasNext()) { // is there a line to read
                next = iterator.next(); // as a variable because we would jump to the next line when using iterator.next() again
                try {
                    if (next.matches(patternStrings)) { // match with defined pattern for strings
                        newStrings.add(next); // add every line to the list
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            strings.addAll(newStrings);

        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> fileContent;
        try {
            fileContent = new ArrayList<>(Files.readAllLines(Paths.get(output.getPath()), StandardCharsets.UTF_8));

            for (int i = 0; i < fileContent.size(); i++) {

                Iterator<String> iterator = strings.iterator(); // create iterator

                String next;
                while (iterator.hasNext()) { // is there a line to read
                    next = iterator.next(); // as a variable because we would jump to the next line when using iterator.next() again

                    if (fileContent.get(i).equals(next)) {

                        String newLine = "";
                        Pattern pattern = Pattern.compile("(\\\"[^%]*\\\")");
                        Matcher matcher = pattern.matcher(next);
                        if (matcher.find()) {

                            String string = matcher.group(1);

                            String encrypted = encrypt(string, "ajobilec");

                            if (next.contains(string)) {

                                newLine = next.replace(string, encrypted);
                            }
                        }

                        fileContent.set(i, newLine);
                        break;
                    }
                }
            }

            Files.write(Paths.get(output.getPath()), fileContent, StandardCharsets.UTF_8);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void viewCode() {
        System.out.println("Code:");
        try {
            List<String> lines = Files.readAllLines(Paths.get(output.getPath()));

            for (String line : lines) { // is there a line to read
                System.out.println(line);
            }

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

    private String encrypt(String strClearText, String strKey) throws Exception {
        String strData;

        try {
            SecretKeySpec skeyspec=new SecretKeySpec(strKey.getBytes(), "Blowfish");
            Cipher cipher=Cipher.getInstance("Blowfish");
            cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
            byte[] encrypted=cipher.doFinal(strClearText.getBytes());
            strData=new String(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        return strData;
    }

}
