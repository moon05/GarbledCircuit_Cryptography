package com.applied_crypto;
import com.applied_crypto.Util;
import org.apache.commons.codec.binary.Hex;
import java.util.*;


public class Evaluator_Gate {

    /**
     *
     * This is the class file for garbled/evaluator gate. You will
     * use it inside Evaluator.java.
     *
     */



    byte[][] table = new byte[4][];
    String [] inputs = new String[2];
    String [] outputs = new String[1];

    public Evaluator_Gate(String inp1, String inp2, String out) {

        /**
         *
         * Assigning inputs and outs of gate, which will be inportant
         * in evaluating gate and also for circuit topology.
         *
         */

        inputs[0] = inp1;
        inputs[1] = inp2;
        outputs[0] = out;
    }

    public void fill_table(String[] tab) throws Exception {

        /**
         *
         * Reading table values and storing as bytes
         *
         */

        for ( int i = 0; i < 4; i++) {
            table[i] = Hex.decodeHex(tab[i].toCharArray());
        }
    }

    public byte[][] get_table() {
        return table;
    }

    public String[] get_inputs() {
        return inputs;
    }

    public String[] get_outputs() {
        return outputs;
    }

    public String decrypt_table(String key1 , String key2 ) throws Exception {

        byte[] byte_key1 = Hex.decodeHex(key1.toCharArray());
        byte[] byte_key2 = Hex.decodeHex(key2.toCharArray());

        String m = null;

        for (int i=0; i<4; i++){
            byte[] temp = Util.specialDecryption(byte_key2, table[i]);
            if (temp != null) {

                byte[] m_byte = Util.specialDecryption(byte_key1, temp);
                if (m_byte != null){
                    m = Hex.encodeHexString(m_byte);
                    break;
                }
            }
        }

        return m;


        /*
        This function will takes in two keys and try to decrypt gate table entries.

            TODO:
            - Decrypt each entry
            - Check if entry is valid, that is find tab

            HINT:
            - Take care about sequence of decryption keys as it should be oposite to encryption.

         */

	// System.out.println("TODO: decrypt_table has not been implemented yet in Evaluator_Gate.java");
	// return null;
    }
}
