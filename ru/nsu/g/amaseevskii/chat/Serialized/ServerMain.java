package ru.nsu.g.amaseevskii.chat.Serialized;

import ru.nsu.g.amaseevskii.chat.ServerWindow;

public class ServerMain {

    public static void main(String[] args) {
        Server server = new Server();
        ServerWindow sw = new ServerWindow(server);
        sw.StartServer();
    }
}
