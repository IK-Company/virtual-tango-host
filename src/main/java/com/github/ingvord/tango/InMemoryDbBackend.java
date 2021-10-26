package com.github.ingvord.tango;

import org.tango.client.database.DeviceExportInfo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryDbBackend implements DbBackend{
    private final ConcurrentMap<String, DeviceInfo> db = new ConcurrentHashMap<>();

    @Override
    public void exportDevice(DeviceInfo info) {
        db.putIfAbsent(info.deviceName, info);
    }

    @Override
    public DeviceInfo importDevice(String deviceName) {
        return db.getOrDefault(deviceName, new DeviceInfo().withExported(false));
    }
}
