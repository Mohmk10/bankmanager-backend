package com.bankmanager.util;

import java.time.Year;
import java.util.Random;

public class TransactionIdGenerator {

    private static final Random random = new Random();

    public static String generate() {
        int year = Year.now().getValue();
        int randomNumber = 100000 + random.nextInt(900000);
        return "T" + year + randomNumber;
    }
}