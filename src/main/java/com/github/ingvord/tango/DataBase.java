package com.github.ingvord.tango;

import fr.esrf.Tango.DevFailed;
import fr.esrf.Tango.DevVarLongStringArray;
import fr.esrf.TangoDs.Util;
import org.omg.CORBA.ORB;
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

            exportAdminDevice(orb);

            exportThisDevice(orb);
        } catch (Exception e) {
            throw DevFailedUtils.newDevFailed(e);
        }
    }

    private void exportThisDevice(ORB orb) throws Exception {
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
                        .withExported(true));
    }

    private void exportAdminDevice(ORB orb) throws Exception {
        this.backend.exportDevice(
                new DeviceInfo()
                        .withDeviceName(manager.getAdminName())
                        .withIor(orb.object_to_string(ServerManager.getInstance().getTangoExporter().getDevice("DServer",manager.getAdminName())._this(orb)))
                        .withVersion(Integer.toString(DeviceImpl.SERVER_VERSION))
                        .withHostName(ServerManager.getInstance().getHostName())
                        .withServerName(ServerManager.getInstance().getServerName())
                        .withStartedOn(Long.toString(System.currentTimeMillis()))
                        .withDeviceClass("DServer")
                        .withPid(Integer.parseInt(ServerManager.getInstance().getPid()))
                        .withExported(true));
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
    public String[] getDeviceDomainList(String wildcard) throws DevFailed{
        try {
            return backend.getDeviceDomainList(wildcard).toArray(String[]::new);
        } catch (Exception e){
            throw DevFailedUtils.newDevFailed(e);
        }
    }

    @Command(name ="DbGetDeviceFamilyList")
    public String[] getDeviceFamilyList(String wildcard) throws DevFailed{
        try {
            return backend.getDeviceFamilyList(wildcard).toArray(String[]::new);
        } catch (Exception e) {
            throw DevFailedUtils.newDevFailed(e);
        }
    }

    @Command(name ="DbGetDeviceMemberList")
    public String[] getDeviceMemberList(String wildcard) throws DevFailed{
        try {
            return backend.getDeviceMemberList(wildcard).toArray(String[]::new);
        } catch (Exception e) {
            throw DevFailedUtils.newDevFailed(e);
        }
    }

    /**
     *	Command DbGetDeviceList related method
     *	Description: Get a list of devices for specified server and class.
     *
     *	@param argIn argin[0] : server name i.e. executable/instance e.g. TestServer/virtual
     *               argin[1] : class name
     *	@return The list of devices for specified server and class.
     */
    @Command(name = "DbGetDeviceList")
    public String[] getDeviceList(String[] argIn) throws DevFailed {
        try {
            return backend.getDeviceList(argIn[0], argIn[1]).toArray(String[]::new);
        } catch (Exception e) {
            throw DevFailedUtils.newDevFailed(e);
        }
    }

    @Command(name = "DbGetDevicePropertyList")
    public String[] getDevicePropertyList(String deviceName){
        return new String[]{};
    }


    @Command(name = "DbGetClassPropertyList")
    public String[] getClassPropertyList(String deviceName){
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
                            .withExported(true)
            );
        } catch (Exception e) {
            throw DevFailedUtils.newDevFailed(e);
        }
    }

    /**
     *	Command DbGetDeviceInfo related method
     *	Description: Returns info from DbImportDevice and started/stopped dates.
     *
     *	@param deviceName Device name
     *	@return Str[0] = Device name
     *           Str[1] = CORBA IOR
     *           Str[2] = Device version
     *           Str[3] = Device Server name
     *           Str[4] = Device Server process host name
     *           Str[5] = Started date (or ? if not set)
     *           Str[6] = Stopped date (or ? if not set)
     *           Str[7] = Device class
     *
     *           Lg[0] = Device exported flag
     *           Lg[1] = Device Server process PID (or -1 if not set)
     */
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

    @Command(name = "DbUnExportDevice")
    public void unExportDevice(String deviceName) throws DevFailed {
        try {
            backend.unExportDevice(deviceName);
        } catch (Exception e) {
            throw DevFailedUtils.newDevFailed(e);
        }
    }

    @Command(name = "DbUnExportServer")
    public void unExportServer(String executable) throws DevFailed {
        try {
            backend.unExportServer(executable);
        } catch (Exception e) {
            throw DevFailedUtils.newDevFailed(e);
        }
    }

    /**
     *	Command DbGetDeviceAttributeProperty2 related method
     *	Description: Retrieve device attribute properties. This command has the possibility to retrieve
     *               device attribute properties which are arrays. It is not possible with the old
     *               DbGetDeviceAttributeProperty command. Nevertheless, the old command has not been
     *               deleted for compatibility reason
     *
     *	@param argin Str[0] = Device name
     *               Str[1] = Attribute name
     *               Str[n] = Attribute name
     *	@return Str[0] = Device name
     *           Str[1] = Attribute property  number
     *           Str[2] = Attribute property 1 name
     *           Str[3] = Attribute property 1 value number (array case)
     *           Str[4] = Attribute property 1 value
     *           Str[n] = Attribute property 1 value (array case)
     *           Str[n + 1] = Attribute property 2 name
     *           Str[n + 2] = Attribute property 2 value number (array case)
     *           Str[n + 3] = Attribute property 2 value
     *           Str[n + m] = Attribute property 2 value (array case)
     */
    @Command(name = "DbGetDeviceAttributeProperty2")
    public String[] getDeviceAttributeProperty2(String[] argin){
        return new String[]{};
    }

    public void setManager(DeviceManager manager){
        this.manager = manager;
    }

    public static class Launcher implements Runnable {
        @Override
        public void run() {
            Util._UseDb = false;

            final Properties props = System.getProperties();
            props.put("OAPort", "10000");

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
