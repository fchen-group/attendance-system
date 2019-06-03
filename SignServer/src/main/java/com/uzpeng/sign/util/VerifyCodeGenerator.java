package com.uzpeng.sign.util;

import java.util.Random;

/**
 */
public class VerifyCodeGenerator {
    private static final int  DEFAULT_LENGTH = 6;

    public static String generate(){
        StringBuilder verifyCode = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < DEFAULT_LENGTH; i++) {
            verifyCode.append(random.nextInt(10));
        }
        return verifyCode.toString();
    }
}
