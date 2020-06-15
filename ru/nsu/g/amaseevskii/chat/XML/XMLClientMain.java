package ru.nsu.g.amaseevskii.chat.XML;

import ru.nsu.g.amaseevskii.chat.RegWindow;

public class XMLClientMain {

    public static void main(String[] args) {
        XMLClient client = new XMLClient();
        try {
            RegWindow rw = new RegWindow(client);
            rw.startReg();
        } catch (ExceptionInInitializerError e) {
            System.out.println("Can`t connect to this server!");
        }
    }
}
