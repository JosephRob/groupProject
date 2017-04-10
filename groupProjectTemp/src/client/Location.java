//Joseph Robertson
//Justin Duong
//Clyve Widjaya
package client;

/**
 * Contains name and port of a sever.
 *
 * @author Joseph
 * @date 17/4/8
 */
public class Location {
    private int port;
    private String name;

    /**
     * Default constructor of Location
     *
     * @param port
     * @param name
     */
    public Location(int port,String name){
        this.name=name;
        this.port=port;
    }

    /**
     * Returns port value
     *
     * @return  the port number
     */
    public int getPort() {
        return port;
    }

    /**
     *  Returns name of the sever.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }
}
