package com.github.ingvord.tango;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class InMemoryDbBackend implements DbBackend{
    public static final String ENTRIES_FILE = "entriesFile";
    public static final String TEST_DEVICES_PROPERTIES = "/test.devices.properties";
    private final ConcurrentMap<String, DeviceInfo> db;

    public InMemoryDbBackend() {
        db = loadEntries().orElse(new ConcurrentHashMap<>());
    }

    @Override
    public void exportDevice(DeviceInfo info) {
        db.put(info.deviceName, info);
    }

    @Override
    public DeviceInfo importDevice(String deviceName) {
        return db.getOrDefault(deviceName, new DeviceInfo().withExported(false));
    }

    @Override
    public List<String> getDeviceDomainList(String wildcard) {
        return db.keySet().stream()
                .map(key -> key.split("/")[0])
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getDeviceFamilyList(String wildcard) {
        //TODO checkArgument
        return db.keySet().stream()
                .filter(key -> key.split("/")[0].equalsIgnoreCase(wildcard.split("/")[0]))//TODO regex?
                .map(key -> key.split("/")[1])
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getDeviceMemberList(String wildcard) {
        //TODO checkArgument
        return db.keySet().stream()
                .filter(key ->  key.split("/")[0].equalsIgnoreCase(wildcard.split("/")[0]) &&
                                key.split("/")[1].equalsIgnoreCase(wildcard.split("/")[1]))//TODO regex?
                .map(key -> key.split("/")[2])
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getDeviceList(String executable, String tangoClass) {
        return db.entrySet().stream()
                .filter(entry -> entry.getKey().split("/")[0].equalsIgnoreCase(executable.split("/")[1]))
                .filter(entry -> entry.getValue().deviceClass.startsWith(tangoClass))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public void unExportDevice(String deviceName) {
        db.replace(deviceName, db.get(deviceName).withExported(false));
    }

    @Override
    public void unExportServer(String executable /*executanble/instance*/) {
        db.keySet().stream()
                .filter(key -> key.startsWith(executable.split("/")[1]))
                .forEach(this::unExportDevice);
    }

    private static Optional<ConcurrentMap<String, DeviceInfo>> loadEntries(){
        try{
            String entriesFile = System.getProperty(ENTRIES_FILE);
            Properties entries = new Properties();
            entries.load(entriesFile == null ?
                    new InputStreamReader(InMemoryDbBackend.class.getResourceAsStream(TEST_DEVICES_PROPERTIES)) :
                    new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(entriesFile)))));

            return Optional.of(entries.entrySet().stream()
                    .map(entry -> Map.entry(entry.getKey().toString(), new DeviceInfo().withDeviceClass(entry.getValue().toString())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (o1, o2) -> o1, ConcurrentHashMap::new)));
        } catch (IOException e){
            return Optional.empty();
        }
    }
}
