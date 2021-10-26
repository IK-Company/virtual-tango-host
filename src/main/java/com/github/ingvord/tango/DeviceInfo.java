package com.github.ingvord.tango;

import fr.esrf.Tango.DevVarLongStringArray;

public class DeviceInfo {
    public static final String DEFAULT_VALUE= "";

    public String deviceName = DEFAULT_VALUE;
    public String ior = DEFAULT_VALUE;
    public String version = DEFAULT_VALUE;
    public String serverName = DEFAULT_VALUE;
    public String hostName = DEFAULT_VALUE;
    public String startedOn = DEFAULT_VALUE;
    public String stoppedOn = DEFAULT_VALUE;
    public String deviceClass = DEFAULT_VALUE;

    public int exported = 0;
    public int pid = -1;

    public DeviceInfo withDeviceName(String v){
        this.deviceName = v;
        return this;
    }

    public DeviceInfo withIor(String v){
        this.ior = v;
        return this;
    }

    public DeviceInfo withVersion(String v){
        this.version = v;
        return this;
    }

    public DeviceInfo withServerName(String v){
        this.serverName = v;
        return this;
    }

    public DeviceInfo withHostName(String v){
        this.hostName = v;
        return this;
    }

    public DeviceInfo withStartedOn(String v){
        this.startedOn = v;
        return this;
    }

    public DeviceInfo withStoppedOn(String v){
        this.stoppedOn = v;
        return this;
    }

    public DeviceInfo withDeviceClass(String v){
        this.deviceClass = v;
        return this;
    }

    public DeviceInfo withPid(int v){
        this.pid = v;
        return this;
    }

    public DeviceInfo withExported(boolean v){
        this.exported = v ? 1 : 0;
        return this;
    }

    public DevVarLongStringArray toDevVarLongStringArray(){
        return new DevVarLongStringArray(
                new int[]{
                        exported,
                        pid
                },
                new String[]{
                        deviceName,
                        ior,
                        version,
                        serverName,
                        hostName,
                        startedOn,
                        stoppedOn,
                        deviceClass
                }
        );
    }
}
