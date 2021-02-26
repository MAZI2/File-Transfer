package com.transfer;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    private static final String sendPath= "/home/matjaz/Programming/File-transfer/Data/";

    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;
    private static ArrayList<File> filesArr = new ArrayList<File>();
    private static ArrayList<String> saves = new ArrayList<String>();
    private static ArrayList<String> toRemove = new ArrayList<String>();

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost",5000)) {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            //SENDER PART

            File save = new File("ClientSave");
            Scanner scanner = new Scanner(save);
            while (scanner.hasNextLine()) {
                saves.add(scanner.nextLine());
            }

            Sender.checkForDeleted(save, toRemove);

            FileOutputStream fos = new FileOutputStream(save, true);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            Sender.Send(dataOutputStream, sendPath, save, bw, filesArr, saves, toRemove);

            // RECEIVER PART
            Receiver.Receive(dataInputStream, sendPath, bw);

            dataInputStream.close();
            dataOutputStream.close();

            bw.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}