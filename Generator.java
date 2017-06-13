package com.applied_crypto;

import com.applied_crypto.Util;
import org.apache.commons.codec.binary.Hex;
import java.util.*;
import org.json.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class Generator {
	private BufferedReader br;
	private String current_line = "";
	private String whole_file = "";
	private JSONObject circuit;

	private JSONObject gates;

	private JSONObject wires;

	private ArrayList<Gate> gates_list = new ArrayList<Gate>();
	private HashMap input_hm = new HashMap();

	private JSONObject inputs;

	public String fileName = "";

	private HashMap wires_0_1= new HashMap();

	private Set <String> inp_out = new HashSet<String>();
	private HashMap wireKeys = new HashMap();



	public Generator(String FILENAME) throws Exception {
		br = new BufferedReader( new FileReader(FILENAME));
		fileName = FILENAME;
		System.out.println("reading circuit...");
		while (  (current_line = br.readLine()) != null) {
			whole_file = whole_file + current_line;

		}
		circuit = new JSONObject(whole_file);
	}

	public void parse_circuit() throws Exception {

		circuit = new JSONObject(whole_file);
		gates = (JSONObject)  circuit.get("gates");
		for (Iterator iterator = gates.keys(); iterator.hasNext();) {
			String key = (String) iterator.next();
			JSONObject gate = (JSONObject) gates.get(key);
			ArrayList<String> inputs = new ArrayList<String>();
			ArrayList<String> outputs = new ArrayList<String>();
			ArrayList<Integer> table = new ArrayList<Integer>();

			JSONArray jsonArray = (JSONArray) gate.get("inp");

			if (jsonArray.length() != 2) {
				throw new Exception("Inputs of Gate: " + key + " are wrong." );
			}

			for (int i = 0; i < jsonArray.length(); i++) {
				inputs.add(jsonArray.get(i).toString());
				//adding input to union set
				inp_out.add(jsonArray.get(i).toString());
			}

			jsonArray = (JSONArray) gate.get("out");
			if (jsonArray.length() != 1) {
				throw new Exception("Output of Gate: " + key + " are wrong." );

			}

			for (int i = 0; i < jsonArray.length(); i++) {
				outputs.add(jsonArray.get(i).toString());
				//adding input to union set
				inp_out.add(jsonArray.get(i).toString());
			}

			String k = gate.get("type").toString();
			String catchType = k;

			jsonArray = (JSONArray) gate.get("table");

			if (jsonArray.length() != 4) {
				throw new Exception("Table length of Gate: " + key + " is wrong." );

			}

			for (int i = 0; i < jsonArray.length(); i++) {
				table.add((int)jsonArray.get(i));
			}

			Gate sg = new Gate(inputs.get(0), inputs.get(1), outputs.get(0));
			sg.fill_table(table);
			sg.set_Type(catchType);
			gates_list.add(sg);

			


		}

		inputs = (JSONObject)  circuit.get("inputs");

		for (Iterator iterator_2 = inputs.keys(); iterator_2.hasNext();) {
			String key = (String) iterator_2.next();
			Integer  value = (Integer) inputs.get(key);
			input_hm.put(key, value);
		}

		// System.out.println(inp_out);

		wires = new JSONObject();
		
		for (String s: inp_out){
			ArrayList<byte[]> temp_key_holder = new ArrayList<byte[]>(2);
			JSONObject k = new JSONObject();
			byte[] key0 = Util.GetRandom();
			byte[] key1 = Util.GetRandom();
			k.put("0", Hex.encodeHexString(key0) );
			k.put("1", Hex.encodeHexString(key1) );
			//wires is a global JSONObject
			wires.put(s, k);
		}


		for (int i = 0; i<gates_list.size(); i++){

			String key = "g" + i;

			Gate temp_gate = gates_list.get(i);

			String[] inps = temp_gate.get_inputs();
			String[] out = temp_gate.get_outputs();


			ArrayList<byte[]> wire1_keys = new ArrayList<byte[]>();
			ArrayList<byte[]> wire2_keys = new ArrayList<byte[]>();
			ArrayList<byte[]> outWire_keys = new ArrayList<byte[]>();


			//creating temp for easier access to stuff in wires just below
			JSONObject temp = (JSONObject) wires.get(inps[0]);
			wire1_keys.add( Hex.decodeHex( temp.get("0").toString().toCharArray() ) );
			wire1_keys.add( Hex.decodeHex( temp.get("1").toString().toCharArray() ) );

			temp = (JSONObject) wires.get(inps[1]);
			wire2_keys.add( Hex.decodeHex( temp.get("0").toString().toCharArray() ) );
			wire2_keys.add( Hex.decodeHex( temp.get("1").toString().toCharArray() ) );

			temp = (JSONObject) wires.get(out[0]);
			outWire_keys.add( Hex.decodeHex( temp.get("0").toString().toCharArray() ) );
			outWire_keys.add( Hex.decodeHex( temp.get("1").toString().toCharArray() ) );


			ArrayList<Integer> temp_table = temp_gate.get_table();
			ArrayList<String> encodedTable = new ArrayList<String>();

			// ALL THE COMBINATIONS OF ENCODING OVER KEY "w1", "w2" and "w3"

				if (temp_table.get(0)==0){
					byte[] midEnc = Util.specialEncryption( wire1_keys.get(0), outWire_keys.get(0));
					byte[] t = Util.specialEncryption( wire2_keys.get(0), midEnc );
					encodedTable.add(Hex.encodeHexString(t));
					}

				else {
					byte[] midEnc = Util.specialEncryption( wire1_keys.get(0), outWire_keys.get(1));
					byte[] t = Util.specialEncryption( wire2_keys.get(0), midEnc );
					encodedTable.add(Hex.encodeHexString(t));
				}

				if (temp_table.get(1)==0){
					byte[] midEnc = Util.specialEncryption( wire1_keys.get(0), outWire_keys.get(0));
					byte[] t = Util.specialEncryption( wire2_keys.get(1), midEnc );
					encodedTable.add(Hex.encodeHexString(t));
					}

				else {
					byte[] midEnc = Util.specialEncryption( wire1_keys.get(0), outWire_keys.get(1));
					byte[] t = Util.specialEncryption( wire2_keys.get(1), midEnc );
					encodedTable.add(Hex.encodeHexString(t));

				}

				if (temp_table.get(2)==0){
					byte[] midEnc = Util.specialEncryption( wire1_keys.get(1), outWire_keys.get(0));
					byte[] t = Util.specialEncryption( wire2_keys.get(0), midEnc );
					encodedTable.add(Hex.encodeHexString(t));
					}

				else {
					byte[] midEnc = Util.specialEncryption( wire1_keys.get(1), outWire_keys.get(1));
					byte[] t = Util.specialEncryption( wire2_keys.get(0), midEnc );
					encodedTable.add(Hex.encodeHexString(t));

				}

				if (temp_table.get(3)==0){
					byte[] midEnc = Util.specialEncryption( wire1_keys.get(1), outWire_keys.get(0));
					byte[] t = Util.specialEncryption( wire2_keys.get(1), midEnc );
					encodedTable.add(Hex.encodeHexString(t));
					}

				else {
					byte[] midEnc = Util.specialEncryption( wire1_keys.get(1), outWire_keys.get(1));
					byte[] t = Util.specialEncryption( wire2_keys.get(1), midEnc );
					encodedTable.add(Hex.encodeHexString(t));
				}

			JSONObject final_table = new JSONObject();
			final_table.put("table", encodedTable);
			gates_list.get(i).set_table(encodedTable);

		}

	//big JSONObject that will finally be written to file
	 JSONObject garbledCircuit = new JSONObject();

	//allGates is the JSONObject that holds all the gates as
	//the name suggests
	 JSONObject allGates = new JSONObject();

	 //looping over all the gates in the storage
	 for (int i =0; i<gates_list.size(); i++){
	 	String gateName = "g" + (i+1);

	 	//creating a JSONObject in which all the specific
	 	//gate information is added
	 	JSONObject master = new JSONObject();

	 	String[] inputs = gates_list.get(i).get_inputs();
	 	String[] outputs = gates_list.get(i).get_outputs();
	 	String retType = gates_list.get(i).get_Type();
	 	ArrayList<String> hexTable = gates_list.get(i).get_hex_table();

	 	master.put("inp", inputs);
	 	master.put("out", outputs);
	 	master.put("table", hexTable);
	 	master.put("type", retType);

	 	allGates.put(gateName, master);

	 }

	 //writing the key "gates" and all it's inputs 
	 garbledCircuit.put("gates", allGates);

	 JSONObject garbleInputs = new JSONObject();

	 Set<String> input_set = new HashSet<String>();
	 input_set = input_hm.keySet();
	 
	 for (String key: input_set){

	 	Integer a = (Integer) input_hm.get(key);
	 	String b = Integer.toString(a);
	 	// System.out.println(input_hm.get(key).getClass().getName());

	 	JSONObject temp = (JSONObject) wires.get(key);
	 	String garbleVal = temp.get(b).toString();

	 	garbleInputs.put(key, garbleVal);

	 }

	 //writing the key "inputs" and it's values
	 garbledCircuit.put("inputs", garbleInputs);

	 //writing the key "wires" and it's values
	 garbledCircuit.put("wires", wires);

	 //writing the final garbledCircuit to file
	 String segs[] = fileName.split("/");
	 String finalFileName = "gar_"+ segs[segs.length - 1];
	 try (FileWriter file = new FileWriter(finalFileName)) {
	    	file.write(garbledCircuit.toString(4));
	    }

	}

	    
	public static void main(String[] args) throws Exception {
		String FILENAME = args[0];

		Generator generator = new Generator(FILENAME);
		generator.parse_circuit();
	}
}
