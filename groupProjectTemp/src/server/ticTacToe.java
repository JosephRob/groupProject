//Joseph Robertson
//Justin Duong
//Clyve Widjaya
package server;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Joseph
 * @date 17/4/8
 */
public class ticTacToe implements Runnable {
    char[][] place;
    int port;
    int count;
    List<String> in;
    String current;

    /**
     * sets board and port
     *
     * @param port
     */
    public ticTacToe(int port){
        count=0;
        in=new ArrayList<>();
        this.port=port;
        place=new char[3][3];
        for (int x=0;x<3;x++)
            for (int y=0;y<3;y++)
                place[x][y]='e';
    }

    /**
     * Handles input from ticTacToeClient and sends back respective data.
     */
    @Override
    public void run() {
        while (true){
            try{
                ServerSocket serverSocket=new ServerSocket(port);
                while (true){
                    Socket socket=serverSocket.accept();
                    BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out=new PrintWriter(socket.getOutputStream());


                    int check=Integer.parseInt(br.readLine());
                    //System.out.println(check);
                    if(check==-1){
                        for (int x=0;x<3;x++){
                            for (int y=0;y<3;y++){
                                place[x][y]='e';
                            }
                        }
                    }
                    else if(check==1) {
                        String username = br.readLine();
                        //System.out.println(username);
                        if (in.size() >= 2) {
                            if (in.contains(username)) {
                                out.println(1);
                                out.flush();
                                int x = Integer.parseInt(br.readLine()), y = Integer.parseInt(br.readLine());
                                char player = 'X';
                                if ((count % 2) == 0) player = 'O';
                                if (x != -1 && y != -1) {
                                    //System.out.println(x+"\t"+y+"\t"+player);
                                    if (in.get(count % in.size()).equals(username)) {
                                        place[x][y] = player;

                                        count++;
                                    }
                                }

                            } else {
                                out.println(0);
                            }

                        } else {
                            out.println(2);
                            for (int x = 0; x < 3; x++)
                                for (int y = 0; y < 3; y++)
                                    place[x][y] = 'e';
                            if (!(in.contains(username))) {
                                in.add(username);
                                current = username;
                            }
                        }
                        out.println(in.get(count % in.size()));
                        for (int x = 0; x < 3; x++)
                            for (int y = 0; y < 3; y++)
                                out.println(place[x][y]);
                        out.flush();
                    }
                    socket.close();
                }
            }
            catch (java.io.IOException e){System.out.println(e);}
        }
    }
}
