package ajoadamlukas.analyzer;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.SocketHandler;

/**
 * Created by lukas on 18.01.2017.
 */
public class Method{
    private String returnType;
    private String methodName;
    private List<Argument> arguments = new ArrayList<Argument>();
    protected String patternArgs = "(.)*(void|int|double|char|String)(.)+";
    boolean obfuscated;


    public Method(String line){
        Execute(line);
        this.obfuscated = false;
    }

    public boolean isObfuscated(){
        return this.obfuscated;
    }

    public boolean hasSameSignature(Method method){
        if (/*this.hasSameReturnType(method) &&*/ this.hasSameArguments(method))
            return true;
        return false;
    }

    public boolean hasSameArguments(Method method){
        if(this.getNumOfArgs()==method.getNumOfArgs()){
            List<Argument> args = method.getArguments();
            for(int i=0;i<this.getNumOfArgs();i++){
                if(!this.arguments.get(i).getType().contentEquals(args.get(i).getType())){
                    //System.out.println(this.arguments.get(i).getType()+" "+args.get(i).getType());
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean hasSameReturnType(Method method){
        if(this.returnType.contentEquals(method.getReturnType()))
            return true;
        return false;
    }

    public void setObfuscated(boolean obfuscated){
        this.obfuscated = obfuscated;
    }

    public void Execute(String line){
        String[] parts = line.split(" ",2);
        this.setReturnType(parts[0]);
        line = parts[1];
        parts = line.split("\\(");
        this.setName(parts[0]);
        line = parts[1];
        parts = line.split(",");
        for(String part:parts){
            if (part.matches(patternArgs)) {
                if (part.contains(")"))
                    part = part.substring(0,part.indexOf(')'));
                this.createArgument(part);
            }
        }

    }

    public void setName(String name){
        this.methodName = name;
        //System.out.println("name"+methodName+"\n");
    }

    public void setReturnType(String type){
        this.returnType = type;
        //System.out.println("return"+returnType+"\n");
    }


    public String getName(){
        return methodName;
    }

    public String getReturnType(){
        return returnType;
    }

    public List<Argument> GetArgs(){
        return arguments;
    }

    public void createArgument(String arg){
        arg.trim();
        String parts[] = arg.split(" ");
        arguments.add(new Argument(parts[0],parts[1]));
    }

    public List<Argument> getArguments(){
        return this.arguments;
    }

    public void printSignature(){
        System.out.println("Name: "+this.getName()+"\n" );
        System.out.println("Return type : "+this.getReturnType()+"\n" );
        printArguments();
    }

    public void printArguments(){
        for(Argument arg:arguments){
            System.out.println(arg.getType()+" "+arg.getName()+"\n");
        }
    }

    public int getNumOfArgs(){
        return arguments.size();
    }
}
