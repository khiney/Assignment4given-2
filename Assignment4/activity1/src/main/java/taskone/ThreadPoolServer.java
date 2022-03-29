/**
 File: Server.java
 Author: Student in Fall 2020B
 Description: Server class in package taskone.
 */

package src.main.java.taskone;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

/**
 * Class: Server
 * Description: Server tasks.
 */
class ThreadPoolServer implements Runnable{
    Socket sock = null;
    OutputStream out = null;
    InputStream in = null;
    static int id = 0;

    public ThreadPoolServer(Socket sock, int id) throws IOException{
        this.sock = sock;
        out = this.sock.getOutputStream();
        in = sock.getInputStream();
        this.id = id;
        run();
    }

    public static void main(String[] args) throws Exception {
        int port;
        StringList strings = new StringList();
        Executor pool=null;

        /*if (args.length != 1) {
            // gradle runServer -Pport=9099 -q --console=plain
            System.out.println("Usage: gradle runServer -Pport=9099 -q --console=plain");
            System.exit(1);
        }*/
        if (args.length < 2) {
            // gradle runServer -Pport=9099 -q --console=plain
            System.out.println("Usage: gradle runServer -Pport=9099 -Ptnum=3 -q --console=plain");
            System.exit(1);
        }

        port = -1;
        try {
            port = Integer.parseInt(args[0]);
            pool = Executors.newFixedThreadPool(Integer.parseInt(args[1]));
        } catch (NumberFormatException nfe) {
            System.out.println("[Port] must be an integer");
            System.exit(2);
        }
        ServerSocket server = new ServerSocket(port);
        System.out.println("Server Started...");
        while (true) {
            System.out.println("Accepting a Request...");
            Socket sock = server.accept();

            /*Performer performer = new Performer(sock, strings);
            performer.doPerform();*/

            Runnable worker = new ThreadPoolServer(sock, id++);
            pool.execute(worker);
            try {
                System.out.println("close socket of client ");
                sock.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        StringList strings = new StringList();
        ServerSocket server = null;
        Performer performer = new Performer(sock, strings);
        performer.doPerform();
    }
}
