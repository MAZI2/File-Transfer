package com.transfer;

import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Server {

    public static void main (String [] args ) throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(15123);
        Socket socket = serverSocket.accept();
        System.out.println("Accepted connection : " + socket);

        File fout = new File("cache.txt");
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        Scanner lines = new Scanner(fout);

        listFiles("/home/matjaz/Programming/File-transfer/Data", socket, bw);
        bw.close();
        sendFiles("/home/matjaz/Programming/File-transfer/cache.txt", socket);
        while(lines.hasNextLine()) {
            sendFiles(lines.nextLine(), socket);
        }
        socket.close();
    }


    private static void listFiles(String startDir, Socket socket, BufferedWriter bw) throws IOException {
        File dir = new File(startDir);
        File[] files = dir.listFiles();

        if(files != null && files.length > 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    listFiles(file.getAbsolutePath(), socket, bw);
                } else {
                    bw.write(file.getAbsolutePath());
                    bw.newLine();
                }
            }
        }
    }
    private static void sendFiles(String filePath, Socket socket) throws IOException {
        File transferFile = new File (filePath);
        byte [] bytearray  = new byte [(int)transferFile.length()];
        FileInputStream fin = new FileInputStream(transferFile);
        BufferedInputStream bin = new BufferedInputStream(fin);
        bin.read(bytearray,0,bytearray.length);
        OutputStream os = socket.getOutputStream();
        System.out.println("Sending Files...");
        os.write(bytearray,0,bytearray.length);
        os.flush();

        System.out.println("File transfer complete");
    }
}
