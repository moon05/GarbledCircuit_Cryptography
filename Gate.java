package com.applied_crypto;

import com.applied_crypto.Util;
import org.apache.commons.codec.binary.Hex;
import java.util.*;


public class Gate {
    ArrayList<Integer> table = new ArrayList<Integer>();
    String [] inputs = new String[2];
    String [] outputs = new String[1];
    String givenType = null ;
    ArrayList<String> stringTable = new ArrayList<String>();

    public Gate(String inp1, String inp2, String out) {
        inputs[0] = inp1;
        inputs[1] = inp2;
        outputs[0] = out;
    }


    public void fill_table(ArrayList<Integer> tab) {
        table = tab;
    }


    public ArrayList<Integer> get_table() {
        return table;
    }

    public String[] get_inputs() {
        return inputs;
    }

    public String[] get_outputs() {

        return outputs;

    }

    //helper getter and setter methods that I have used in Generator

    public void set_table(ArrayList<String> encodedTable){
        stringTable = encodedTable;
    }

    public ArrayList<String> get_hex_table(){
        return stringTable;
    }

    //helper getter and setter methods for the Type key

    public void set_Type(String gType){
        givenType = gType;
    }

    public String get_Type(){
        return givenType;
    }



    /*
     * remember to inverse keys
     */
    public int decrypt_table(int key1 , int key2 ) throws Exception {
        if (key1 == 0 && key2 == 0) {
            return table.get(0);

        } else if (key1 == 0 && key2 == 1) {
            return table.get(1);
        } else if (key1 == 1 && key2 == 0) {
            return table.get(2);
        } else if (key1 == 1 && key2 == 1) {
            return table.get(3);
        }
        return 100;
    }


    public int get_entry(int i) {

        return table.get(i);

    }

}

