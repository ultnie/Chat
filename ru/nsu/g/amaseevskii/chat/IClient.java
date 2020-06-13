package ru.nsu.g.amaseevskii.chat;

import javax.swing.*;
import java.io.IOException;

public interface IClient {
    void registration(String name, JTextArea users,JTextArea chat) throws IOException, InterruptedException;
    void connect(String ip, Integer port);
    void getUsers();
    void writeMessage(String message);
}
