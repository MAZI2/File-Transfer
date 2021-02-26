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

            checkForDeleted(save);

            FileOutputStream fos = new FileOutputStream(save, true);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            int delete = dataInputStream.readInt();
            int number = dataInputStream.readInt(); //number of files to be received

            for(int i = 0; i < delete; i++) {
                File toDelete= new File(receivePath + dataInputStream.readUTF());
                System.out.println("Deleting: " + toDelete.getName());
                toDelete.delete();
            }

            for(int i = 0; i < number; i++) {
                String filename = dataInputStream.readUTF(); //get file name
                String relativePath = dataInputStream.readUTF();
                receiveFile(filename, receivePath + relativePath);
                bw.write(receivePath + relativePath + filename);
                bw.newLine();
                bw.flush();
            }

            //SENDER PART
            Scanner scanner = new Scanner(save);
            while (scanner.hasNextLine()) {
                saves.add(scanner.nextLine());
            }

            dataOutputStream.writeInt(toRemove.size());

            listFiles(receivePath, bw); //scan directory and add non directory files to ArrayList files
            dataOutputStream.writeInt(filesArr.size()); //send number of sent files

            for (int i = 0 ; i < toRemove.size();i++){
                dataOutputStream.writeUTF(toRemove.get(i).replace(receivePath, ""));
                dataOutputStream.flush();
            }

            for (int i = 0 ; i < filesArr.size();i++){
                dataOutputStream.writeUTF(filesArr.get(i).getName()); //send file name
                dataOutputStream.writeUTF(filesArr.get(i).getAbsolutePath().replace(receivePath, "").replace(filesArr.get(i).getName(), "")); //send relative path
                dataOutputStream.flush();

                File check = new File(filesArr.get(i).getAbsolutePath());
                if (!check.isDirectory()) {
                    sendFile(filesArr.get(i).getAbsolutePath());
                }
            }

            dataInputStream.close();
            dataOutputStream.close();

            bw.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void checkForDeleted(File save) throws IOException {
        Scanner dirs = new Scanner(save);
        File tempFile = new File("TempFile2.txt");
        BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));

        while (dirs.hasNextLine()) {
            String line = dirs.nextLine();
            Path path = Paths.get(line);

            if (Files.exists(path)) {
                bw.write(line + System.getProperty("line.separator"));
            } else {
                toRemove.add(line);
            }
        }
        bw.close();
        dirs.close();
        tempFile.renameTo(save);
    }

    public static void listFiles(String startDir, BufferedWriter bw) throws IOException {
        File dir = new File(startDir);

        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File dir) {
                return !saves.stream().anyMatch(dir.getAbsolutePath()::contains);
            }
        };

        File[] files = dir.listFiles(filter);

        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    listFiles(file.getAbsolutePath(), bw);
                } else {
                    filesArr.add(file);
                }
                bw.write(file.getAbsolutePath());
                bw.newLine();
            }
        }
    }

    private static void receiveFile(String fileName, String path) throws Exception{
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

    private static void sendFile(String path) throws Exception{
        int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        System.out.println("Sending: " + path);

        dataOutputStream.writeLong(file.length()); //send file size

        byte[] buffer = new byte[4*1024]; //buffer for file pieces
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
    }
}