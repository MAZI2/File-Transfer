package com.transfer;

import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Client {

    public static void main (String [] args ) throws IOException, ClassNotFoundException {
        int filesize=1022386;
        int bytesRead;
        int currentTot = 0;
        Socket socket = new Socket("192.168.0.105",15123);


        InputStream in = socket.getInputStream();

        byte [] bytearray  = new byte [filesize];

        FileOutputStream fos = new FileOutputStream("/home/matjaz/Programming/File-transfer/Received/FileCache");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bytesRead = in.read(bytearray,0,bytearray.length);
        currentTot = bytesRead;

        do {
            bytesRead =
                    in.read(bytearray, currentTot, (bytearray.length-currentTot));
            if(bytesRead >= 0) currentTot += bytesRead;
        } while(bytesRead > -1);

        bos.write(bytearray, 0 , currentTot);
        bos.flush();
        bos.close();
        socket.close();
    }
}
