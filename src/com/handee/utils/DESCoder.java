package com.handee.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.Key;

/**
 * @author wang
 */
public class DESCoder {
    private static BASE64Encoder base64 = new BASE64Encoder();
    private static byte[] myIV = {50, 51, 52, 53, 54, 55, 56, 57};

    // 字节数必须是8的倍数
    // private static String strkey = "01234567890123456789012345678912";

    public static String desEncrypt(String input, String strkey) throws Exception {

        BASE64Decoder base64d = new BASE64Decoder();
        DESedeKeySpec p8ksp = new DESedeKeySpec(base64d.decodeBuffer(strkey));
        Key key = SecretKeyFactory.getInstance("DESede").generateSecret(p8ksp);

        input = padding(input);

        byte[] plainBytes = input.getBytes(Charset.forName("UTF-8"));
        Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
        SecretKeySpec myKey = new SecretKeySpec(key.getEncoded(), "DESede");
        IvParameterSpec ivspec = new IvParameterSpec(myIV);
        cipher.init(1, myKey, ivspec);
        byte[] cipherText = cipher.doFinal(plainBytes);
        return removeBR(base64.encode(cipherText));

    }

    public static String desDecrypt(String cipherText, String strkey) throws Exception {
        cipherText = URLDecoder.decode(cipherText, "UTF-8");
        BASE64Decoder base64d = new BASE64Decoder();
        DESedeKeySpec p8ksp = new DESedeKeySpec(base64d.decodeBuffer(strkey));
        Key key = SecretKeyFactory.getInstance("DESede").generateSecret(p8ksp);

        byte[] inPut = base64d.decodeBuffer(cipherText);
        Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
        SecretKeySpec myKey = new SecretKeySpec(key.getEncoded(), "DESede");
        IvParameterSpec ivspec = new IvParameterSpec(myIV);
        cipher.init(2, myKey, ivspec);
        byte[] output = removePadding(cipher.doFinal(inPut));

        return new String(output, "UTF8");

    }

    private static String removeBR(String str) {
        StringBuffer sf = new StringBuffer(str);

        for (int i = 0; i < sf.length(); ++i) {
            if (sf.charAt(i) == '\n') {
                sf = sf.deleteCharAt(i);
            }
        }
        for (int i = 0; i < sf.length(); ++i)
            if (sf.charAt(i) == '\r') {
                sf = sf.deleteCharAt(i);
            }

        return sf.toString();
    }

    public static String padding(String str) {
        byte[] oldByteArray;
        try {
            oldByteArray = str.getBytes("UTF8");
            int numberToPad = 8 - oldByteArray.length % 8;
            byte[] newByteArray = new byte[oldByteArray.length + numberToPad];
            System.arraycopy(oldByteArray, 0, newByteArray, 0, oldByteArray.length);
            for (int i = oldByteArray.length; i < newByteArray.length; ++i) {
                newByteArray[i] = 0;
            }
            return new String(newByteArray, "UTF8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("Crypter.padding UnsupportedEncodingException");
        }
        return null;
    }

    public static byte[] removePadding(byte[] oldByteArray) {
        int numberPaded = 0;
        for (int i = oldByteArray.length; i >= 0; --i) {
            if (oldByteArray[(i - 1)] != 0) {
                numberPaded = oldByteArray.length - i;
                break;
            }
        }

        byte[] newByteArray = new byte[oldByteArray.length - numberPaded];
        System.arraycopy(oldByteArray, 0, newByteArray, 0, newByteArray.length);

        return newByteArray;
    }

    public static void main(String args[]) {

        String str = "AuGGDqs6sb8=";

        System.out.println(str);
        try {
            // String strkey = "2e9fdb6ea74a461196b48949e3fcb8b7";
            // String strkey=CommonsUtils.getUUId();

            // String desstr =
            // DESCoder.desEncrypt("1qaz2ws=ss&sdsd=&sadf",strkey);

            String pstr = DESCoder.desDecrypt(str, "2e9fdb6ea74a461196b48949e3fcb8b7");
            // System.out.println("strkey:"+strkey);
            // System.out.println("length:"+strkey.length());

            // System.out.println("Encode:" + desstr);
            System.out.println("Decode:" + pstr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
