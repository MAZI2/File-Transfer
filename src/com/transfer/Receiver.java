package com.transfer;

import java.io.*;

public class Receiver {

    public static void Receive(DataInputStream dataInputStream, String directory, BufferedWriter bw) throws Exception {
        int delete = dataInputStream.readInt();
        int number = dataInputStream.readInt(); //number of files to be received

        for(int i = 0; i < delete; i++) {
            File toDelete= new File(directory + dataInputStream.readUTF());
            System.out.println("Deleting: " + toDelete.getName());
            toDelete.delete();
        }

        for(int i = 0; i < number; i++) {
            String filename = dataInputStream.readUTF(); //get file name
            String relativePath = dataInputStream.readUTF();
            receiveFile(dataInputStream, filename, directory + relativePath);
            bw.write(directory + relativePath + filename);
            bw.newLine();
            bw.flush();
        }
    }

    private static void receiveFile(DataInputStream dataInputStream, String fileName, String path) throws Exception {
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
