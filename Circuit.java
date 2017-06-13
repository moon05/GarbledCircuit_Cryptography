package com.applied_crypto;

import com.applied_crypto.Util;
import org.apache.commons.codec.binary.Hex;
import java.util.*;
import org.json.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//import org.json.simple.JSONObject;




public class Circuit {
	private BufferedReader br;
	private String current_line = "";
	private String whole_file = "";
	private JSONObject circuit;
	private JSONObject gates;
	private JSONObject wires;
	private ArrayList<Gate> gates_list = new ArrayList<Gate>();
	
	private ArrayList<Evaluator_Gate> ev_gates_list = new ArrayList<Evaluator_Gate>();
	private HashMap input_hm = new HashMap();

	private JSONObject inputs;

	public Circuit(String FILENAME) throws Exception {
		br = new BufferedReader( new FileReader(FILENAME));
		System.out.println("reading circuit...");
		while (  (current_line = br.readLine()) != null) {
			whole_file = whole_file + current_line;

		}
		try {
			circuit = new JSONObject(whole_file);
		} catch (JSONException ex) {
			System.out.println("Circuit file is not JSON");

		}
	}

	/*
	* code to read simple non encrypted circuit from file and reading gates.
	*/
	public void create_circuit() throws Exception {
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
			}

			jsonArray = (JSONArray) gate.get("out");
			if (jsonArray.length() != 1) {
				throw new Exception("Output of Gate: " + key + " are wrong." );

			}

			for (int i = 0; i < jsonArray.length(); i++) {
				outputs.add(jsonArray.get(i).toString());
			}

			jsonArray = (JSONArray) gate.get("table");

			if (jsonArray.length() != 4) {
				throw new Exception("Table length of Gate: " + key + " is wrong." );

			}

			for (int i = 0; i < jsonArray.length(); i++) {
				table.add((int)jsonArray.get(i));
			}

			Gate sg = new Gate(inputs.get(0), inputs.get(1), outputs.get(0));
			sg.fill_table(table);
			gates_list.add(sg);


		}


	}

	/*
	*code for reading inputs, we do not have OT so inputs are read from json.
	*/
	public void load_inputs() throws Exception {
		inputs = (JSONObject)  circuit.get("inputs");
		for (Iterator iterator = inputs.keys(); iterator.hasNext();) {
			String key = (String) iterator.next();
			Integer  value = (Integer) inputs.get(key);
			input_hm.put(key, value);
		}
	}

	/*
	*code to evaluate circuit on inputs.
	*/
	public void evaluate_circuit() throws Exception {
		Gate temp_gate;
		String [] temp_inputs;
		String [] temp_outputs;

		TreeMap output_hm = new TreeMap();
		int temp_output_val = 0;
		int i = 0;
		while (i < gates_list.size()) {

			temp_gate = gates_list.get(i);
			temp_inputs = temp_gate.get_inputs();
			temp_outputs = temp_gate.get_outputs();

			if (input_hm.containsKey(temp_inputs[0]) && input_hm.containsKey(temp_inputs[1])) {
				temp_output_val = temp_gate.decrypt_table((int)input_hm.get(temp_inputs[0]), (int) input_hm.get(temp_inputs[1]));
				//input_hm.remove(temp_inputs[0]);
				//input_hm.remove(temp_inputs[1]);
				output_hm.remove(temp_inputs[0]);
				output_hm.remove(temp_inputs[1]);


				output_hm.put(temp_outputs[0], temp_output_val);
				input_hm.put(temp_outputs[0], temp_output_val);
				gates_list.remove(gates_list.get(i));
				i = 0;

			} else {
				i = i + 1;
			}


		}
		System.out.println(Arrays.asList(output_hm));
	}

	public static void main(String[] args) throws Exception {
		String FILENAME = args[0];

		Circuit circuit = new Circuit(FILENAME);
		circuit.create_circuit();
		circuit.load_inputs();
		circuit.evaluate_circuit();
	}
}


