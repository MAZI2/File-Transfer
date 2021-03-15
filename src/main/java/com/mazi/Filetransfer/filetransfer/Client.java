package com.mazi.Filetransfer.filetransfer;

import java.io.*;
import java.net.Socket;

public class Client {
    private static final String sendPath= "/home/matjaz/IdeaProjects/File transfer new workspace/File-transfer/dirs/Data/";

    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;

    public static void synctwo() {
        try (Socket socket = new Socket("localhost",5000)) {

            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            //SENDER
            File save = new File("temp/ClientSave");
            save.createNewFile();

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