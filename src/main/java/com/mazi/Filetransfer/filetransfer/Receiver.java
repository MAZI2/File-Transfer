package com.mazi.Filetransfer.filetransfer;

import java.io.*;

public class Receiver {

    public static void Receive(DataInputStream dataInputStream, String directory, BufferedWriter bw) throws Exception {
        int delete = dataInputStream.readInt(); //number of files to be deleted
        int number = dataInputStream.readInt(); //number of files to be received

        for(int i = 0; i < delete; i++) {
            File toDelete= new File(directory + dataInputStream.readUTF()); //get directory to delete
            System.out.println("Deleting: " + toDelete.getName());
            toDelete.delete();
        }

        for(int i = 0; i < number; i++) {
            String filePath = dataInputStream.readUTF(); //get directory to receive or create

            if (dataInputStream.readUTF().equals("file")) { //if directory is a file, receive
                receiveFile(dataInputStream, directory + filePath);
            } else {
                File dir = new File(directory + filePath); //if directory is a folder, create
                if (!dir.exists()){
                    dir.mkdirs();
                }
            }
            bw.write(directory + filePath);
            bw.newLine();
            bw.flush();
        }
    }

    private static void receiveFile(DataInputStream dataInputStream, String path) throws Exception {
        int bytes = 0;

        FileOutputStream fileOutputStream = new FileOutputStream(path); //output to file

        long size = dataInputStream.readLong(); //get size of file
        byte[] buffer = new byte[4*1024];
        while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer,0,bytes);
            size -= bytes; //decrease size of file by received bytes
        }
        fileOutputStream.close();
    }
}
