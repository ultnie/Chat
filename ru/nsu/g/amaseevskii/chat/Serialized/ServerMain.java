package ru.nsu.g.amaseevskii.chat.Serialized;

import ru.nsu.g.amaseevskii.chat.ServerWindow;

public class ServerMain {

    public static void main(String[] args) {

        ServerWindow sw = new ServerWindow();
        sw.StartServer();
    }
}
