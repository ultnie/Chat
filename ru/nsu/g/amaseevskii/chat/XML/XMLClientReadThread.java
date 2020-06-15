package ru.nsu.g.amaseevskii.chat.XML;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class XMLClientReadThread extends Thread {
    private JTextArea chat;
    private JTextArea users;
    private boolean success;
    private HashMap<String, String> otherClients;
    private MyXMLReader fromServer;

    XMLClientReadThread(JTextArea users, JTextArea chat, MyXMLReader from, HashMap<String, String> oc) {
        this.chat = chat;
        this.users = users;
        otherClients = oc;
        fromServer = from;
        success = false;
    }

    @Override
    public void run() {
        while(!isInterrupted()) {
            try {
                fromServer.readXMLMessage();
                if (fromServer.getHeader().equals("event")) {
                    switch (fromServer.getCommandOrEventName()) {
                        case "userlogout":
                            otherClients.remove(fromServer.getName());
                            users.setText("");
                            for (Map.Entry<String, String> user : otherClients.entrySet())
                                users.append(user.getKey() + "\n");
                            chat.append(fromServer.getName() + " has left\n");
                            break;
                        case "usertimedout":
                            otherClients.remove(fromServer.getName());
                            users.setText("");
                            for (Map.Entry<String, String> user : otherClients.entrySet())
                                users.append(user.getKey() + "\n");
                            chat.append(fromServer.getName() + " timed out\n");
                            break;
                        case "message":
                            chat.append(fromServer.getName() + ": " + fromServer.getMessage() + "\n");
                            break;
                        case "userlogin":
                            chat.append(fromServer.getName() + " connected\n");
                            otherClients.put(fromServer.getName(), "Client");
                            users.setText("");
                            for (Map.Entry<String, String> user : otherClients.entrySet())
                                users.append(user.getKey() + "\n");
                            break;
                        default:
                            System.out.println("unknown xml " + fromServer.getCommandOrEventName());
                            break;
                    }
                } else if (fromServer.getHeader().equals("success")) {
                    synchronized (this) {
                        success = true;
                        if (fromServer.isUserList()) {
                            otherClients = fromServer.getUsers();
                        }
                        notify();

                    }
                } else if (fromServer.getHeader().equals("error")) {
                    System.out.println(fromServer.getMessage());
                    synchronized (this) {
                        success = false;
                        notify();
                    }
                } else {
                    System.out.println("Unknown header");
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
                chat.append("Server has stopped working. Write anything to quit.\n");
                interrupt();
            }
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public HashMap<String, String> getOtherClients() {
        return otherClients;
    }

    public MyXMLReader getReader() {
        return fromServer;
    }
}