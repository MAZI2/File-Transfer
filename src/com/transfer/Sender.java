package com.transfer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Sender {

    public static void Send(DataOutputStream dataOutputStream, String directory, File save, BufferedWriter bw, ArrayList<File> filesArr, ArrayList<String> saves, ArrayList<String> toRemove) throws Exception {

        dataOutputStream.writeInt(toRemove.size());

        listFiles(directory, bw, filesArr, saves); //scan directory and add non directory files to ArrayList files
        dataOutputStream.writeInt(filesArr.size()); //send number of sent files

        for (int i = 0; i < toRemove.size(); i++) {
            dataOutputStream.writeUTF(toRemove.get(i).replace(directory, ""));
            dataOutputStream.flush();
        }

        for (int i = 0; i < filesArr.size(); i++) {
            dataOutputStream.writeUTF(filesArr.get(i).getName()); //send file name
            dataOutputStream.writeUTF(filesArr.get(i).getAbsolutePath().replace(directory, "").replace(filesArr.get(i).getName(), "")); //send relative path
            dataOutputStream.flush();

            File check = new File(filesArr.get(i).getAbsolutePath());
            if (!check.isDirectory()) {
                sendFile(dataOutputStream, filesArr.get(i).getAbsolutePath());
            }
        }
    }

    public static void checkForDeleted(File save, ArrayList<String> toRemove) throws IOException {
        Scanner dirs = new Scanner(save);
        File tempFile = new File("TempFile.txt");
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

    public static void listFiles(String startDir, BufferedWriter bw, ArrayList<File> filesArr, ArrayList<String> saves) throws IOException {
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
                    listFiles(file.getAbsolutePath(), bw, filesArr, saves);
                } else {
                    filesArr.add(file);
                }
                bw.write(file.getAbsolutePath());
                bw.newLine();
            }
        }
    }

    private static void sendFile(DataOutputStream dataOutputStream, String path) throws Exception{
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
