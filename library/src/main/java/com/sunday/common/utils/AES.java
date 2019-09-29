package com.sunday.common.utils;

import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * 作者 : mlx
 * 时间 : 2016/11/11 10:32
 * 说明 : Android 和 ios 通用AES加密 {@link "http://blog.csdn.net/yuxikuo_1/article/details/39476071"}
 */

public class AES {


    private final String KEY_GENERATION_ALG = "PBKDF2WithHmacSHA1";

    private final int HASH_ITERATIONS = 10000;
    private final int KEY_LENGTH = 256;

    private char[] humanPassphrase = {'P', 'e', 'r', ' ', 'v', 'a', 'l', 'l',
            'u', 'm', ' ', 'd', 'u', 'c', 'e', 's', ' ', 'L', 'a', 'b', 'a', 'n', 't'};
    private byte[] salt = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0xA, 0xB, 0xC, 0xD,
            0xE, 0xF}; // must save this for next time we want the key

    private PBEKeySpec myKeyspec = new PBEKeySpec(humanPassphrase, salt, HASH_ITERATIONS, KEY_LENGTH);
    private final String CIPHERMODEPADDING = "AES/CBC/PKCS7Padding";

    private SecretKeyFactory keyfactory = null;
    private SecretKey sk = null;
    private SecretKeySpec skforAES = null;
    private byte[] iv = {0xA, 1, 0xB, 5, 4, 0xF, 7, 9, 0x17, 3, 1, 6, 8, 0xC, 0xD, 91};

    private IvParameterSpec IV;

    public AES() {

        try {
            keyfactory = SecretKeyFactory.getInstance(KEY_GENERATION_ALG);
            sk = keyfactory.generateSecret(myKeyspec);
        } catch (NoSuchAlgorithmException nsae) {
            Log.e("AESdemo", "no key factory support for PBEWITHSHAANDTWOFISH-CBC");
        } catch (InvalidKeySpecException ikse) {
            Log.e("AESdemo", "invalid key spec for PBEWITHSHAANDTWOFISH-CBC");
        }

        // This is our secret key. We could just save this to a file instead of regenerating it
        // each time it is needed. But that file cannot be on the device (too insecure). It could
        // be secure if we kept it on a server accessible through https.
        byte[] skAsByteArray = sk.getEncoded();
        // Log.d("","skAsByteArray=" + skAsByteArray.length + ","+ Base64Encoder.encode(skAsByteArray));
        skforAES = new SecretKeySpec(skAsByteArray, "AES");
        IV = new IvParameterSpec(iv);
    }

    /**
     * 加密
     * @param plaintext
     * @return
     */
    public String encrypt(byte[] plaintext) {

        byte[] ciphertext = encrypt(CIPHERMODEPADDING, skforAES, IV, plaintext);
        String base64_ciphertext = Base64Encoder.encode(ciphertext);
        return base64_ciphertext;
    }

    /**
     * 解密
     * @param ciphertext_base64
     * @return
     */
    public String decrypt(String ciphertext_base64) {
        byte[] s = Base64Decoder.decodeToBytes(ciphertext_base64);
        String decrypted = new String(decrypt(CIPHERMODEPADDING, skforAES, IV, s));
        return decrypted;
    }

    // Use this method if you want to add the padding manually
    // AES deals with messages in blocks of 16 bytes.
    // This method looks at the length of the message, and adds bytes at the end
    // so that the entire message is a multiple of 16 bytes.
    // the padding is a series of bytes, each set to the total bytes added (a
    // number in range 1..16).
    private byte[] addPadding(byte[] plain) {
        byte[] plainpad = null;
        int shortage = 16 - (plain.length % 16);
        // if already an exact multiple of 16, need to add another block of 16
        // bytes
        if (shortage == 0) {
            shortage = 16;
        }

        // reallocate array bigger to be exact multiple, adding shortage bits.
        plainpad = new byte[plain.length + shortage];
        for (int i = 0; i < plain.length; i++) {
            plainpad[i] = plain[i];
        }
        for (int i = plain.length; i < plain.length + shortage; i++) {
            plainpad[i] = (byte) shortage;
        }
        return plainpad;
    }

    // Use this method if you want to remove the padding manually
    // This method removes the padding bytes
    private byte[] dropPadding(byte[] plainpad) {
        byte[] plain = null;
        int drop = plainpad[plainpad.length - 1]; // last byte gives number of
        // bytes to drop

        // reallocate array smaller, dropping the pad bytes.
        plain = new byte[plainpad.length - drop];
        for (int i = 0; i < plain.length; i++) {
            plain[i] = plainpad[i];
            plainpad[i] = 0; // don't keep a copy of the decrypt
        }
        return plain;
    }

    private byte[] encrypt(String cmp, SecretKey sk, IvParameterSpec IV, byte[] msg) {
        try {
            Cipher c = Cipher.getInstance(cmp);
            c.init(Cipher.ENCRYPT_MODE, sk, IV);
            return c.doFinal(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] decrypt(String cmp, SecretKey sk, IvParameterSpec IV, byte[] ciphertext) {
        try {
            Cipher c = Cipher.getInstance(cmp);
            c.init(Cipher.DECRYPT_MODE, sk, IV);
            return c.doFinal(ciphertext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void testAES() {
//        AES gAes = new AES();
//
//        String sendString = "欢迎，Hello World！";
//        byte[] sendBytes = null;
//        try {
//            sendBytes = sendString.getBytes("UTF8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        String str = gAes.encrypt(sendBytes);
//        System.out.println(str);
//        String result = gAes.decrypt(str);
//        System.out.println(result);
    }

}
