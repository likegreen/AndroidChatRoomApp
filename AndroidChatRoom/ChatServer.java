/**
 * Copyright (c) 2014 Shan Ji.
 *
 * @author Shan Ji
 * @version 5/4/2014
 **/

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.*;
import java.net.*;

public class ChatServer {

  
  private static ServerSocket serverSocket = null;
  
  private static Socket clientSocket = null;

  private static final clientThread[] threads = new clientThread[200];

  public static void main(String args[]) {

    int portNumber = 8090;

    try {
      serverSocket = new ServerSocket(portNumber);
	  System.out.println("Server is running");
	  System.out.println(serverSocket);
    } catch (IOException e) {
      System.out.println(e);
    }

    while (true) {
      try {
        clientSocket = serverSocket.accept();
        int i = 0;
        for (i = 0; i <200; i++) {
          if (threads[i] == null) {
            (threads[i] = new clientThread(clientSocket, threads)).start();
			System.out.println("Client number "+i+" is connected, availbe clients left: "+ (200-i));
            break;
          }
        }
        if (i == 200) {
          PrintStream os = new PrintStream(clientSocket.getOutputStream());
          os.println("Server too busy. Try later.");
          os.close();
          clientSocket.close();
        }
      } catch (IOException e) {
        System.out.println(e);
      }
    }
  }
}

class clientThread extends Thread {

  private BufferedReader is = null;
  private PrintStream os = null;
  private Socket clientSocket = null;
  private final clientThread[] threads;
  private int maxClientsCount;

  public clientThread(Socket clientSocket, clientThread[] threads) {
    this.clientSocket = clientSocket;
    this.threads = threads;
    maxClientsCount = threads.length;
  }

  public void run() {
    int maxClientsCount = this.maxClientsCount;
    clientThread[] threads = this.threads;

    try {

	  is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      os = new PrintStream(clientSocket.getOutputStream());
      os.println("Enter your name.");
      String nickname = is.readLine().trim();
      os.println("Welcome " + nickname+ " to Shan Ji's chat room.\nEnter \\quit to leave the room");
      for (int i = 0; i < maxClientsCount; i++) {
        if (threads[i] != null && threads[i] != this) {
          threads[i].os.println("*** A new user " + nickname
              + " entered the chat room !!! Welcome!!!***");
		  
        }
      }
      while (true) {
        String line = is.readLine();
        if (line.startsWith("quit")) {
          break;
        }
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null) {
            threads[i].os.println("<" + nickname + ">: " + line);
          }
        }
      }
      for (int i = 0; i < maxClientsCount; i++) {
        if (threads[i] != null && threads[i] != this) {
          threads[i].os.println("*** The user " + nickname+ " is leaving the chat room !!! ***");
        }
      }
      os.println("*** Bye " + nickname + " ***");

      for (int i = 0; i < maxClientsCount; i++) {
        if (threads[i] == this) {
          threads[i] = null;
		  //for the future use
        }
      }

      is.close();
      os.close();
      clientSocket.close();
    } catch (IOException e) {
    }
  }
}