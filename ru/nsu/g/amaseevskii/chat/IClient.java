package ru.nsu.g.amaseevskii.chat;

import javax.swing.*;

public interface IClient {
    void registration(String name, JTextArea users,JTextArea chat);
    void connect(String ip, Integer port);
    void getUsers();
    void writeMessage(String message);
}
