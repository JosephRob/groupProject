package server;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.ServerSocket;
import java.io.PrintWriter;
import java.util.HashMap;
import java.net.Socket;

/**
 * Created by lex on 16/03/17.
 */
public class room implements Runnable{
    int port;
    HashMap <String,Integer[]> positions=new HashMap<>();
    HashMap <String,Double[]> colors=new HashMap<>();
    public room(int port){
        this.port=port;
    }
    @Override
    public void run() {
        while (true){try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket=serverSocket.accept();
                BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out=new PrintWriter(socket.getOutputStream());

                if(Integer.parseInt(br.readLine())==1) {
                    String currentUser = br.readLine();
                    Integer[] place = new Integer[2];
                    Double[] color = new Double[3];
                    for (int x = 0; x < 2; x++)
                        place[x] = Integer.parseInt(br.readLine());
                    for (int x = 0; x < 3; x++)
                        color[x] = Double.parseDouble(br.readLine());

                    positions.put(currentUser, place);
                    colors.put(currentUser, color);


                    for (String name : positions.keySet()) {
                        //System.out.println(name+"\t"+positions.get(name)[0]+"\t"+positions.get(name)[1]);
                        out.println(name);
                        for (int x = 0; x < 2; x++)
                            out.println(positions.get(name)[x]);
                        for (int x = 0; x < 3; x++)
                            out.println(colors.get(name)[x]);
                        out.flush();

                    }
                }
                else{//exit
                    String userName=br.readLine();
                    if (positions.containsKey(userName)) {
                        positions.remove(userName);
                        colors.remove(userName);
                        //System.out.println("removing "+userName+" from room "+port);
                    }
                }

                socket.close();
            }
        }
        catch (Exception e){System.out.println(e);System.exit(1);}}
    }
}
