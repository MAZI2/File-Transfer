package com.mazi.Filetransfer.filetransfer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final String receivePath = "/home/matjaz/IdeaProjects/File transfer new workspace/File-transfer/dirs/Received/";

    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;

    public static void sync() {
        try(ServerSocket serverSocket = new ServerSocket(5000)){ //listening to port:5000
            Socket clientSocket = serverSocket.accept();
            System.out.println("accepted!");

            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

            //SENDER

            File save = new File("/home/matjaz/IdeaProjects/File transfer new workspace/File-transfer/dirs/ServerSave");
            save.createNewFile();

            Sender sender = new Sender();
            BufferedWriter bw = sender.Send(dataOutputStream, receivePath, save);

            //RECEIVER
            Receiver.Receive(dataInputStream, receivePath, bw);

            dataInputStream.close();
            dataOutputStream.close();

            bw.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}