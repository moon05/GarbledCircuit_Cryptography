package com.applied_crypto;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import org.apache.commons.codec.binary.Hex;
import javax.xml.bind.DatatypeConverter;
import java.util.Arrays;
import java.security.SecureRandom;
import java.util.Random;


public class Util {

    /**** Problem XXX! How to generate random numbers in Java.
      - The following are three ways of generating random bytes in
      Java. Which one is appropriate for cryptographic schemes (such
      as shuffling the rows in your garbling table)?
      In a comment included in your code, explain which one to use.
    *****/

    public static final byte[] GetRandom() {
	return GetRandom1(); // TODO: You must change this to GetRandom1(), GetRandom2(), or GetRandom3(), and leave a comment explaining why!
    }

    public static final byte[] GetRandom1() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[16];
        random.nextBytes(bytes);
        return bytes;
    }

    public static final byte[] GetRandom2() {
        Random random = new Random();
        byte bytes[] = new byte[16];
        random.nextBytes(bytes);
        return bytes;
    }

    public static final byte[] GetRandom3() {
        byte bytes[] = new byte[16];
        for (int i = 0; i < 16; i++) {
            bytes[i] = (byte)(Math.random() * 256);
        }
        return bytes;
    }

    public static final int KEYLENGTH = 128; // n from Course in Cryptography textbook

    /* Function to generate AES key */

    public static  byte[] generate_key() throws Exception {
	return GetRandom();
    }

    public static byte[] specialEncryption(byte[] k, byte[] m) throws Exception {

        ///DONE///

	// Special Encryption algorithm from Course in Cryptography
	assert(k.length == KEYLENGTH/8);
	assert(m.length <= KEYLENGTH/8 * 3); // m must be bounded in size

        byte[] r = GetRandom();
	// Compute the PRF
	byte[] prf = lengthQuadruplingPRF(k, r);

	// Create the padded message
	// The padding is the first KEYLENGTH/8 bytes
	byte[] mm = new byte[KEYLENGTH/8 + m.length];
	for (int i = 0; i < KEYLENGTH/8; i++) mm[i] = 0; // Padding is all 0 bits
	System.arraycopy(m, 0, mm, KEYLENGTH/8, m.length);
	
        // XORing the message (in place)
	assert(mm.length <= prf.length);
        for (int i = 0; i < mm.length; i++) {
            mm[i] = (byte) (((int) mm[i]) ^ ((int) prf[i]));
        }

        // Return concatenation of (r, mm)
        byte[] output = new byte[r.length + mm.length];
        System.arraycopy(r,  0, output, 0,         r.length);
        System.arraycopy(mm, 0, output, r.length, mm.length);
	return output;
    }

    public static byte[] specialDecryption(byte[] k, byte[] c) throws Exception {
	assert(k.length == KEYLENGTH/8);
	assert(c.length > KEYLENGTH/8*2);

    //length of r which should be n
    byte[] c_1 = new byte[KEYLENGTH/8];
    System.arraycopy(c, 0, c_1, 0, c_1.length);
    //rest of actual cipher text after subtracting n
    byte[] c_2 = new byte[c.length-k.length];
    System.arraycopy(c, c_1.length, c_2, 0, c_2.length);

    //making prf with c_1 as input
    byte[] prf = lengthQuadruplingPRF(k, c_1);

    //dummy message m_1
    byte[] m_1 = new byte[KEYLENGTH/8];
    byte[] m_2 = new byte[c_2.length - m_1.length];

    for (int i=0; i<c_2.length; i++){
        c_2[i] = (byte) (((int)c_2[i]) ^ ((int)prf[i]));
    }

    System.arraycopy(c_2, 0, m_1, 0, m_1.length);
    System.arraycopy(c_2, m_1.length, m_2, 0, m_2.length);


    //return null if m_1 is not zero otherwise return m_2
    for (int i = 0; i < KEYLENGTH/8; i++){
        if (m_1[i] != 0){
            return null;
        }
    }

	return m_2;
    }

    public static byte[] lengthQuadruplingPRF(byte[] k, byte[] r) throws Exception {
	// Input: 16 byte key, 16 byte value
	// Output: 64 byte pseudorandom bytes
	assert(k.length == KEYLENGTH/8);
	assert(r.length == KEYLENGTH/8);
	SecretKeySpec skeySpec = new SecretKeySpec(k, "AES");
	IvParameterSpec ivSpec = new IvParameterSpec(new byte[KEYLENGTH/8]); // iv to 0
	Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
	byte[] counter = new byte[KEYLENGTH/8];
	byte[] output = new byte[KEYLENGTH/8 * 4];
	for (int i = 0; i < 4; i++) {
	    byte[] block = cipher.update(r);
	    System.arraycopy(block, 0, output, KEYLENGTH/8*i, KEYLENGTH/8);
	}
	return output;
    }

    public static void print_table(byte[][] table, String s) throws Exception {
        System.out.println(s);
        for ( int i = 0; i < 4; i++) {
            System.out.println(Hex.encodeHexString(table[i]));
        }
    }

    public static void print_wire(byte[][] wire) throws Exception {
        for ( int i = 0; i < 2; i++) {
            System.out.println(Hex.encodeHexString(wire[i]));
        }
    }
    public static void print_key(byte[] entry) {
        System.out.println(Hex.encodeHexString(entry));
    }


}
