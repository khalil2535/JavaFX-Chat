package com.client;


import com.model.messages.Status;

import java.io.IOException;


public class Test {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Client c = new Client("ahmad",
                "images/dominic.png",
                Status.ONLINE,
                9001,
                "127.0.0.1");
        c.connect();
        System.out.println(c.getNewMessage());
        c.sendMessage("Hello World");
        System.out.println(c.getNewMessage());
    }
}
