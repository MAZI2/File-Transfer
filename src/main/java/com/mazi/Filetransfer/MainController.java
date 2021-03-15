package com.mazi.Filetransfer;

import com.mazi.Filetransfer.filetransfer.Client;
import com.mazi.Filetransfer.filetransfer.Server;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;

import java.io.*;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

@RestController
public class MainController {
    private File console = new File("temp/console.txt");
    private static ArrayList<String> ipsList = new ArrayList<String>();

    @GetMapping("/Server")
    public String server() throws IOException {
        console.createNewFile();
        Thread thread1 = new Thread(() -> Server.sync());
        String read = readConsole("temp/console.txt");

        if(console.exists()) {
            PrintWriter writer = new PrintWriter(console);
            writer.print("");
            writer.close();
        }

        if(!thread1.isAlive()) {
            thread1.start();
        } else {
            thread1.run();
        }
        return read;
    }



    @GetMapping("/Client")
    public String client(Model model) throws FileNotFoundException {
        Thread thread2 = new Thread(() -> Client.synctwo());

        PrintStream o = new PrintStream(console);
        System.setOut(o);

        thread2.start();
        return "done";
    }

    @GetMapping("/update")
    private ArrayList<String> update() {
        return ipsList;
    }


    private static String readConsole(String filePath)
    {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return contentBuilder.toString();
    }

    @GetMapping("ips")
    private void findIps() {
        Thread ips = new Thread(() -> {
            int timeout=1000;
            for (int i=1;i<255;i++){
                String host="192.168.0." + i;
                try {
                    if (InetAddress.getByName(host).isReachable(timeout)){
                        ipsList.add(host + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        ips.start();
    }
}

