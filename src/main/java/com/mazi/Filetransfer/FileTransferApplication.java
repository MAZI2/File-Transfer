package com.mazi.Filetransfer;

import com.mazi.Filetransfer.filetransfer.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.ui.Model;

import java.io.IOException;
import java.io.PrintWriter;

@SpringBootApplication
public class FileTransferApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileTransferApplication.class, args);
	}
}
