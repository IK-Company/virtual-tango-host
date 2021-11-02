package com.github.ingvord.tango;

import org.tango.client.database.DeviceExportInfo;

import java.util.Collection;
import java.util.List;

public interface DbBackend {
    public void exportDevice(DeviceInfo info) throws Exception;
    public DeviceInfo importDevice(String deviceName) throws Exception;

    List<String> getDeviceDomainList(String wildcard) throws Exception;

    List<String> getDeviceFamilyList(String wildcard) throws Exception;

    List<String> getDeviceMemberList(String wildcard) throws Exception;

    List<String> getDeviceList(String executable, String tangoClass) throws Exception;

    void unExportServer(String executable) throws Exception;

    void unExportDevice(String deviceName) throws Exception;
}
