package ru.nsu.g.amaseevskii.chat.Serialized;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;

import static ru.nsu.g.amaseevskii.chat.ServerLogger.serverLogger;

public class ServerWriteThread extends Thread{
    private String name;
    private ArrayList<ObjectOutputStream> objectOutputStreams;
    private ArrayList<String> clients;
    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;
    private ArrayDeque<Message> lastMessages;
    private Integer maxLastMessages;
    private int log;

    ServerWriteThread(Socket socket, ArrayDeque<Message> lastMessages, ObjectOutputStream toClient,
                      ArrayList<ObjectOutputStream> oos, ArrayList<String> clients, int log) throws IOException {
        this.name = "New User";
        this.objectOutputStreams = oos;
        this.lastMessages = lastMessages;
        this.toClient = toClient;
        this.clients = clients;
        this.log=log;
        fromClient = new ObjectInputStream(socket.getInputStream());
        maxLastMessages = 10;
    }

    @Override
    public void run() {
        Message message;
        while (!this.isInterrupted()) {
            try {
                message = (Message) fromClient.readObject();

                switch (message.getType()) {
                    case "Message":
                        if (log == 1)
                            serverLogger.info(message.getDate() + " " + message.getSource() + ": " + message.getMessage());
                        System.out.println(message.getDate() + " " + message.getSource() + ": " + message.getMessage());
                        toClient.writeObject(new Message("Success", "Message delivered"));
                        break;
                    case "Registration":
                        if (log == 1)
                            serverLogger.info(message.getSource() + " connected");
                        System.out.println(message.getSource() + " connected");
                        name = message.getSource();
                        toClient.writeObject(new Message("Success", "Registration successful"));
                        clients.add(name);
                        for (Message msg : lastMessages) {
                            toClient.writeObject(msg);
                        }
                        break;
                    case "Get user list":
                        if (log == 1)
                            serverLogger.info("Sending user list to " + message.getSource());
                        System.out.println("Sending user list to " + message.getSource());
                        StringBuilder userList = new StringBuilder();
                        for (String client : clients)
                            userList.append(client).append("\n");
                        userList.deleteCharAt(userList.length() - 1);
                        toClient.writeObject(new Message("User list", userList.toString()));
                        System.out.println("User list sent");
                }
                if (message.getType().equals("Message") || message.getType().equals("Registration"))
                    synchronized (lastMessages) {
                        lastMessages.add(message);
                        if (lastMessages.size() > maxLastMessages)
                            lastMessages.removeFirst();
                        lastMessages.notify();
                    }
            } catch (Exception e) {
                if (log == 1)
                    serverLogger.info(name + " has left.");
                System.out.println(name + " has left.");
                clients.remove(name);
                try {
                    objectOutputStreams.remove(toClient);
                    fromClient.close();
                    toClient.close();
                    message = new Message("Connection close", "", name);
                    synchronized (lastMessages) {
                        lastMessages.add(message);
                        if (lastMessages.size() > maxLastMessages)
                            lastMessages.removeFirst();
                        lastMessages.notify();
                    }
                } catch (IOException e1) {
                    System.out.println(e1.getMessage());
                }
                currentThread().interrupt();
                break;
            }
        }
    }
}
