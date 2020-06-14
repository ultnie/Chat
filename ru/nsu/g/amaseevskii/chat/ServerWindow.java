package ru.nsu.g.amaseevskii.chat;

import ru.nsu.g.amaseevskii.chat.Serialized.Server;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.URL;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class ServerWindow {

    JFrame frame;
    JTextArea info;
    Server server;

    public void StartServer() {
        frame = new JFrame();
        frame.setSize(500,200);
        info = new JTextArea();
        info.append("The idea is that closing that window should close the server\n");
        try {
            info.append("Local IP: " + Inet4Address.getLocalHost().getHostAddress()+"\n");
            URL whatismyip = new URL("http://bot.whatismyipaddress.com");
            BufferedReader br = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            String extip = br.readLine().trim();
            info.append("External IP: " + extip);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        info.setEditable(false);
        frame.setTitle("Server");
        frame.add(info);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        server = new Server();
    }
}
