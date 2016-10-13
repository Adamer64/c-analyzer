package ajoadamlukas.analyzer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
	// write your code here
        Analyzer analyzer = new Analyzer();
        System.out.println("Reading from a file...");
        analyzer.readFile("D:/input.txt");
        analyzer.viewIncludes();
        analyzer.viewVariables();
        analyzer.viewVariableNames();
    }
}
