package client;

/**
 * Created by lex on 15/03/17.
 */
public class Location {
    private int port;
    private String name;
    public Location(int port,String name){
        this.name=name;
        this.port=port;
    }
    public int getPort() {
        return port;
    }
    public String getName() {
        return name;
    }
}
