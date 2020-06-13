package ru.nsu.g.amaseevskii.chat.Serialized;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {
    private final String message;
    private String source;
    private final String date;
    private String type;

    public Message(String type, String msg) {
        this.type = type;
        message = msg;
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        date = sdfDate.format(new Date());
    }

    public Message(String type, String msg, String source) {
        this.type = type;
        this.source = source;
        message = msg;
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        date = sdfDate.format(new Date());
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return type + message;
    }

    public String getDate() {
        return date;
    }

    public String getSource() {
        return source;
    }
}
