package com.cartify.common.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtil {
    private DateTimeUtil() {}

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_INSTANT;

    public static String nowIso() {
        return ISO.format(Instant.now().atOffset(ZoneOffset.UTC));
    }
}
