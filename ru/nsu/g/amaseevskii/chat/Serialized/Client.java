package ru.nsu.g.amaseevskii.chat.Serialized;

import ru.nsu.g.amaseevskii.chat.IClient;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Client implements IClient {
    private Socket mySocket;
    private String clientName;
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;
    private ClientReadThread reader;
    private ArrayList<String> otherClients;

    Client() {
        otherClients = new ArrayList<>();
    }

    public void connect (String ip, Integer port){
        try {
            mySocket = new Socket(ip, port);
            toServer = new ObjectOutputStream(mySocket.getOutputStream());
            fromServer = new ObjectInputStream(mySocket.getInputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new ExceptionInInitializerError("Can`t connect!");
        }
    }

    public Client(String ip, Integer port) {
        try {
            mySocket = new Socket(ip, port);
            toServer = new ObjectOutputStream(mySocket.getOutputStream());
            fromServer = new ObjectInputStream(mySocket.getInputStream());
            otherClients = new ArrayList<>();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new ExceptionInInitializerError("Can`t connect!");
        }
    }


    public void registration(String name, JTextArea users, JTextArea chat) throws IOException, InterruptedException {
        clientName = name;
        toServer.writeObject(new Message("Registration", clientName, clientName));
        reader = new ClientReadThread(users, chat, fromServer, otherClients);
        reader.start();
        synchronized (reader) {
            reader.wait();
            if (!reader.isSuccess()) {
                throw new ExceptionInInitializerError("Registration failed!");
            } else {
                System.out.println("Registration successful!");
            }
        }
        getUsers();
    }

    public void getUsers() {
        try {
            toServer.writeObject(new Message("Get user list", "",  clientName));
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

    public void writeMessage(String message) {
        synchronized (reader) {
            try {
                toServer.writeObject(new Message("Message", message, clientName));
                reader.wait();
            } catch (Exception e) {
                System.exit(0);
            }
            if (!reader.isSuccess()) {
                throw new ExceptionInInitializerError("Message not delivered!");
            }
        }
    }

}
