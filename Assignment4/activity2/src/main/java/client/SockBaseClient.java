package activity2.src.main.java.client;

import java.net.*;
import java.io.*;

import org.json.*;

import buffers.RequestProtos.Request;
import buffers.ResponseProtos.Response;
import buffers.ResponseProtos.Entry;

import java.util.*;
import java.util.stream.Collectors;

class SockBaseClient {

    public static void main (String args[]) throws Exception {
        Socket serverSock = null;
        OutputStream out = null;
        InputStream in = null;
        int i1=0, i2=0;
        int port = 9099; // default port
        boolean greet = true;
        boolean userChoice = false;
        boolean playGame = false;

        // Make sure two arguments are given
        if (args.length != 2) {
            System.out.println("Expected arguments: <host(String)> <port(int)>");
            System.exit(1);
        }
        String host = args[0];
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port] must be integer");
            System.exit(2);
        }

        // Ask user for username

        try {
            System.out.println("Please provide your name for the server.");
            // connect to the server
            serverSock = new Socket(host, port);
            while (true) {

                BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
                String strToSend = stdin.readLine();
                if(strToSend.equalsIgnoreCase("quit")){
                    Request op = Request.newBuilder()
                            .setOperationType(Request.OperationType.QUIT)
                            .build();
                    Response response;

                    // write to the server
                    out = serverSock.getOutputStream();
                    in = serverSock.getInputStream();

                    op.writeDelimitedTo(out);

                    // read from the server
                    response = Response.parseDelimitedFrom(in);

                    // print the server response.
                    System.out.println(response.getMessage());
                    break;
                }else if(greet) {
                    // Build the first request object just including the name
                    Request op = Request.newBuilder()
                            .setOperationType(Request.OperationType.NAME)
                            .setName(strToSend).build();
                    Response response;

                    // write to the server
                    out = serverSock.getOutputStream();
                    in = serverSock.getInputStream();

                    op.writeDelimitedTo(out);

                    // read from the server
                    response = Response.parseDelimitedFrom(in);

                    // print the server response.
                    System.out.println(response.getMessage());

                    System.out.println("* \nWhat would you like to do? \n 1 - to see the leader board \n " +
                            "2 - to enter a game \n 3 - quit the game");
                    greet = false;
                    userChoice=true;
                }else if(userChoice){
                    if(strToSend.equalsIgnoreCase("1")){
                        Request op = Request.newBuilder()
                                .setOperationType(Request.OperationType.LEADER)
                                .setName(strToSend).build();
                        Response response;
                        out = serverSock.getOutputStream();
                        in = serverSock.getInputStream();

                        op.writeDelimitedTo(out);

                        // read from the server
                        response = Response.parseDelimitedFrom(in);

                        // print the server response.
                        System.out.println(response.getMessage());
                    }else if(strToSend.equalsIgnoreCase("2")){
                        Request op = Request.newBuilder()
                                .setOperationType(Request.OperationType.NEW)
                                .setName(strToSend).build();
                        Response response;
                        out = serverSock.getOutputStream();
                        in = serverSock.getInputStream();

                        op.writeDelimitedTo(out);

                        // read from the server
                        response = Response.parseDelimitedFrom(in);

                        // print the server response.
                        if(response.hasMessage()){              // in case there's an error
                            System.out.println(response.getMessage());
                        }else{
                            System.out.println(response.getImage());
                            System.out.println(response.getTask());
                            playGame = true;
                            userChoice = false;
                        }
                    }else if(strToSend.equalsIgnoreCase("3")){
                        Request op = Request.newBuilder()
                                .setOperationType(Request.OperationType.QUIT)
                                .setName(strToSend).build();
                        Response response;
                        out = serverSock.getOutputStream();
                        in = serverSock.getInputStream();

                        op.writeDelimitedTo(out);

                        // read from the server
                        response = Response.parseDelimitedFrom(in);

                        // print the server response.
                        System.out.println(response.getImage());
                        System.out.println(response.getTask());
                    }


                }else if(playGame){
                    Request op = Request.newBuilder()
                            .setOperationType(Request.OperationType.ROWCOL)
                            .setRow(Character.getNumericValue(strToSend.charAt(0)))
                            .setColumn(Character.getNumericValue(strToSend.charAt(1)))
                            .build();
                    Response response;
                    out = serverSock.getOutputStream();
                    in = serverSock.getInputStream();

                    op.writeDelimitedTo(out);

                    // read from the server
                    response = Response.parseDelimitedFrom(in);

                    // print the server response.
                    if(response.getResponseType()==Response.ResponseType.ERROR){              // in case there's an error
                        System.out.println(response.getMessage());
                    }else if(response.getResponseType()==Response.ResponseType.WON){
                        System.out.println(response.getImage());
                        System.out.println("You have WON the game!");
                        System.out.println("* \nWhat would you like to do? \n 1 - to see the leader board \n " +
                                "2 - to enter a game \n 3 - quit the game");
                        playGame = false;
                        userChoice = true;
                        /*if (response.getHit()){
                            System.out.println("You hit a ship!");
                        }else{
                            System.out.println("You missed...");
                        }
                        System.out.println(response.getImage());
                        System.out.println(response.getTask());*/
                    }else{
                        System.out.println(response.getImage());
                        System.out.println(response.getTask());
                        if (response.getHit()){
                            System.out.println("You hit a ship!");
                        }else{
                            System.out.println("You missed...");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null)   in.close();
            if (out != null)  out.close();
            if (serverSock != null) serverSock.close();
        }
    }
}


