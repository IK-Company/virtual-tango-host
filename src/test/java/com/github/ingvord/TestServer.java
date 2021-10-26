package com.github.ingvord;

import org.tango.server.ServerManager;
import org.tango.server.annotation.Device;

@Device
public class TestServer {

    public static void main(String[] args) {
        ServerManager.getInstance()
                .start(args, TestServer.class);
    }
}
