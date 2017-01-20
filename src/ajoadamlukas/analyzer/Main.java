package ajoadamlukas.analyzer;

import ajoadamlukas.analyzer.ajo.Encryptor;
import ajoadamlukas.analyzer.ajo.Formatter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    // STATIC VARIABLES
    private static File output;
    private static String input = "tesco.c";

    public static void main(String[] args) throws IOException {
        obfuscate(input);
        viewCode();
    }

    private static void obfuscate(String inputFile) {

        // output definition
        String outputName = "./obfuscated_" + inputFile;  // outputfile name definition
        output = new File(outputName);

        // tools declarations
        Encryptor encryptor = new Encryptor("tesco.c");
        Formatter formatter = new Formatter("tesco.c");

        // methods used
        encryptor.encryptStrings();
        formatter.deleteComments();
        formatter.removeWhitespace();
    }

    private static void viewCode() {
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

}
