package com.github.ingvord;

import fr.esrf.Tango.DevState;
import fr.esrf.TangoApi.Database;
import fr.esrf.TangoApi.DeviceProxy;
import org.junit.Assert;
import org.junit.Test;

public class TestDatabase {

    @Test
    public void testConnect() throws Exception{
        Database db = new Database("hzgxenvtest",Integer.toString(10000));
    }

    @Test
    public void testConnectProxy() throws Exception{
        DeviceProxy proxy = new DeviceProxy("tango://localhost:10000/sys/database/2#dbase=no");
        Assert.assertSame(DevState.ON, proxy.state());
    }

}
