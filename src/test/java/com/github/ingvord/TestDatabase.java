package com.github.ingvord;

import com.github.ingvord.tango.DataBase;
import fr.esrf.Tango.DevState;
import fr.esrf.TangoApi.Database;
import fr.esrf.TangoApi.DeviceProxy;
import org.junit.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestDatabase {
    private final static ExecutorService exec = Executors.newSingleThreadExecutor();

    @BeforeClass
    public static void beforeClass() throws Exception{
        exec.submit(new DataBase.Launcher());
        Thread.sleep(1000);//TODO is there a better way?
    }

    @AfterClass
    public static void afterClass(){
        exec.shutdownNow();
    }

    @Test
    public void testConnect() throws Exception {
        Database db = new Database("localhost",Integer.toString(10000));
    }

    @Test
    public void testConnectProxy() throws Exception{
        DeviceProxy proxy = new DeviceProxy("tango://localhost:10000/sys/database/2");
        Assert.assertSame(DevState.ON, proxy.state());
    }


    //TODO more tests here

    public static void main(String[] args) {
        new DataBase.Launcher().run();
    }
}
