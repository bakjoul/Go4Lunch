package com.bakjoul.go4lunch.data.utils;

import androidx.annotation.NonNull;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;
import java.util.Date;

public class TimeUtils {
    @NonNull
    public static Date getNetworkTime() {
        NTPUDPClient client = new NTPUDPClient();
        client.setDefaultTimeout(5000);
        try {
            InetAddress address = InetAddress.getByName("pool.ntp.org");
            TimeInfo timeInfo = client.getTime(address);
            return new Date(timeInfo.getMessage().getTransmitTimeStamp().getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return new Date();
        } finally {
            client.close();
        }
    }
}
