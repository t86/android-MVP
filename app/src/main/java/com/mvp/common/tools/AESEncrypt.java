package com.mvp.common.tools;

/**
 * Created by tanglin on 2017/2/17.
 */


import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESEncrypt {
    //用来加密的公钥：app端使用
    private static final String DEFAULT_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApWczn1kTu+R5CaHM2f3zzvKHvjd1YI33U783+TavQ188WHWXC0VrTELsGKDHdMFMzUAetGPLVl3siDgFZYk81kcnvlsyyuBe9lLwK0Qpkzxa+lNpPirblixX/YwV1VokvvhV4Lc17kRYgXJEwsr1lm6JHJaUJ3jZ2F+ajsyK9xwphX2/rkj9BDPqa/LrK4bIkR9Z/uEuHBRFYInoHtZMGbDFoHKVMWvz5efWxgftM3cRKdmC5ln5AFo6Vdc+1Oyo29MbCYzYP6mefPhUjcxGXVEfCFUSaMH1PyAEiBspFWiiO7KE1nMH3X4JrYNUPncBHmH7fEO27DylexHv5MQ9TwIDAQAB";

    public static final String AES128_KEY = "bdxiaodai123!@#$"; // aes128加密固定key


    public static final byte[] IV = {0x41, 0x72, 0x65, 0x79, 0x6F, 0x75, 0x6D, 0x79, 0x53, 0x6E, 0x6F, 0x77, 0x6D, 0x61, 0x6E, 0x3F};

    /**
     * 加密
     *
     * @param content 需要加密的内容
     * @param applyKey  加密密码
     * @return
     */
    public static String encrypt(String content, String applyKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        String sKey = applyKey;

        if (sKey == null) {
            sKey = AES128_KEY;
        }

        // 判断Key是否为16位
        if (sKey.length() != 16) {
            System.out.print("Key长度不是16位");
            return null;
        }
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");//"算法/模式/补码方式"
        IvParameterSpec iv = new IvParameterSpec(IV);//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

        byte[] srawt = content.getBytes();
        int len = srawt.length;
        /* 计算补0后的长度 */
        while (len % 16 != 0) len++;
        byte[] sraw = new byte[len];
        /* 在最后补0 */
        for (int i = 0; i < len; ++i) {
            if (i < srawt.length) {
                sraw[i] = srawt[i];
            } else {
                sraw[i] = 0;
            }
        }
        byte[] encrypted = cipher.doFinal(sraw);

        return Base64Utils.encode(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
    }
    /**解密
     * @param content  待解密内容
     * @param password 解密密钥
     * @return
     */
    public byte[] decrypt(byte[] content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

}