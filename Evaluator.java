package com.applied_crypto;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import org.json.*;
import com.applied_crypto.Evaluator_Gate;

class Evaluator {

	private BufferedReader br;
	private String current_line = "";
	private String whole_file = "";
	private JSONObject circuit;
	private JSONObject gates;
	private JSONObject inputs;


	private ArrayList<Evaluator_Gate> ev_gates_list = new ArrayList<Evaluator_Gate>();
	private HashMap input_hm = new HashMap();



	public Evaluator(String FILENAME) throws Exception {
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

	public void create_circuit() throws Exception {

		/**
		 *
		 * This function will read gates objects from the input JSON
		 * file. First we try to read the gates object and throws
		 * an error otherwise.
		 *
		 */

		try {
			gates = (JSONObject)  circuit.get("gates");
		} catch (JSONException ex) {
			System.out.println("gates is not JSON");

		}

		/**
		 *
		 * After reading the gates object we iterate over all the
		 * gates inside it.
		 *
		 */

		for (Iterator iterator = gates.keys(); iterator.hasNext();) {

			/*
			Here we have shown how to read a single gate object from
			gates. Your code will go here.

			FOLLOWING IS THE TODO LIST FOR YOU:
				- Read Inputs and Outputs of the given gate
				- Read Table of the given gate
				- Store gate attributes inside a list or data structure.


			HINTS:
				- Inputs, Outputs and Gate Table can be read using .get() method.
				- Gate data structure can be a list of Evaluator_Gate objects (see Evaluator_Gate.java).

			 */

			String key = (String) iterator.next();
			JSONObject gate = (JSONObject) gates.get(key);
			//added now
			ArrayList<String> inputs = new ArrayList<String>();
			ArrayList<String> outputs = new ArrayList<String>();
			String[] table = new String[4];

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
				table[i] = ((String)jsonArray.get(i));
			}

			Evaluator_Gate sg = new Evaluator_Gate(inputs.get(0), inputs.get(1), outputs.get(0));
			sg.fill_table(table);
			ev_gates_list.add(sg);


		}


	}

	public void load_inputs() throws Exception {

		/*
		This function will the inputs objects from circuit JSON.
		Given code parses over all the inputs.
		Your code will go here.

			FOLLOWING IS THE TODO FOR YOU:
			- Read all the inputs and store in some data structure as key,value

			HINT:
			- You can use jave treemap or hashmap for inputs. This will
			be useful to you inside evaluate_circuit
		 */
		inputs = (JSONObject)  circuit.get("inputs");

		for (Iterator iterator = inputs.keys(); iterator.hasNext();) {
			String key = (String) iterator.next();
			String  value = (String) inputs.get(key);
			input_hm.put(key, value);
		}



	}

	public void evaluate_circuit() throws Exception {

		/*

		This function will implements the main logic of evaluating each gate
		based on given inputs. You will need to parse data structure from
		create_circuit and parse it using inputs that you have read in load_inputs.


			FOLLOWING IS THE TODO LIST FOR YOU:
				- Parse all the gates
				- For each gate, evaluate it using both inputs
				- Save the value of gate output with its output wire name
				- Once all gates have been evaluated print the values of
				output wires. This will be output of the circuit.
			HINT:
				- Gate will only be evaluated if value for
				both if it's input wires are given.
				- You can search both inputs of each gate by comparing name
				of it's input wires.


		 */
		Evaluator_Gate temp_gate;
		String [] temp_inputs;
		String [] temp_outputs;

		TreeMap output_hm = new TreeMap();
		String temp_output_val;
		int i = 0;
		while(i < ev_gates_list.size()){
			temp_gate = ev_gates_list.get(i);
			temp_inputs = temp_gate.get_inputs();
			temp_outputs = temp_gate.get_outputs();


			if (input_hm.containsKey(temp_inputs[0]) && input_hm.containsKey(temp_inputs[1])) {
				temp_output_val = temp_gate.decrypt_table((String)input_hm.get(temp_inputs[0]), (String) input_hm.get(temp_inputs[1]));
				//input_hm.remove(temp_inputs[0]);
				//input_hm.remove(temp_inputs[1]);
				output_hm.remove(temp_inputs[0]);
				output_hm.remove(temp_inputs[1]);


				output_hm.put(temp_outputs[0], temp_output_val);
				input_hm.put(temp_outputs[0], temp_output_val);
				ev_gates_list.remove(ev_gates_list.get(i));
				i = 0;

			} else {
				i = i + 1;
			}
		} 

	    System.out.println(Arrays.asList(output_hm));

	}

	public static void main(String[] args) throws Exception {

		/**
		 *
		 * This is the main file for evaluator. You should not have
		 * make changes in this function.
		 */

		String FILENAME = args[0];

		Evaluator circuit = new Evaluator(FILENAME);
		circuit.create_circuit();
		circuit.load_inputs();
		circuit.evaluate_circuit();
	}
}


