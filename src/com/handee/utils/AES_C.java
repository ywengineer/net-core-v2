package com.handee.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加解密处理。
 * <p/>
 * 加密Cipher为AES/CBC/PKCS5Padding.
 * </p>
 *
 * @author Mark
 */
public class AES_C {

    /**
     * 加密。
     *
     * @param text     加密文本
     * @param password 密钥
     * @return 加密串
     * @throws Exception 加密异常
     */
    public static String encrypt(String text, String password) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] keyBytes = new byte[16];

        byte[] b = new BASE64Decoder().decodeBuffer(password);

        int len = b.length;

        if (len > keyBytes.length)
            len = keyBytes.length;

        System.arraycopy(b, 0, keyBytes, 0, len);

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        byte[] iv = new byte[16];

        System.arraycopy(keyBytes, 0, iv, 0, iv.length);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        byte[] results = cipher.doFinal(text.getBytes("UTF-8"));

        byte[] output = new byte[iv.length + results.length];

        System.arraycopy(iv, 0, output, 0, iv.length);

        System.arraycopy(results, 0, output, iv.length, results.length);

        BASE64Encoder encoder = new BASE64Encoder();

        return encoder.encode(output);

    }

    /**
     * 解密
     *
     * @param text     加密串
     * @param password 密钥
     * @return 原文本
     * @throws Exception 加密异常
     */
    public static String decrypt(String text, String password) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        BASE64Decoder decoder = new BASE64Decoder();

        byte[] keyBytes = new byte[16];

        byte[] b = new BASE64Decoder().decodeBuffer(password);
        int len = b.length;
        if (len > keyBytes.length)
            len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);

        byte[] encryptedData = decoder.decodeBuffer(text);
        byte[] iv = new byte[16];
        System.arraycopy(encryptedData, 0, iv, 0, iv.length);

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        byte[] dataToDecrypt = new byte[encryptedData.length - iv.length];
        System.arraycopy(encryptedData, iv.length, dataToDecrypt, 0, dataToDecrypt.length);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        byte[] plainText = cipher.doFinal(dataToDecrypt);

        return new String(plainText, "UTF-8");
    }

    public static void main(String[] args) throws Exception {
        String key = "1234567891234567";
        String data = "artnerid=1000&account=qwes33&timestamp=1316571670&sign=c69453915ef210039b1ff27db59c0ff8";
        System.out.println(decrypt("1234567891234567AAAAADVGBBU/2t0KQtNyeeUdWFdJpsVR3NwByb/gZqQkVZd/NnB8vV2dKvU2ctB0nkAoSnbHUDZ9IHNielCCchokPV8QAqO7bOd5yXv1AVGRqttPRRVapxfBqKkHCMAm63drBw==", key));
        System.out.println(encrypt(data, key));
    }
}
