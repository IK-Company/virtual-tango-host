package com.github.ingvord;

import com.github.ingvord.tango.DataBase;
import fr.esrf.Tango.DevState;
import fr.esrf.TangoApi.Database;
import fr.esrf.TangoApi.DeviceProxy;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class TestDatabase {

    @Test
    @Ignore
    public void testConnect() throws Exception{
        Database db = new Database("localhost",Integer.toString(10000));
    }

    @Test
    @Ignore
    public void testConnectProxy() throws Exception{
        DeviceProxy proxy = new DeviceProxy("tango://localhost:10000/sys/database/2");
        Assert.assertSame(DevState.ON, proxy.state());
    }


    public static void main(String[] args) {
        new DataBase.Launcher().run();
    }
}
