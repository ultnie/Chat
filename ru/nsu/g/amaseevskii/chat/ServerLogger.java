package ru.nsu.g.amaseevskii.chat;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ServerLogger {
    public static Logger serverLogger = Logger.getLogger(ServerLogger.class.getName());
    static FileHandler fh;

    public static void setLogger() {
        try {
            fh = new FileHandler("ServerLog.txt");
            serverLogger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
