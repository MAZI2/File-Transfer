package com.transfer;

import javax.sound.midi.SysexMessage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Sender {
    ArrayList<File> filesArr = new ArrayList<File>();
    ArrayList<String> saves = new ArrayList<String>();
    ArrayList<String> toRemove = new ArrayList<String>();

    public BufferedWriter Send(DataOutputStream dataOutputStream, String directory, File save) throws Exception {
        Scanner scanner = new Scanner(save);
        while (scanner.hasNextLine()) {
            saves.add(scanner.nextLine());
        }

        checkForDeleted(save);
        dataOutputStream.writeInt(toRemove.size());

        FileOutputStream fos = new FileOutputStream(save, true);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        listFiles(directory, bw); //scan directory and add non directory files to ArrayList files
        dataOutputStream.writeInt(filesArr.size()); //send number of sent files

        for (int i = 0; i < toRemove.size(); i++) {
            dataOutputStream.writeUTF(toRemove.get(i).replace(directory, ""));
            dataOutputStream.flush();
        }

        for (int i = filesArr.size() - 1; i >= 0; i--) {
            File check = new File(filesArr.get(i).getAbsolutePath());

            dataOutputStream.writeUTF(filesArr.get(i).getAbsolutePath().replace(directory, "")); //send relative path
            dataOutputStream.flush();

            if (!check.isDirectory()) {
                dataOutputStream.writeUTF("file");
                sendFile(dataOutputStream, filesArr.get(i).getAbsolutePath());
            } else {
                dataOutputStream.writeUTF("dir");
            }
        }
        return bw;
    }

    public void checkForDeleted(File save) throws IOException {
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

    public void listFiles(String startDir, BufferedWriter bw) throws IOException {
        File dir = new File(startDir);

        File[] files = dir.listFiles();

        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    listFiles(file.getAbsolutePath(), bw);
                }
                if (!saves.contains(file.getAbsolutePath())) {
                    filesArr.add(file);
                    bw.write(file.getAbsolutePath());
                    bw.newLine();
                }
            }
        }
    }

    private void sendFile(DataOutputStream dataOutputStream, String path) throws Exception{
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
