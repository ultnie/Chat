package ru.nsu.g.amaseevskii.chat.XML;

import ru.nsu.g.amaseevskii.chat.ServerWindow;

public class XMLServerMain {
    public static void main(String[] args) {
        XMLServer server = new XMLServer();
        ServerWindow sw = new ServerWindow(server);
        sw.StartServer();
    }
}