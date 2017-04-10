//Joseph Robertson
//Justin Duong
//Clyve Widjaya
package server;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Joseph
 * @date 17/4/8
 */
public class chat implements Runnable{
    int port;
    String history;

    /**
     * default constructor
     *
     * @param port
     */
    public chat(int port){
        this.port=port;
        history="";
    }

    /**
     * updates and sends chat information.
     */
    public void run(){
        while (true){try {
            ServerSocket serverSocket=new ServerSocket(port);
            while (true){
                Socket socket=serverSocket.accept();
                BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out=new PrintWriter(socket.getOutputStream());

                String username=br.readLine();
                String line=br.readLine();

                if (!(line.equals(" "))) {
                    history += "\n" + username + ":\n\t" + line;
                }

                out.println(history);
                out.flush();
                socket.close();
            }

        }catch (Exception e){System.out.println(e);}}
    }
}
