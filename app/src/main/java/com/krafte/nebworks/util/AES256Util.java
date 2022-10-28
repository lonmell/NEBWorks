package com.krafte.nebworks.util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES256Util {
    private String iv;
    private Key keySpec;

    public AES256Util(String key) throws UnsupportedEncodingException {
        this.iv = key.substring(0, 16);
        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes("UTF-8");
        int len = b.length;
        if (len > keyBytes.length) {
            len = keyBytes.length;
        }
        System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        this.keySpec = keySpec;
    } // 암호화

    public String encode(String str) throws UnsupportedEncodingException, NoSuchAlgorithmException
            , NoSuchPaddingException, InvalidKeyException
            , InvalidAlgorithmParameterException, IllegalBlockSizeException
            , BadPaddingException {
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
        byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
        String enStr = new String(Base64.encodeToString(encrypted, 0));
        return enStr;
    } //복호화

    public String decode(String str) throws UnsupportedEncodingException, NoSuchAlgorithmException
            , NoSuchPaddingException, InvalidKeyException
            , InvalidAlgorithmParameterException, IllegalBlockSizeException
            , BadPaddingException {
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes("UTF-8")));
        byte[] byteStr = Base64.decode(str.getBytes(), 0);
        return new String(c.doFinal(byteStr), "UTF-8");
    }
}