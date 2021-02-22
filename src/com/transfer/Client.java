package com.transfer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
    private static final String sendPath= "/home/matjaz/Programming/File-transfer/Data/";

    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;
    private static ArrayList<File> filesArr = new ArrayList<File>();
    private static ArrayList<String> saves = new ArrayList<String>();

    public static void main(String[] args) {
        try(Socket socket = new Socket("localhost",5000)) {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            dataOutputStream.flush();

            File save = new File("ClientSave");
            Scanner scanner = new Scanner(save);
            while(scanner.hasNextLine()) {
                saves.add(scanner.nextLine());
                System.out.println(saves.size());
            }

            listFiles(sendPath, scanner); //scan directory and add non directory files to ArrayList files
            dataOutputStream.writeInt(filesArr.size()); //send number of sent files

            for(int i = 0 ; i < filesArr.size();i++){
                dataOutputStream.writeUTF(filesArr.get(i).getName()); //send file name
                dataOutputStream.writeUTF(filesArr.get(i).getAbsolutePath().replace(sendPath, "").replace(filesArr.get(i).getName(), "")); //send relative path
                dataOutputStream.flush();

                sendFile(filesArr.get(i).getAbsolutePath());
            }

            dataInputStream.close();
            dataOutputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void listFiles(String startDir, Scanner scanner) {
        File dir = new File(startDir);

        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                System.out.println(name);
                System.out.println(!saves.stream().anyMatch(name::contains));
                return !saves.stream().anyMatch(name::contains);
            }
        };

        File[] files = dir.listFiles(filter);

        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    listFiles(file.getAbsolutePath(), scanner);
                } else {
                    filesArr.add(file);
                }
            }
        }
    }

    private static void sendFile(String path) throws Exception{
        int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);

        dataOutputStream.writeLong(file.length()); //send file size

        byte[] buffer = new byte[4*1024]; //buffer for file pieces
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
    }

}