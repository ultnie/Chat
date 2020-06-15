package ru.nsu.g.amaseevskii.chat.XML;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

public class XMLServerWriteThread extends Thread {

    private String name;
    private ArrayList<MyXMLWriter> writers;
    private HashMap<String, String> clients;
    private MyXMLReader fromClient;
    private MyXMLWriter toClient;
    private ArrayDeque<XMLMessage> lastMessages;
    private Integer maxLastMessages;

    XMLServerWriteThread(Socket socket, ArrayDeque<XMLMessage> lastMessages, MyXMLWriter toClient,
                         ArrayList<MyXMLWriter> writers, HashMap<String, String> clients) throws IOException {
        this.name = "New User";
        this.writers = writers;
        this.lastMessages = lastMessages;
        this.toClient = toClient;
        this.clients = clients;
        fromClient = new MyXMLReader(socket.getInputStream());;
        maxLastMessages = 10;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                fromClient.readXMLMessage();
                switch (fromClient.getCommandOrEventName()) {
                    case "message":
                        System.out.println(name + ": " + fromClient.getMessage());
                        toClient.sendSuccessMessage("", "", null);
                        break;
                    case "login":
                        System.out.println(fromClient.getName() + " connected");
                        name = fromClient.getName();
                        clients.put(name, fromClient.getType());
                        toClient.sendSuccessMessage("login", Integer.toString(clients.size()), null);
                        for (XMLMessage msg : lastMessages) {
                            toClient.sendEventMessage(msg.getEventType(), msg.getMessage(), msg.getName());
                        }
                        break;
                    case "list":
                        System.out.println("Sending user list to " + fromClient.getUSID());
                        toClient.sendSuccessMessage("list", "", clients);
                        System.out.println("User list sent!");
                    case "connection_check":
                        toClient.sendSuccessMessage("connection_check", "", null);
                }
                if (fromClient.getCommandOrEventName().equals("message"))
                    synchronized (lastMessages) {
                        lastMessages.add(new XMLMessage("message", fromClient.getMessage(), name));
                        if (lastMessages.size() > maxLastMessages)
                            lastMessages.removeFirst();
                        lastMessages.notify();
                    }
                if (fromClient.getCommandOrEventName().equals("login"))
                    synchronized (lastMessages) {
                        lastMessages.add(new XMLMessage("userlogin", fromClient.getName()));
                        if (lastMessages.size() > maxLastMessages)
                            lastMessages.removeFirst();
                        lastMessages.notify();
                    }
            }catch (SocketTimeoutException e1) {
                System.out.println(name + " timed out");
                clients.remove(name);
                writers.remove(toClient);
                XMLMessage message = new XMLMessage("usertimedout", "", name);
                synchronized (lastMessages) {
                    lastMessages.add(message);
                    if (lastMessages.size() > maxLastMessages)
                        lastMessages.removeFirst();
                    lastMessages.notify();
                }
                currentThread().interrupt();
                break;
            } catch (ParserConfigurationException | IOException e) {
                System.out.println(name + " has left.");
                clients.remove(name);
                writers.remove(toClient);
                XMLMessage message = new XMLMessage("userlogout", "", name);
                synchronized (lastMessages) {
                    lastMessages.add(message);
                    if (lastMessages.size() > maxLastMessages)
                        lastMessages.removeFirst();
                    lastMessages.notify();
                }
                currentThread().interrupt();
                break;
            }
        }
    }
}