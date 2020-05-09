package com.client.chatwindow;

import com.client.util.DialogsUtil;
import com.model.messages.Message;
import com.model.messages.MessageType;
import com.model.messages.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

import static com.model.messages.MessageType.CONNECTED;

public class Listener implements Runnable {

    private static final String HAS_CONNECTED = "has connected";
    public static String username;
    private static String picture;
    private static ObjectOutputStream oos;
    public String hostname;
    public int port;
    ChatController controller;
    Logger logger = LoggerFactory.getLogger(Listener.class);
    private ObjectInputStream input;

    public Listener(String hostname, int port, String username, String picture, ChatController controller) {
        this.hostname = hostname;
        this.port = port;
        Listener.username = username;
        Listener.picture = picture;
        this.controller = controller;
    }

    /**
     * This method is used for sending a normal Message
     *
     * @param msg - The message which the user generates
     * @throws IOException when error happen in sending message
     */
    public static void send(String msg) throws IOException {
        Message createMessage = new Message();
        createMessage.setName(username);
        createMessage.setType(MessageType.USER);
        createMessage.setStatus(Status.AWAY);
        createMessage.setText(msg);
        createMessage.setPicture(picture);
        oos.writeObject(createMessage);
        oos.flush();
    }

    /* This method is used for sending a normal Message
     * @param msg - The message which the user generates
     */
    public static void sendStatusUpdate(Status status) throws IOException {
        Message createMessage = new Message();
        createMessage.setName(username);
        createMessage.setType(MessageType.STATUS);
        createMessage.setStatus(status);
        createMessage.setPicture(picture);
        oos.writeObject(createMessage);
        oos.flush();
    }

    /* This method is used to send a connecting message */
    public static void sendConnectMsg() throws IOException {
        Message createMessage = new Message();
        createMessage.setName(username);
        createMessage.setType(CONNECTED);
        createMessage.setText(HAS_CONNECTED);
        createMessage.setPicture(picture);
        oos.writeObject(createMessage);
    }

    public void run() {
        Socket socket = null;
        try {
            socket = new Socket(hostname, port);
            OutputStream outputStream = socket.getOutputStream();
            oos = new ObjectOutputStream(outputStream);
            InputStream is = socket.getInputStream();
            input = new ObjectInputStream(is);
        } catch (IOException e) {
            DialogsUtil.showErrorDialog("Could not connect to server");
            logger.error("Could not Connect");
        }
        logger.info("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());

        try {
            sendConnectMsg();
            logger.info("Sockets in and out ready!");
            while (socket.isConnected()) {
                Message message = (Message) input.readObject();

                if (message != null) {
                    logger.debug("Message received:" + message.getText() + " MessageType:" + message.getType() + "Name:" + message.getName());
                    switch (message.getType()) {
                        case USER:
                            controller.addToChat(message);
                            break;
                        case NOTIFICATION:
                            controller.newUserNotification(message);
                            break;
                        case SERVER:
                            controller.addAsServer(message);
                            break;
                        case CONNECTED:
                        case DISCONNECTED:
                        case STATUS:
                            controller.setUserList(message);
                            break;
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            controller.logoutScene();
        }
    }

}
