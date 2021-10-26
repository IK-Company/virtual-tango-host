package com.github.ingvord.tango;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoDs.Util;
import org.tango.DeviceState;
import org.tango.server.ServerManager;
import org.tango.server.annotation.*;
import org.tango.server.device.DeviceManager;
import org.tango.server.servant.ORBUtils2;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Stream;

@Device(transactionType = TransactionType.NONE)
public class DataBase {
    private final Properties properties  = new Properties();

    @DeviceManagement
    private DeviceManager manager;


    @Init
    @StateMachine(endState = DeviceState.ON)
    public void init() throws DevFailed, IOException {
        ORBUtils2.exportDeviceWithoutDatabase("database", manager.getDevice());
        this.properties.load(new InputStreamReader(DataBase.class.getResourceAsStream("/db.properties")));
    }

    @Command(name = "DbGetProperty")
    public String[] getProperty(String[] argIn){
        return Arrays.stream(argIn)
                .filter(this.properties::containsKey)
                .flatMap(key -> Stream.of(key, this.properties.get(key).toString()))
                .toArray(String[]::new);
    }

    @Command(name= "DbGetServerNameList")
    public String[] getServerNameList(String wildcard){
        return new String[]{
                "DataBaseds"//this
                //TODO broadcast
        };
    }

    @Command(name ="DbGetDeviceDomainList")
    public String[] getDeviceDomainList(String wildcard){
        return new String[]{
                "sys"
                //TODO broadcast
        };
    }

    @Command(name ="DbGetDeviceFamilyList")
    public String[] getDeviceFamilyList(String wildcard){
        return new String[]{
                "database"
                //TODO broadcast
        };
    }

    @Command(name ="DbGetDeviceMemberList")
    public String[] getDeviceMemberList(String wildcard){
        return new String[]{
                "2"
                //TODO broadcast
        };
    }

    @Command(name ="DbGetClassList")
    public String[] getClassList(String wildcard){
        return new String[]{
                this.getClass().getSimpleName()
                //TODO broadcast
        };
    }

    @Command(name ="DbGetDeviceAliasList")
    public String[] getDeviceAliasList(String wildcard){
        return new String[]{
                //TODO broadcast
        };
    }

    @Command(name ="DbGetAttributeAliasList")
    public String[] getAttributeAliasList(String wildcard){
        return new String[]{
                //TODO broadcast
        };
    }

    @Command(name ="DbGetObjectList")
    public String[] getObjectList(String wildcard){
        return new String[]{
                //TODO broadcast
        };
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

    public void setManager(DeviceManager manager){
        this.manager = manager;
    }
}
