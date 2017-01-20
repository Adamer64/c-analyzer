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

    private File output;
    private String outputName = "./undefined.c";


    private List<String> lines;
    private List<String> strings = new ArrayList<>();
    private List<String> comments = new ArrayList<>();
    private List<String> stringValues = new ArrayList<>();
    private List<String> commentValues = new ArrayList<>();
    private String patternStrings = "(.*)(\")([^%]*)(\")(.*)";
    private String patternComments = "(.*)(//)(.*)";

    public Encryptor (String inputFile) {
        outputName = new String("./obfuscated_" + inputFile);
        output = new File(outputName);

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

        extractValues();
        deleteComments();
        encryptStrings();
        removeWhitespace();

        viewCode();
    }

    private String insertCharAt(String st, char ch, int index){
        //1 Exception if st == null
        //2 Exception if index<0 || index>st.length()

        if (st == null){
            throw new NullPointerException("Null string!");
        }

        if (index < 0 || index > st.length())
        {
            throw new IndexOutOfBoundsException("Try to insert at negative location or outside of string");
        }
        return st.substring(0, index)+ch+st.substring(index, st.length());
    }

    private void removeWhitespace() {

        List<String> fileContent = null;
        try {
            fileContent = new ArrayList<>(Files.readAllLines(Paths.get(output.getPath().toString()), StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (String s : fileContent) {

                sb.append(s + "\n");
            }
            String wholeFile = new String(sb.toString());

            wholeFile = wholeFile.replaceAll("\\s","");

            int length = 50;
            int i = 1;
            while ( i * length < wholeFile.length()) {
                wholeFile = insertCharAt(wholeFile, '\n', i * length);
                i++;
            }

            PrintWriter out = new PrintWriter(output.getName());
            out.println(wholeFile);
            out.close();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void extractValues() {
        Iterator<String> iterator1 = strings.iterator(); // create iterator

        String next = ""; // we need this for later
        while (iterator1.hasNext()) { // is there a line to read
            next = iterator1.next(); // as a variable because we would jump to the next line when using iterator.next() again
            try {
                Pattern pattern = Pattern.compile("\"(.*)\"");
                Matcher matcher = pattern.matcher(next);
                if (matcher.find()) {
                    stringValues.add(matcher.group(1));
                }
            } catch (Exception e) {
                //TODO handle
            }
        }

        Iterator<String> iterator2 = comments.iterator(); // create iterator

        while (iterator2.hasNext()) { // is there a line to read
            next = iterator2.next(); // as a variable because we would jump to the next line when using iterator.next() again
            try {
                Pattern pattern = Pattern.compile("(//.*)");
                Matcher matcher = pattern.matcher(next);
                if (matcher.find()) {
                    commentValues.add(matcher.group(1));
                }
            } catch (Exception e) {
                //TODO handle
            }
        }

    }

    private String encrypt(String strClearText, String strKey) throws Exception {
        String strData="";

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

    private void deleteComments(){
        List<String> newComments = new ArrayList<>();

        try {
            lines = Files.readAllLines(Paths.get(output.getPath().toString())); // read every line to the lines List

            Iterator<String> iterator = lines.iterator(); // create iterator
            String next = ""; // we need this for later
            while (iterator.hasNext()) { // is there a line to read
                next = iterator.next(); // as a variable because we would jump to the next line when using iterator.next() again
                try {
                    if (next.matches(patternComments)){
                        newComments.add(next);
                    }
                } catch (Exception e) {
                    //TODO handle
                }
            }
            comments.addAll(newComments);

        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> fileContent = null;
        try {
            fileContent = new ArrayList<>(Files.readAllLines(Paths.get(output.getPath().toString()), StandardCharsets.UTF_8));

            for (int i = 0; i < fileContent.size(); i++) {

                Iterator<String> iterator = comments.iterator(); // create iterator

                String next = "";
                while (iterator.hasNext()) { // is there a line to read
                    next = iterator.next(); // as a variable because we would jump to the next line when using iterator.next() again

                    if (fileContent.get(i).equals(next)) {
                        String newLine = new String();
                        Pattern pattern = Pattern.compile("(\\s*//.*)");
                        Matcher matcher = pattern.matcher(next);
                        if (matcher.find()) {
                            String comment = matcher.group(1);
                            if (next.contains(comment)){
                                newLine = next.replace(comment, "");
                            }
                        }

                        fileContent.set(i, newLine);
                        break;
                    }
                }
            }

            Files.write(Paths.get(output.getPath().toString()), fileContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void encryptStrings() {

        List<String> newStrings = new ArrayList<>();
        try {
            lines = Files.readAllLines(Paths.get(Paths.get(output.getPath()).toString())); // read every line to the lines List

            Iterator<String> iterator = lines.iterator(); // create iterator
            String next = ""; // we need this for later
            while (iterator.hasNext()) { // is there a line to read
                next = iterator.next(); // as a variable because we would jump to the next line when using iterator.next() again
                try {
                    if (next.matches(patternStrings)) { // match with defined pattern for strings
                        newStrings.add(next); // add every line to the list
                    }
                } catch (Exception e) {
                    //TODO handle
                }
            }

            strings.addAll(newStrings);

        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> fileContent = null;
        try {
            fileContent = new ArrayList<>(Files.readAllLines(Paths.get(output.getPath()), StandardCharsets.UTF_8));

            for (int i = 0; i < fileContent.size(); i++) {

                Iterator<String> iterator = strings.iterator(); // create iterator

                String next = "";
                while (iterator.hasNext()) { // is there a line to read
                    next = iterator.next(); // as a variable because we would jump to the next line when using iterator.next() again

                    if (fileContent.get(i).equals(next)) {

                        String newLine = new String();
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
                    else {
//                        System.out.println(next);
                    }
                }
            }

            Files.write(Paths.get(output.getPath().toString()), fileContent, StandardCharsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void viewStrings() {
        System.out.println("String lines:");
        for (String string : strings) {
            System.out.println(string);
        }
        System.out.println();

        System.out.println("Strings:");
        for (String stringValue : stringValues) {
            System.out.println(stringValue);
        }
        System.out.println();
    }

    private void viewComments() {
        System.out.println("Comment lines:");
        for (String comment : comments) {
            System.out.println(comment);
        }
        System.out.println();

        System.out.println("Comments: ");
        for (String commentValue : commentValues) {
            System.out.println(commentValue);
        }
        System.out.println();
    }

    private void viewCode() {
        System.out.println("Code:");
        try {
            List<String> lines = Files.readAllLines(Paths.get(output.getPath().toString()));

            Iterator<String> iterator = lines.iterator(); // create iterator

            String next = ""; // we need this for later
            while (iterator.hasNext()) { // is there a line to read
                System.out.println(iterator.next());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
