package ru.nsu.g.amaseevskii.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class ChatWindow {
    private final IClient client;
    private boolean xml;

    ChatWindow(IClient client) {
        this.client = client;
        xml = false;
        openChat();
    }

    private JFrame frame;
    private JTextArea chat;
    private JTextArea users;

    public void openChat() {
        frame = new JFrame();
        GridBagLayout grid = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        frame.setLayout(grid);
        frame.setTitle("Chat");

        //chat area
        chat = new JTextArea(25, 60);
        chat.setEditable(false);
        JScrollPane scrollableTextArea = new JScrollPane(chat);
        scrollableTextArea.setAutoscrolls(true);
        chat.setAutoscrolls(true);
        chat.setLineWrap(true);

        scrollableTextArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollableTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(scrollableTextArea, gbc);

        //message area
        gbc.anchor = GridBagConstraints.CENTER;
        JTextArea messageWindow = new JTextArea(5, 60);
        JScrollPane writeHere = new JScrollPane(messageWindow);
        writeHere.setAutoscrolls(false);
        writeHere.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        writeHere.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        frame.add(writeHere, gbc);
        messageWindow.setLineWrap(true);

        //user list
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 3;
        gbc.gridy = 0;
        this.users = new JTextArea(25, 20);
        JScrollPane cp = new JScrollPane(users);
        cp.setAutoscrolls(false);
        cp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        cp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        users.setLineWrap(true);
        frame.add(cp, gbc);
        users.setEditable(false);

        //button
        gbc.ipady = 30;
        gbc.ipadx = 60;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        JButton sendMessage = new JButton("Send");

        messageWindow.addKeyListener(
                new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                    }
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER){
                            if(!messageWindow.getText().trim().equals("")) {
                                client.writeMessage(messageWindow.getText());
                                messageWindow.setText("");
                            }
                            e.consume();
                        }
                    }
                    @Override
                    public void keyReleased(KeyEvent e) {
                    }
                }
        );

        sendMessage.addActionListener(e -> {
            if(!messageWindow.getText().trim().equals("")) {
                client.writeMessage(messageWindow.getText().trim());
                messageWindow.setText("");
            }
        });
        frame.add(sendMessage, gbc);
        frame.setSize(1000, 800);
        frame.setPreferredSize(frame.getSize());
        frame.setResizable(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void launchChat(){
        frame.setVisible(true);
    }

    public JTextArea getChat() {
        return chat;
    }

    public JTextArea getUsers() {
        return users;
    }

}
