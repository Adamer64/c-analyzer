package ajoadamlukas.analyzer.tools;

import ajoadamlukas.analyzer.data.Method;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by lukas on 18.01.2017.
 */
public class MethodsObfuscator {

    // STATIC VARIABLES
    private static File output;
    private static String patternMethods = "(void|int|double)(\\s)([^,])+(\\()(.)+";
    private static String methodsInput = "methods.txt";


    // INSTANCE VARIABLES
    private List<String> lines;
    private  List<Method> methods = new ArrayList<Method>();
//    private String inputFile = null;

    private String[] command = {"compute1(a,b);\n","compute2(&a,&b);\n","compute3(a,b);\n","a = compute4(a,b);\n", "b = compute5(&a,&b);\n"};

    public MethodsObfuscator(String outputName) {
        output = new File(outputName);

        readFile();
    }

    public void obfuscateOverload() {
        for(Method method:methods){
            if (!method.isObfuscated()){
                method.setObfuscated(true);
                for(Method nestedMethod:methods){
                    if (!nestedMethod.isObfuscated() && !method.hasSameSignature(nestedMethod)) {
                        this.rename(method.getName(), nestedMethod.getName());
                        //System.out.println(method.getName()+" chceme "+ nestedMethod.getName()+"\n");
                        nestedMethod.setObfuscated(true);
                        nestedMethod.setName(method.getName());
                    }
                }

            }
        }
    }

    public void obfuscateRenameMethods() {
        int i = 0;
        for(Method method:methods){
            rename(String.valueOf((char)(65+i)),method.getName());
            i++;
        }
    }

    public void obfuscateByInserting() {
        insertMethods();
        List<String> fileContent = null;
        boolean inBody = false;
        int lineOfMain = 0;
        try {
            fileContent = new ArrayList<>(Files.readAllLines(Paths.get(output.getPath()), StandardCharsets.UTF_8));
            for (int i = 0; i < fileContent.size(); i++) {
                if (fileContent.get(i).contains("return 0"))
                    inBody = false;
                if(inBody && i>lineOfMain+4 && (i % 3 ==0)){
                    fileContent.add(i,command[ThreadLocalRandom.current().nextInt(0, 4 + 1)]);
                }
                if (fileContent.get(i).contains("main")) {
                    inBody = true;
                    lineOfMain = i;
                    fileContent.add(i+1,"int a = 5;\n");
                    fileContent.add(i+1,"int b = 10;\n");
                }
            }

            Files.write(Paths.get(Paths.get(output.getPath()).toString()), fileContent, StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertMethods() {
        List<String> fileContent = null;
        try {
            fileContent = new ArrayList<>(Files.readAllLines(Paths.get(output.getPath()), StandardCharsets.UTF_8));
            for (int i = 0; i < fileContent.size(); i++) {
                if (!fileContent.get(i).contains("#include")){
                    int conuter = i;
                    String next = "";
                    List<String> methodsContent = null;
                    methodsContent = new ArrayList<>(Files.readAllLines(Paths.get(methodsInput), StandardCharsets.UTF_8));
                    Iterator<String> iterator = methodsContent.iterator(); // create iterator
                    while (iterator.hasNext()) { // is there a line to read
                        next = iterator.next();
                        fileContent.add(conuter,next);
                        conuter++;
                    }
                    break;
                }
            }
            Files.write(Paths.get(Paths.get(output.getPath()).toString()), fileContent, StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rename(String replacement, String target) {
        if (target.contentEquals("main"))
            return;
        List<String> fileContent = null;
        try {
            fileContent = new ArrayList<>(Files.readAllLines(Paths.get(output.getPath()), StandardCharsets.UTF_8));
            for (int i = 0; i < fileContent.size(); i++) {
                if (fileContent.get(i).contains(target)){
                    String newLine = fileContent.get(i);
                    //System.out.println("BEFORE"+newLine+"\n");
                    //System.out.println(target+"  za  "+replacement+"\n");
                    newLine = newLine.replace(target,replacement);
                    //System.out.println("AFTER"+newLine+"\n");
                    fileContent.set(i, newLine);
                }
            }

            Files.write(Paths.get(Paths.get(output.getPath()).toString()), fileContent, StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void readFile() {
        String next = ""; // we need this for later
        try {
            lines = Files.readAllLines(Paths.get(output.getPath())); // read every line to the lines List

            Iterator<String> iterator = lines.iterator(); // create iterator
            while (iterator.hasNext()) { // is there a line to read
                next = iterator.next(); // as a variable because we would jump to the next line when using iterator.next() again
                try {
                    if (next.matches(patternMethods)) { // match with defined pattern for include statements
                        Method method = new Method(next);
                        methods.add(method); // add every line to the list
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private List<Method> getMethods() {
//        return methods;
//    }

}
