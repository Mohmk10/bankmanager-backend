package com.bankmanager.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AccountNumberGenerator {

    private static final SecureRandom random = new SecureRandom();

    public static String generate() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.format("%05d", random.nextInt(100000));

        return "C" + datePart + randomPart;
    }
}