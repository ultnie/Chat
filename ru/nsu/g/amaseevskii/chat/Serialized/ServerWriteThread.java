package ru.nsu.g.amaseevskii.chat.Serialized;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;

import static ru.nsu.g.amaseevskii.chat.ServerLogger.serverLogger;

public class ServerWriteThread extends Thread {
    private String name;
    private final ArrayList<ObjectOutputStream> objectOutputStreams;
    private final ArrayList<String> clients;
    private final ObjectInputStream fromClient;
    private final ObjectOutputStream toClient;
    private final ArrayDeque<Message> lastMessages;
    private final Integer maxLastMessages;
    private final Socket socket;
    private final int log;

    ServerWriteThread(Socket socket, ArrayDeque<Message> lastMessages, ObjectOutputStream toClient,
                      ArrayList<ObjectOutputStream> oos, ArrayList<String> clients, int log) throws IOException {
        this.name = "New User";
        this.objectOutputStreams = oos;
        this.lastMessages = lastMessages;
        this.toClient = toClient;
        this.clients = clients;
        this.log = log;
        this.socket = socket;
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
                    case "Message" -> {
                        if (log == 1)
                            serverLogger.info(message.getDate() + " " + message.getSource() + ": " + message.getMessage());
                        System.out.println(message.getDate() + " " + message.getSource() + ": " + message.getMessage());
                        toClient.writeObject(new Message("Success", "Message delivered"));
                    }
                    case "Registration" -> {
                        if (log == 1)
                            serverLogger.info(message.getSource() + " connected");
                        System.out.println(message.getSource() + " connected");
                        name = message.getSource();
                        toClient.writeObject(new Message("Success", "Registration successful"));
                        clients.add(name);
                        for (Message msg : lastMessages) {
                            toClient.writeObject(msg);
                        }
                    }
                    case "Get user list" -> {
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
                }
                if (message.getType().equals("Message") || message.getType().equals("Registration"))
                    synchronized (lastMessages) {
                        lastMessages.add(message);
                        if (lastMessages.size() > maxLastMessages)
                            lastMessages.removeFirst();
                        lastMessages.notify();
                    }
            } catch (Exception e) {
                try {
                    if (socket.getInetAddress().isReachable(5000)) {
                        if (log == 1)
                            serverLogger.info(name + " has left.");
                        System.out.println(name + " has left.");
                        message = new Message("Disconnect", "", name);
                    } else {
                        if (log == 1)
                            serverLogger.info(name + " disconnected by timeout");
                        System.out.println(name + " disconnected by timeout");
                        message = new Message("Disconnect by timeout", "", name);
                    }
                    clients.remove(name);
                    objectOutputStreams.remove(toClient);
                    fromClient.close();
                    toClient.close();
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
