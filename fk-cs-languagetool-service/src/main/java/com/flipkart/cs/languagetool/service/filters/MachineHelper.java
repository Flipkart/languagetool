package com.flipkart.cs.languagetool.service.filters;

import java.net.InetAddress;

/**
 * Created by anmol.kapoor on 03/01/17.
 */
public class MachineHelper {
    private static String machineName = null;
    private static String defaultMachineName = "machine";

    public static String getMachineName() {

        if (machineName == null) {
            try {
                String hostName = InetAddress.getLocalHost().getHostName();
                if (machineName.contains("local")) machineName = "local";
                else
                {
                    machineName = machineName+"-"+InetAddress.getLocalHost().getHostAddress();
                }
            } catch (Exception e) {
                machineName = defaultMachineName;
            }
        }
        return machineName;
    }
}