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

    // STATIC VARIABLES
    private static String patternStrings = "(.*)(\")([^%]*)(\")(.*)";
    private static File output;

    // INSTANCE VARIABLES
    private List<String> lines;
    private List<String> strings = new ArrayList<>();

    public Encryptor (String outputName) {
        output = new File(outputName);

        // read current state of output file
        try {
            lines = Files.readAllLines(Paths.get(outputName));
            PrintWriter pw = new PrintWriter(output);
            for (String line : lines) {
                pw.println(line);
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void encryptStrings() {

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
