package com.mvp.common.tools;

/**
 * Created by tanglin on 2017/6/27.
 */

public class TokenValidation {

    private static TokenValidation single = null;
    private long expireTime;
    private int intervalTime;

    private TokenValidation() {

    }

    public static TokenValidation getInstance() {
        if (single == null) {
            synchronized (TokenValidation.class) {
                if (single == null) {
                    single = new TokenValidation();
                }
            }
        }
        return single;
    }

    public boolean isValidToken() {
        if (expireTime > System.currentTimeMillis() && expireTime < System.currentTimeMillis() + intervalTime * 60 * 1000) {
            return true;
        } else {
            return false;
        }
    }

    public void updateToken() {

    }

    public void clearToken() {

    }

    //set expire time wiht minute unit
    public void setExpireTime(int time) {
        intervalTime = time;
        expireTime = System.currentTimeMillis() + time * 60 * 1000;
    }


}
