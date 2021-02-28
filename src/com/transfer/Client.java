package com.transfer;

import java.io.*;
import java.net.Socket;

public class Client {
    private static final String sendPath= "/home/matjaz/Programming/File-transfer/Data/";

    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost",5000)) {

            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            //SENDER
            File save = new File("ClientSave");

            Sender sender = new Sender();
            BufferedWriter bw = sender.Send(dataOutputStream, sendPath, save);

            //RECEIVER
            Receiver.Receive(dataInputStream, sendPath, bw);

            dataInputStream.close();
            dataOutputStream.close();

            bw.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}