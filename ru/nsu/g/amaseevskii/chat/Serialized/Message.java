package ru.nsu.g.amaseevskii.chat.Serialized;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {
    private final String message;
    private String source;
    private final String date;

    public Message(String msg) {
        message = msg;
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        date = sdfDate.format(new Date());
    }

    public Message(String msg, String source) {
        this.source = source;
        message = msg;
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        date = sdfDate.format(new Date());
    }

}
