package sample;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by lex on 16/03/17.
 */
public class room implements Runnable{
    int port;
    public room(int port){
        this.port=port;
    }
    @Override
    public void run() {
        while (true){try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket=serverSocket.accept();



                socket.close();
            }
        }catch (Exception e){}}
    }
}
