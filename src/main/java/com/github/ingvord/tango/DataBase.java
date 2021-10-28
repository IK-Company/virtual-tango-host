package com.github.ingvord.tango;

import fr.esrf.Tango.DevFailed;
import fr.esrf.Tango.DevVarLongStringArray;
import fr.esrf.TangoDs.Util;
import org.tango.DeviceState;
import org.tango.orb.ORBManager;
import org.tango.server.ServerManager;
import org.tango.server.annotation.*;
import org.tango.server.device.DeviceManager;
import org.tango.server.servant.DeviceImpl;
import org.tango.server.servant.ORBUtils2;
import org.tango.utils.DevFailedUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Stream;

@Device(transactionType = TransactionType.NONE)
public class DataBase {
    public static final String SYS_DATABASE_2 = "sys/database/2";
    private final Properties properties  = new Properties();

    private DbBackend backend;

    @DeviceManagement
    private DeviceManager manager;


    @Init
    @StateMachine(endState = DeviceState.ON)
    public void init() throws DevFailed, IOException {
        ORBUtils2.exportDeviceWithoutDatabase("database", manager.getDevice());
        this.properties.load(new InputStreamReader(DataBase.class.getResourceAsStream("/db.properties")));
        this.backend = DbBackendFactory.getInstance().createInstance();

        try {
            var orb = ORBManager.getOrb();

            this.backend.exportDevice(
                    new DeviceInfo()
                            .withDeviceName(SYS_DATABASE_2)
                            .withIor(orb.object_to_string(manager.getDevice()._this(orb)))
                            .withVersion(Integer.toString(DeviceImpl.SERVER_VERSION))
                            .withHostName(ServerManager.getInstance().getHostName())
                            .withServerName(ServerManager.getInstance().getServerName())
                            .withStartedOn(Long.toString(System.currentTimeMillis()))
                            .withDeviceClass(manager.getClassName())
                            .withPid(Integer.parseInt(ServerManager.getInstance().getPid()))
                            .withExported(true)
            );
        } catch (Exception e) {
            throw DevFailedUtils.newDevFailed(e);
        }
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
        return backend.getDeviceDomainList(wildcard).toArray(String[]::new);
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

    @Command(name = "DbGetDeviceList")
    public String[] getDeviceList(String[] argIn){
        //argIn[0] = TestServer/dev
        //argIn[1] = TestServer aka Device Class
        return new String[]{};
    }

    @Command(name = "DbGetDevicePropertyList")
    public String[] getDevicePropertyList(String deviceName){
        return new String[]{};
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

    @Command(name = "DbExportDevice")
    public void exportDevice(String[] argIn) throws DevFailed{
        try {
            this.backend.exportDevice(
                    new DeviceInfo()
                            .withDeviceName(argIn[0])
                            .withIor(argIn[1])
                            .withHostName(argIn[2])
                            .withVersion(argIn[3])
                            .withPid(Integer.parseInt(argIn[4]))
                            .withDeviceClass(argIn[5])
            );
        } catch (Exception e) {
            throw DevFailedUtils.newDevFailed(e);
        }
    }

    @Command(name = "DbImportDevice")
    public DevVarLongStringArray importDevice(String deviceName) throws DevFailed{
        try {
            return this.backend.importDevice(deviceName).toDevVarLongStringArray();
        } catch (Exception e) {
            throw DevFailedUtils.newDevFailed(e);
        }
    }

    @Command(name = "DbGetDeviceInfo")
    public DevVarLongStringArray getDeviceInfo(String deviceName) throws DevFailed {
        return importDevice(deviceName);
    }

    public static void main(String[] args) {
        new Launcher().run();
    }

    public void setManager(DeviceManager manager){
        this.manager = manager;
    }

    public static class Launcher implements Runnable {
        @Override
        public void run() {
            Util._UseDb = false;

            ServerManager.getInstance().addClass(DataBase.class.getCanonicalName(), DataBase.class);
            ServerManager.getInstance()
//                .setUseDb(false)
                    .start(new String[]{
                            "2",
                            "-nodb",
                            "-dlist",
                            SYS_DATABASE_2
                    }, "Databaseds");

        }
    }
}
