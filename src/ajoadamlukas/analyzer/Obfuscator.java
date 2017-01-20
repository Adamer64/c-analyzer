package ajoadamlukas.analyzer;

import ajoadamlukas.analyzer.tools.VariablesObfuscator;
import ajoadamlukas.analyzer.tools.Encryptor;
import ajoadamlukas.analyzer.tools.Formatter;
import ajoadamlukas.analyzer.tools.MethodsObfuscator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Obfuscator {

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

        // generate first output file
        try {
            List<String> originalContent = new ArrayList<>(Files.readAllLines(Paths.get(input), StandardCharsets.UTF_8));
            Files.write(Paths.get(output.getPath()), originalContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // tools declarations
        MethodsObfuscator methodObfuscator = new MethodsObfuscator(output.getName());
        VariablesObfuscator variablesObfuscator = new VariablesObfuscator(output.getName());
        Formatter formatter = new Formatter(output.getName());
        Encryptor encryptor = new Encryptor(output.getName());

        // methods used
        methodObfuscator.obfuscateByInserting();
        methodObfuscator.obfuscateRenameMethods();
        methodObfuscator.obfuscateOverload();
        variablesObfuscator.obfuscateVariables();
        formatter.deleteComments();
        encryptor.encryptStrings();
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
