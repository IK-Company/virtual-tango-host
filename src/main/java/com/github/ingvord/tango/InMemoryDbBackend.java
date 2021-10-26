package com.github.ingvord.tango;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

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

    @Override
    public List<String> getDeviceDomainList(String wildcard) {
        return db.keySet().stream()
                .map(key -> key.split("/")[0])
                //TODO filter
                .collect(Collectors.toList());
    }
}
