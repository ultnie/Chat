package ru.nsu.g.amaseevskii.chat.XML;

import ru.nsu.g.amaseevskii.chat.IServer;
import ru.nsu.g.amaseevskii.chat.ReadConfig;
import ru.nsu.g.amaseevskii.chat.ServerLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

import static ru.nsu.g.amaseevskii.chat.ServerLogger.serverLogger;

public class XMLServer implements IServer {
    private ServerSocket mainSocket;
    private int port = 8080;
    private HashMap<String, String> clients;
    private HashMap<String, Integer> config;
    private ArrayList<MyXMLWriter> writers;
    private ArrayDeque<XMLMessage> lastMessages;
    private int log = 0;
    private static final boolean SERVER_IS_ONLINE = true;

    public void launchServer() {
        try {
            config = ReadConfig.readConfig();
            if (config.containsKey("Port"))
                port=config.get("Port");
            if (config.containsKey("MakeLog"))
                log=config.get("MakeLog");
            if (log == 1)
                ServerLogger.setLogger();
            mainSocket = new ServerSocket(port, 5000);
            clients = new HashMap<>();
            writers = new ArrayList<>();
            lastMessages = new ArrayDeque<>();
            XMLServerReadThread reader = new XMLServerReadThread(lastMessages, writers);
            reader.start();
            listen();
        } catch (IOException e) {
            if (log == 1)
                serverLogger.info("Server died");
            System.out.println("Server died");
        }
    }

    public void listen() throws IOException {
        if (log == 1)
            serverLogger.info("Server is online");
        System.out.println("Server is online");
        MyXMLWriter writer;
        while (SERVER_IS_ONLINE) {
            Socket socket = mainSocket.accept();
            writer = new MyXMLWriter(socket.getOutputStream());
            socket.setSoTimeout(5000);
            writers.add(writer);
            if (log == 1)
                serverLogger.info("New client: " + socket.getRemoteSocketAddress().toString());
            System.out.println("New client: " + socket.getRemoteSocketAddress().toString());
            XMLServerWriteThread client = new XMLServerWriteThread(socket, lastMessages, writer, writers, clients);
            client.start();
        }
    }

}
