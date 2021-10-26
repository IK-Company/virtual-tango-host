package com.github.ingvord.tango;

import org.tango.client.database.DeviceExportInfo;

import java.util.List;

public interface DbBackend {
    public void exportDevice(DeviceInfo info) throws Exception;
    public DeviceInfo importDevice(String deviceName) throws Exception;

    List<String> getDeviceDomainList(String wildcard);
}
