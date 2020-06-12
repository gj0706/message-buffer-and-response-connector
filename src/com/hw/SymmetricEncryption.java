package com.hw;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class SymmetricEncryption {
    static byte[] encrypt(byte s[], Cipher c, SecretKey sk) throws Exception
    {
        c.init(Cipher.ENCRYPT_MODE, sk);
        return c.doFinal(s);
    }

    static byte[] decrypt(byte s[], Cipher c, SecretKey sk) throws Exception
    {
        c.init(Cipher.DECRYPT_MODE, sk);
        return c.doFinal(s);
    }
}
