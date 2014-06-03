package com.handee.utils;

import org.apache.mina.util.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.Provider;

/**
 * AES加解密处理。
 *
 * @author Mark
 */
public class AES {
    private static final String CHARSET = "UTF-8";
    private static final Charset DEFAULT_CHARSET = Charset.forName(CHARSET);
    public static final Provider provider = new BouncyCastleProvider();
    public static final IvParameterSpec iv = new IvParameterSpec(new byte[16]);
    public static final String CBC_PKCS7 = "AES/CBC/PKCS7Padding";

    public static final String ECB_PKCS5 = "AES/ECB/PKCS5Padding";
    public static final String ECB_PKCS7 = "AES/ECB/PKCS7Padding";

    /**
     * 使用AES/CBC/PKCS7Padding加密。
     *
     * @param input 文本
     * @param key   密钥
     * @return 加密串
     * @throws Exception 加密异常
     */
    public static String encryptWithPKCS7(String input, String key) throws Exception {
        return encrypt(input, key, CBC_PKCS7, provider);
    }

    /**
     * 使用AES/CBC/PKCS7Padding解密。
     *
     * @param input 加密串
     * @param key   密钥
     * @return 原文本
     * @throws Exception 解密异常
     */
    public static String decryptWithPKCS7(String input, String key) throws Exception {
        return decrypt(input, key, CBC_PKCS7, provider);
    }

    /**
     * 使用AES/CBC/PKCS5Padding加密。
     *
     * @param input 文本
     * @param key   密钥
     * @return 加密串
     * @throws Exception 加密异常
     */
    public static String encryptWithPKCS5(String input, String key) throws Exception {
        return encrypt(input, key, ECB_PKCS5, null);
    }

    /**
     * 使用AES/CBC/PKCS5Padding解密。
     *
     * @param input 加密串
     * @param key   密钥
     * @return 原文本
     * @throws Exception 解密异常
     */
    public static String decryptWithPKCS5(String input, String key) throws Exception {
        return decrypt(input, key, ECB_PKCS5, null);
    }

    /**
     * 加密。
     *
     * @param input     文本
     * @param key       密钥
     * @param algorithm 算法
     * @param provider  加密算法提供方
     * @return 加密串
     * @throws java.lang.Exception 加密异常。
     */
    public static String encrypt(String input, String key, String algorithm, Provider provider) throws Exception {
        Cipher cipher;//= provider == null ? Cipher.getInstance(algorithm) : Cipher.getInstance(algorithm, provider);
        if (algorithm.contains("/CBC/")) {
            cipher = Cipher.getInstance(algorithm, provider);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(CHARSET), "AES"), iv);
        } else {
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(CHARSET), "AES"));
        }
        return new String(Base64.encodeBase64(cipher.doFinal(input.getBytes(CHARSET))), CHARSET);
    }

    /**
     * 解密。
     *
     * @param input     加密串
     * @param key       密钥
     * @param algorithm 算法
     * @param provider  加密算法提供方
     * @return 原文本
     * @throws java.lang.Exception 加密异常。
     */
    public static String decrypt(String input, String key, String algorithm, Provider provider) throws Exception {
        Cipher cipher;// = provider == null ? Cipher.getInstance(algorithm) : Cipher.getInstance(algorithm, provider);
        if (algorithm.contains("/CBC/")) {
            cipher = Cipher.getInstance(algorithm, provider);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(CHARSET), "AES"), iv);
        } else {
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(CHARSET), "AES"));
        }
        return new String(cipher.doFinal(Base64.decodeBase64(input.getBytes(CHARSET))), CHARSET);
    }

    public static void main(String[] args) throws Exception {
        String key = "1234567891234567";
        String data = "artnerid=1000&account=qwes33&timestamp=1316571670&sign=c69453915ef210039b1ff27db59c0ff8";
//        System.out.println(AES.decrypt("6y7Go3Bgm4cqRQ7OTIwJtGAuuo4ya79fQNDu8tPChKhve53/86a4C25HqCoMetugxEq64BLP5J+7MQ9kFGJiiDkaaPFdHXqt5RmOxj+6gUd5PnSQ5A51ZWrStM6K+ASY", key, "AES/CBC/PKCS5Padding"));
        System.out.println(AES.encryptWithPKCS7(data, key));
        System.out.println(AES.encryptWithPKCS7(data + "===", key));
        System.out.println(AES.decryptWithPKCS7("5VCcAiT24ahH99TJ3V1nL0jsSJQh/UsMWKwXlJ6ExlO4Wu/mVxrP9+2HTbHMEpiMbSPzV6aTg1UHWY7rEgVnFNWVv7yb18LEPdwcHTVVyXzXOTZRiIdHRi46IOARsW3YC2ralD/G/3utbmBbPrRq3eplNY9BTlv2xgdX6NjQTOkDIMqFKfnYNhm3Xucgo9CHsWhZ/mA7up8dMf87R5aNk+aRRbzrHvVN0M2gHi6c3SciDORuFfxaDa0NWREUxAdYmBirExquIgZP5w2rjEhCMhv5osDgCjCpQ+SVHsE0zpciDQiadprf3htXZnGNoTgVlZBuKkz3CLRju1NPxQHHe13iRo1sZuOeWiPvWqEDYjOrIfLGPnOq4wIHNAXS74wuTugq5enwQ6vzLqfnZGo9/Z7Ljq92eH3ebEBJz0cNyAo=", key));
        System.out.println(AES.decryptWithPKCS7("i3AaIa4sCcEpnuCGwpTZ7aoyEVGE1OBh9Xap8sWr3R8OR3o3ewHoxSyAFLWtJPvNw0tJgBVRTyZxuOMpQbGLjQS1YWnGggZQLllG7qsb+M5C63LLMmvAwVr0FxNT/7r9", key));

        System.out.println("==========");

        System.out.println(AES.encryptWithPKCS5(data, key));
        System.out.println(AES.encryptWithPKCS5(data + "===", key));
        System.out.println(AES.decryptWithPKCS5("6y7Go3Bgm4cqRQ7OTIwJtGAuuo4ya79fQNDu8tPChKhve53/86a4C25HqCoMetugxEq64BLP5J+7MQ9kFGJiiDkaaPFdHXqt5RmOxj+6gUd5PnSQ5A51ZWrStM6K+ASY", key));
        System.out.println(AES.decryptWithPKCS5("6y7Go3Bgm4cqRQ7OTIwJtGAuuo4ya79fQNDu8tPChKhve53/86a4C25HqCoMetugxEq64BLP5J+7MQ9kFGJiiDkaaPFdHXqt5RmOxj+6gUf07LHRzsEbRQSu+K0ysgxM", key));

    }
}
