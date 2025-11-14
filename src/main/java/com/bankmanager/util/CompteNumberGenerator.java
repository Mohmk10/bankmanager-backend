package com.bankmanager.util;

import java.time.Year;
import java.util.Random;

public class CompteNumberGenerator {

    private static final Random random = new Random();

    public static String generate() {
        int year = Year.now().getValue();
        int randomNumber = 10000000 + random.nextInt(90000000);
        return "C" + year + randomNumber;
    }
}