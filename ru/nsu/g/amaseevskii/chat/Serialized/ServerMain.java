package ru.nsu.g.amaseevskii.chat.Serialized;

import ru.nsu.g.amaseevskii.chat.ServerWindow;

import java.net.UnknownHostException;

public class ServerMain {

    public static void main(String[] args) {

        Server server = new Server();
        try {
            ServerWindow sw = new ServerWindow(server);
            sw.StartServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
