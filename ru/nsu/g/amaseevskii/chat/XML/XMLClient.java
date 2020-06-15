package ru.nsu.g.amaseevskii.chat.XML;

import ru.nsu.g.amaseevskii.chat.IClient;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class XMLClient implements IClient {

    private Socket mySocket;
    private String clientName;
    private XMLClientReadThread reader;
    private MyXMLWriter toServer;
    private MyXMLReader fromServer;
    private HashMap<String, String> otherClients;
    private String USID;
    private Timer timeoutTimer;
    private TimerListener tl;

    XMLClient() {
        otherClients = new HashMap<>();
    }

    public void connect(String ip, Integer port) {
        try {
            mySocket = new Socket(ip, port);
            toServer = new MyXMLWriter(mySocket.getOutputStream());
            fromServer = new MyXMLReader(mySocket.getInputStream());
            tl = new TimerListener();
            timeoutTimer = new Timer(1000, tl);
            timeoutTimer.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new ExceptionInInitializerError("Can`t connect!");
        }
    }

    @Override
    public void registration(String name, JTextArea users, JTextArea chat) throws InterruptedException {
        clientName = name;
        try {
            toServer.sendCommandMessage("login", clientName, "Client", "", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        reader = new XMLClientReadThread(users, chat, fromServer, otherClients);
        reader.start();
        synchronized (reader) {
            reader.wait();
            if (!reader.isSuccess()) {
                throw new ExceptionInInitializerError("Registration failed!");
            } else {
                USID = reader.getReader().getUSID();
            }
        }
        System.out.println("Registration successful!");
        getUsers();
    }

    @Override
    public void getUsers() {
        try {
            toServer.sendCommandMessage("list", "", "", USID, "");
            synchronized (reader) {
                reader.wait();
                if (!reader.isSuccess()) {
                    throw new ExceptionInInitializerError("Can't get user list!");
                }
                otherClients = reader.getOtherClients();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void writeMessage(String message) {
        synchronized (reader) {
            try {
                toServer.sendCommandMessage("message", "", "", USID, message);
                reader.wait();
            } catch (Exception e) {
                System.exit(0);
            }
            if (!reader.isSuccess()) {
                throw new ExceptionInInitializerError(reader.getReader().getMessage());
            }
        }
    }

    private class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent e) {
            try {
                toServer.sendEventMessage ("connection_check", "", "");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}

