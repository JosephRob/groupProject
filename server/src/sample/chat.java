package sample;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by lex on 16/03/17.
 */
public class chat implements Runnable{
    int port;
    String history;
    chat(int port){
        this.port=port;
        history="";
    }
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
