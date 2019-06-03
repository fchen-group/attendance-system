package com.uzpeng.sign.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

public class CryptoUtil {
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);

    public static String encodePassword(String originPassword){
        return passwordEncoder.encode(originPassword);
    }

    public static boolean match(String origin, String encoded){
        return passwordEncoder.matches(origin, encoded);
    }

}
