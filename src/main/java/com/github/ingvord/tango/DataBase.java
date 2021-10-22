package com.github.ingvord.tango;

import fr.esrf.TangoDs.Util;
import org.tango.DeviceState;
import org.tango.server.ServerManager;
import org.tango.server.annotation.Device;
import org.tango.server.annotation.Init;
import org.tango.server.annotation.StateMachine;
import org.tango.server.annotation.TransactionType;

import java.util.Arrays;
import java.util.stream.Stream;

@Device(transactionType = TransactionType.NONE)
public class DataBase {


    @Init
    @StateMachine(endState = DeviceState.ON)
    public void init(){

    }

    public static void main(String[] args) {
        Util._UseDb = false;

        ServerManager.getInstance().addClass(DataBase.class.getCanonicalName(), DataBase.class);
        ServerManager.getInstance()
//                .setUseDb(false)
                .start(new String[]{
                        "2",
                        "-nodb",
                        "-dlist",
                        "sys/database/2"
                }, "Databaseds");
    }
}
