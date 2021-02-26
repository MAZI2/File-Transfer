package com.transfer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
    private static final String receivePath = "/home/matjaz/Programming/File-transfer/Received/";

    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;
    private static ArrayList<File> filesArr = new ArrayList<File>();
    private static ArrayList<String> saves = new ArrayList<String>();
    private static ArrayList<String> toRemove = new ArrayList<String>();

    public static void main(String[] args) throws IOException {
        try(ServerSocket serverSocket = new ServerSocket(5000)){ //listening to port:5000
            Socket clientSocket = serverSocket.accept();

            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

            //RECEIVER PART
            File save = new File("ServerSave");

            Sender.checkForDeleted(save, toRemove);

            FileOutputStream fos = new FileOutputStream(save, true);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            Receiver.Receive(dataInputStream, receivePath, bw);

            //SENDER PART
            Scanner scanner = new Scanner(save);
            while (scanner.hasNextLine()) {
                saves.add(scanner.nextLine());
            }

            Sender.Send(dataOutputStream, receivePath, save, bw, filesArr, saves, toRemove);

            dataInputStream.close();
            dataOutputStream.close();

            bw.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}