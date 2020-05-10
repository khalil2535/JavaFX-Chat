package com.server;

import com.model.messages.Message;
import com.model.messages.MessageType;
import com.model.messages.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class Server {

    /* Setting up variables */
    private static final int PORT = 9001;
    private static final HashSet<ObjectOutputStream> writers = new HashSet<>();
    private static final ArrayList<User> users = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        logger.info("The chat server is running.");

        try (ServerSocket listener = new ServerSocket(PORT)) {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static User getUser(String name) {
        for (User user : users) {
            if (user.getName().equals(name)) return user;
        }
        return null;
    }


    private static class Handler extends Thread {
        private final Socket socket;
        private final Logger logger = LoggerFactory.getLogger(Handler.class);
        private User user;
        private ObjectInputStream ois;
        private OutputStream os;
        private ObjectOutputStream oos;
        private InputStream is;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            logger.info("Attempting to connect a user...");
            try {
                is = socket.getInputStream();
                ois = new ObjectInputStream(is);
                os = socket.getOutputStream();
                oos = new ObjectOutputStream(os);

                Message firstMessage = (Message) ois.readObject();
                checkDuplicateUsername(firstMessage);

                writers.add(oos);
                sendNotification(firstMessage);
                addToList();

                while (socket.isConnected()) {
                    Message inputMsg = (Message) ois.readObject();
                    if (inputMsg != null) {
                        logger.info(inputMsg.getType() + " - " + inputMsg.getName() + ": ");
                        switch (inputMsg.getType()) {
                            case USER:
                                sendToAll(inputMsg);
                                break;
                            case CONNECTED:
                                addToList();
                                break;
                            case STATUS:
                                changeStatus(inputMsg);
                                break;
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.error("Exception in run() method for user: " + (user != null ? user.getName() : "no user"),
                        e.getMessage());
            } finally {
                closeConnections();
            }
        }

        private void changeStatus(Message inputMsg) throws IOException {
            logger.debug(inputMsg.getName() + " has changed status to  " + inputMsg.getStatus());
            sendToAll(inputMsg);
        }

        private synchronized void checkDuplicateUsername(Message firstMessage) {
            logger.info(firstMessage.getName() + " is trying to connect");

            if (getUser(firstMessage.getName()) != null) {
                user = new User(firstMessage.getName(),
                        firstMessage.getPicture(),
                        firstMessage.getStatus());

                users.add(user);
                logger.info(user.getName() + " has been added to the list");
            } else {
                logger.error(firstMessage.getName() + " is already connected");
            }
        }

        /**
         * TODO separate send notification and public key
         */
        private void sendNotification(Message firstMessage) throws IOException {
            Message msg = new Message();
            // TODO use this to send RSA public key to all users
            msg.setText("has joined the chat.");
            msg.setType(MessageType.NOTIFICATION);
            msg.setName(firstMessage.getName());
            msg.setPicture(firstMessage.getPicture());
            sendToAll(msg);
        }


        private void removeFromList() throws IOException {
            logger.debug("removeFromList() method Enter");
            Message msg = new Message();
            msg.setText("has left the chat.");
            msg.setType(MessageType.DISCONNECTED);
            msg.setName("SERVER");
            msg.setUsers(users);
            sendToAll(msg);
            logger.debug("removeFromList() method Exit");
        }

        /*
         * For displaying that a user has joined the server
         */
        private void addToList() throws IOException {
            Message msg = new Message();
            // TODO put here the encryption key(s)
            msg.setText("Welcome, You have now joined the server! Enjoy chatting!");
            msg.setType(MessageType.CONNECTED);
            msg.setName("SERVER");
            sendToAll(msg);
        }

        /*
         * Creates and sends a Message type to the listeners.
         */
        private void sendToAll(Message msg) throws IOException {
            for (ObjectOutputStream writer : writers) {
                msg.setUsers(users);
                writer.writeObject(msg);
                writer.reset();
            }
        }

        /*
         * Once a user has been disconnected, we close the open connections and remove the writers
         */
        private synchronized void closeConnections() {
            logger.debug("closeConnections() method Enter");
            logger.info("HashMap names:" + users.size() + " writers:" + writers.size() + " users:" + users.size());
            if (user != null) {
                if (user.getName() != null) {
                    users.remove(user);
                    logger.info("User: " + user.getName() + " has been removed!");
                }
                if (user != null) {
                    users.remove(user);
                    logger.info("User object: " + user + " has been removed!");
                }
                if (oos != null) {
                    writers.remove(oos);
                    logger.info("Writer object: " + user + " has been removed!");
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    removeFromList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                logger.info("HashMap names:" + users.size() + " writers:" + writers.size() + " usersList size:" + users.size());
                logger.debug("closeConnections() method Exit");
            }
        }
    }
}
