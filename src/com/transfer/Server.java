package com.transfer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final String receivePath = "/home/matjaz/Programming/File-transfer/Received/";

    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;

    public static void main(String[] args) throws IOException {
        try(ServerSocket serverSocket = new ServerSocket(5000)){ //listening to port:5000
            Socket clientSocket = serverSocket.accept();

            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

            //SENDER PART
            File save = new File("ServerSave");

            Sender sender = new Sender();
            BufferedWriter bw = sender.Send(dataOutputStream, receivePath, save);

            //RECEIVER PART
            Receiver.Receive(dataInputStream, receivePath, bw);

            dataInputStream.close();
            dataOutputStream.close();

            bw.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}