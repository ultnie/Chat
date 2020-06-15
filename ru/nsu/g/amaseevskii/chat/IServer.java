package ru.nsu.g.amaseevskii.chat;

import java.io.IOException;

public interface IServer {
    void listen() throws IOException;
    void launchServer();
}
