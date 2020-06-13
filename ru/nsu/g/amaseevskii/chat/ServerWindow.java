package ru.nsu.g.amaseevskii.chat;

import ru.nsu.g.amaseevskii.chat.Serialized.Server;

import javax.swing.*;
import java.net.Inet4Address;
import java.net.UnknownHostException;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class ServerWindow {
    private Server server;

    public ServerWindow(Server server) throws UnknownHostException {
        this.server = server;
    }

    JFrame frame;
    JTextArea info;

    public void StartServer() throws UnknownHostException {
        frame = new JFrame();
        frame.setSize(500,200);
        info = new JTextArea();
        info.append("The idea is that closing that window should close the server\n");
        info.append("IP: " + Inet4Address.getLocalHost());
        info.setEditable(false);
        frame.setTitle("Server");
        frame.add(info);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
