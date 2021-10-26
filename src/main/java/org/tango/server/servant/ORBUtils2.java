package org.tango.server.servant;

import fr.esrf.Tango.DevFailed;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tango.orb.ORBManager;
import org.tango.utils.DevFailedUtils;

import java.util.Locale;

public class ORBUtils2 {
    private static final XLogger XLOGGER = XLoggerFactory.getXLogger(ORBUtils2.class);

    private ORBUtils2(){}

    public static void exportDeviceWithoutDatabase(final String name, final DeviceImpl dev) throws DevFailed {
        XLOGGER.entry(name);

        // For server without db usage.
        // it is necessary to create our own CORBA object id and to bind it into
        // the OOC Boot Manager for access through a stringified object
        // reference
        // constructed using the corbaloc style
        final String lowerCaseDevice = name.toLowerCase(Locale.ENGLISH);
        final byte[] oid = lowerCaseDevice.getBytes();
        final POA poa = ORBManager.getPoa();
        try {
            poa.activate_object_with_id(oid, dev);
        } catch (final ServantAlreadyActive e) {
            throw DevFailedUtils.newDevFailed(e);
        } catch (final ObjectAlreadyActive e) {
            throw DevFailedUtils.newDevFailed(e);
        } catch (final WrongPolicy e) {
            throw DevFailedUtils.newDevFailed(e);
        }
        // Get the object id and store it
        dev._this(ORBManager.getOrb());
        dev.setObjId(oid);

        registerDeviceForJacorb(lowerCaseDevice);

        XLOGGER.exit();
    }

    private static void registerDeviceForJacorb(final String name) throws DevFailed {
        // Get the 3 fields of device name
        // After a header used by JacORB, in the device name
        // the '/' char must be replaced by another separator
        final String targetname = ORBManager.SERVER_IMPL_NAME + "/" + ORBManager.NODB_POA + "/" + name;
        // And set the JacORB objectKeyMap HashMap
        final org.jacorb.orb.ORB jacorb = (org.jacorb.orb.ORB) ORBManager.getOrb();
        jacorb.addObjectKey(name, targetname);
    }
}
