package ru.nsu.g.amaseevskii.chat.Serialized;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class ServerReadThread extends Thread{
    private final ArrayDeque<Message> lastMessages;
    private final ArrayList<ObjectOutputStream> objectOutputStreams;

    ServerReadThread(ArrayDeque<Message> lastMessages, ArrayList<ObjectOutputStream> oos) {
        this.lastMessages = lastMessages;
        this.objectOutputStreams = oos;
    }

    @Override
    public void run() {
        while(!isInterrupted()) {
            synchronized (lastMessages) {
                try {
                    lastMessages.wait();
                    for (ObjectOutputStream oos : objectOutputStreams) {
                        try {
                            oos.writeObject(lastMessages.getLast());
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                            objectOutputStreams.remove(oos);
                        }
                    }
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
