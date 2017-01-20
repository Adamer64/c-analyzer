package ajoadamlukas.analyzer.data;

/**
 * Created by lukas on 19.01.2017.
 */
public class Argument {

    private String name;
    private String type;

    public Argument(String type, String name){
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
