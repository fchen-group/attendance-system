package com.uzpeng.sign.util;

import com.uzpeng.sign.domain.UserDO;

import java.util.HashMap;

/**
 */
public class UserMap {

    private static final HashMap<String, UserDO> idMap = new HashMap<>();
    private static final HashMap<Integer, String> tokenMap = new HashMap<>();
    private static final HashMap<String, Integer> wsSessionMap = new HashMap<>();

    public static void putUser(String auth, UserDO role){
        idMap.put(auth, role);
    }

    public static UserDO getUser(String auth){
        return idMap.get(auth);
    }

    public static void remove(String auth){
        idMap.remove(auth);
    }

    public static void putToken(Integer signId, String token){
        tokenMap.put(signId, token);
    }

    public static String getToken(Integer signId){
        return tokenMap.get(signId);
    }

    public static void removeToken(Integer signId){
        tokenMap.remove(signId);
    }

    public static void putSignId(String sessionId, Integer signId){
        wsSessionMap.put(sessionId, signId);
    }

    public static Integer getSignId(String sessionId){
        return wsSessionMap.get(sessionId);
    }

    public static void removeSignId(String sessionId){
        wsSessionMap.remove(sessionId);
    }

}
