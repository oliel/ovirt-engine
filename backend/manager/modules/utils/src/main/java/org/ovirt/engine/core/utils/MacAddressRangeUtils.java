package org.ovirt.engine.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class MacAddressRangeUtils {

    private static final long MAC_ADDRESS_MULTICAST_BIT = 0x010000000000L;
    private static final int HEX_RADIX = 16;

    public static List<String> initRange(String start, String end, int size) {
        return innerInitRange(start, end, size);
    }

    private static List<String> innerInitRange(String start, String end, int stopAfter) {
        String parsedRangeStart = StringUtils.remove(start, ':');
        String parsedRangeEnd = StringUtils.remove(end, ':');
        if (parsedRangeEnd == null || parsedRangeStart == null) {
            return Collections.emptyList();
        }

        long startNum = Long.parseLong(parsedRangeStart, HEX_RADIX);
        long endNum = Long.parseLong(parsedRangeEnd, HEX_RADIX);
        return innerInitRange(stopAfter, startNum, endNum);
    }

    private static List<String> innerInitRange(int stopAfter, long startNum, long endNum) {
        if (startNum > endNum) {
            return Collections.emptyList();
        }

        // Initialize ArrayList for all potential records. (ignore that there need not be that many records.
        List<String> macAddresses = new ArrayList<>(Math.min(stopAfter, (int) (endNum - startNum)));
        for (long i = startNum; i <= endNum; i++) {
            if ((MAC_ADDRESS_MULTICAST_BIT & i) != 0) {
                continue;
            }

            macAddresses.add(macToString(i));

            if (stopAfter-- <= 0) {
                return macAddresses;
            }
        }

        return macAddresses;
    }

    public static String macToString(long macAddress) {
        String value = String.format("%012x", macAddress);
        char[] chars = value.toCharArray();

        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(chars[0]).append(chars[1]);
        for (int pos = 2; pos < value.length(); pos += 2) {
            stringBuilder.append(":")
                    .append(chars[pos])
                    .append(chars[pos + 1]);
        }

        return stringBuilder.toString();
    }

    public static boolean isRangeValid(String start, String end) {
        return !innerInitRange(start, end, 1).isEmpty();
    }
}
