package ru.nsu.g.amaseevskii.chat.Serialized;

import ru.nsu.g.amaseevskii.chat.ReadConfig;
import ru.nsu.g.amaseevskii.chat.ServerLogger;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

import static ru.nsu.g.amaseevskii.chat.ServerLogger.serverLogger;

public class Server {
    private ServerSocket mainSocket;
    private ArrayList<String> clients;
    private ArrayList<ObjectOutputStream> streams;
    private ArrayDeque<Message> lastMessages;
    HashMap<String, Integer> config;
    private int port = 8080;
    private int log = 0;
    private static final boolean SERVER_IS_ONLINE = true;

    public Server() {
        config = ReadConfig.readConfig();
        if (config.containsKey("Port"))
            port=config.get("Port");
        if (config.containsKey("MakeLog"))
            log=config.get("MakeLog");
        if (log == 1)
            ServerLogger.setLogger();
        try {
            mainSocket = new ServerSocket(port, 5000);
            clients = new ArrayList<>();
            streams = new ArrayList<>();
            lastMessages = new ArrayDeque<>();
            ServerReadThread reader = new ServerReadThread(lastMessages, streams);
            reader.start();
            listen();
        } catch (IOException e) {
            if (log == 1)
                serverLogger.info("Server died");
            System.out.println("Server died");
        }
    }

    private void listen() throws IOException {
        if (log == 1)
            serverLogger.info("Server is online");
        System.out.println("Server is online");
        ObjectOutputStream oos;
        while (SERVER_IS_ONLINE) {
            Socket socket = mainSocket.accept();
            socket.setSoTimeout(5000);
            oos = new ObjectOutputStream(socket.getOutputStream());
            streams.add(oos);
            if (log == 1)
                serverLogger.info("New client: " + socket.getRemoteSocketAddress().toString());
            System.out.println("New client: " + socket.getRemoteSocketAddress().toString());
            ServerWriteThread client = new ServerWriteThread(socket, lastMessages, oos, streams, clients, log);
            client.start();
        }
    }
}
