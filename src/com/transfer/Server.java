package com.transfer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final String receivePath = "/home/matjaz/Programming/File-transfer/Received/";

    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(5000)){ //listening to port:5000
            Socket clientSocket = serverSocket.accept();

            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

            int number = dataInputStream.readInt(); //number of files to be received

            for(int i = 0; i < number; i++) {
                String filename = dataInputStream.readUTF(); //get file name
                receiveFile(filename, receivePath + dataInputStream.readUTF()); //get relative path and append it ot receive path
            }

            dataInputStream.close();
            dataOutputStream.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void receiveFile(String fileName, String path) throws Exception{
        System.out.println(path);
        System.out.println(path + fileName);
        int bytes = 0;
        File dir = new File(path);
        if (!dir.exists()){
            dir.mkdirs();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(path + fileName); //output to file

        long size = dataInputStream.readLong(); //get size of file
        byte[] buffer = new byte[4*1024];
        while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer,0,bytes);
            size -= bytes; //decrease size of file by received bytes
        }
        fileOutputStream.close();
    }
}