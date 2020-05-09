package com.client;

import com.client.chatwindow.Listener;
import com.model.messages.Message;
import com.model.messages.MessageType;
import com.model.messages.Status;
import com.model.messages.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

import static com.model.messages.MessageType.CONNECTED;

public class Client {
    private final User user;
    private final Logger logger = LoggerFactory.getLogger(Listener.class);
    private final String hostname;
    private final int port;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    Client(User user, int port, String hostname) {
        this.hostname = hostname;
        this.port = port;
        this.user = user;
    }

    Client(String username, String picture, Status status, int port, String hostname) {
        this.hostname = hostname;
        this.port = port;
        this.user = new User(username, picture, status);
    }

    /* This method is used to send a connecting message */
    public void connect() throws IOException {
        Socket socket = new Socket(hostname, port);
        OutputStream outputStream = socket.getOutputStream();
        oos = new ObjectOutputStream(outputStream);
        InputStream is = socket.getInputStream();
        ois = new ObjectInputStream(is);
        logger.info("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
        send(CONNECTED, "has connected");
    }

    private void send(MessageType type, String message) throws IOException {
        Message createMessage = new Message();
        createMessage.setName(user.getName());
        createMessage.setType(type);
        createMessage.setText(message);
        createMessage.setStatus(user.getStatus());
        createMessage.setPicture(user.getPicture());
        oos.writeObject(createMessage);
        oos.flush();
    }

    /**
     * This method is used for sending a normal Message
     *
     * @param status - The message which the user generates
     */
    public void sendStatusUpdate(Status status) throws IOException {
        Status oldStatus = user.getStatus();
        user.setStatus(status);
        try {
            send(MessageType.STATUS, "");
        } catch (Exception e) {
            user.setStatus(oldStatus);
            throw e;
        }
    }

    /**
     * This method is used for sending a normal Message
     *
     * @param msg - The message which the user generates
     * @throws IOException when error happen in sending message
     */
    public void sendMessage(String msg) throws IOException {
        send(MessageType.USER, msg);
    }

    public Message getNewMessage() throws IOException, ClassNotFoundException {
        while (true) {
            Message message = (Message) ois.readObject();
            if (message != null) {
                logger.info("Message received:" + message.getText() + " MessageType:" + message.getType());
                return message;
            }
        }
    }

}
