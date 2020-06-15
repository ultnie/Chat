package ru.nsu.g.amaseevskii.chat.Serialized;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayDeque;
import java.util.ArrayList;

import static ru.nsu.g.amaseevskii.chat.ServerLogger.serverLogger;

public class ServerWriteThread extends Thread {
    String name;
    private ArrayList<ObjectOutputStream> objectOutputStreams;
    private ArrayList<String> clients;
    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;
    private ArrayDeque<Message> lastMessages;
    private Integer maxLastMessages;
    private Timer timeoutTimer;
    private TimerListener tl;
    protected int log;

    ServerWriteThread(Socket socket, ArrayDeque<Message> lastMessages, ObjectOutputStream toClient,
                      ArrayList<ObjectOutputStream> oos, ArrayList<String> clients, int log) throws IOException {
        this.name = "New User";
        this.objectOutputStreams = oos;
        this.lastMessages = lastMessages;
        this.toClient = toClient;
        this.clients = clients;
        this.log = log;
        tl = new TimerListener();
        timeoutTimer = new Timer(1000, tl);
        fromClient = new ObjectInputStream(socket.getInputStream());
        maxLastMessages = 10;
        timeoutTimer.start();
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
                    case "Connection check" -> {}
                }
                if (message.getType().equals("Message") || message.getType().equals("Registration") || message.getType().equals("Disconnect by timeout"))
                    synchronized (lastMessages) {
                        lastMessages.add(message);
                        if (lastMessages.size() > maxLastMessages)
                            lastMessages.removeFirst();
                        lastMessages.notify();
                    }
                
            } catch (SocketTimeoutException e1) {
                if (log == 1)
                    serverLogger.info(name + " timed out");
                System.out.println(name + " timed out");
                message = new Message("Disconnect by timeout", "", name);

                clients.remove(name);
                objectOutputStreams.remove(toClient);
                timeoutTimer.stop();

                try {
                    fromClient.close();
                    toClient.close();
                    synchronized (lastMessages) {
                        lastMessages.add(message);
                        if (lastMessages.size() > maxLastMessages)
                            lastMessages.removeFirst();
                        lastMessages.notify();
                    }
                } catch (IOException e2) {
                    System.out.println(e2.getMessage());
                }
                currentThread().interrupt();
                break;
            }
            catch (ClassNotFoundException | IOException e) {
                if (log == 1)
                    serverLogger.info(name + " has left");
                System.out.println(name + " has left");
                message = new Message("Disconnect", "", name);

                clients.remove(name);
                objectOutputStreams.remove(toClient);
                timeoutTimer.stop();

                try {
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

    private class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent e) {
            try {
                toClient.writeObject(new Message("Connection check", ""));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
