package ru.nsu.g.amaseevskii.chat.Serialized;

import ru.nsu.g.amaseevskii.chat.RegWindow;

public class ClientMain {
    public static void main(String[] args) {

        Client client = new Client();
        try {
            RegWindow rw = new RegWindow(client);
            rw.startReg();
        } catch (ExceptionInInitializerError e) {
            System.out.println("Can`t connect to this server!");
        }
    }
}