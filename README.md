Instructions for running
========================

## To build all the java code
```
mvn compile
```

## To evaluate a simple circuit JSON file on sample inputs
```
mvn exec:java -Dexec.mainClass="com.applied_crypto.Circuit" -Dexec.args="./circuit.json"
```

If everything went well, you should expect to see the following output (in between a lot of noisy `[INFO]` lines from the build tool):
```
reading circuit...
[{w7=0}]
```
This means the output wire named `w7` has a 0 value.

You can modify the `circuit.json` file to try out different input values.

## Testing the 32-bit Adder circuit
We provide a python script, run_adder.py, to help you evaluate the example circuit (32-bit adder) on numeric inputs. You provide it with the name of the JSON file, and two numeric inputs (decimal numbers). The python script converts the decimal numbers to bits, and writes the bits into the JSON file.
```
python run_adder.py <./32adder.json> <x> <y>
```

## To execute the Garbled Circuit Evaluator
As you implement your tasks in `Evaluator.java`, you can use the following command to execute Bob's role, the circuit evaluator (this will print an error until you implement something!):
```
mvn exec:java -Dexec.mainClass="com.applied_crypto.Evaluator" -Dexec.args="./gar_circuit.json"
```
We include the `gar_circuit.json` as an example of a garbled circuit JSON file generated from our reference implementation. After you implement your own Garbled Circuit Evaluator, you can change this filename to point to your own generated files as well.


## To execute the Garbled Circuit Generator
As you implement your tasks in `Generator.java`, you can use the following command to execute Alice's role, the circuit generator (this will print an error until you implement something!):
```
mvn exec:java -Dexec.mainClass="com.applied_crypto.Generator" -Dexec.args="./circuit.json"
```


## To test your generated circuit files with the reference evaluator
We provide a precompiled (jar file) implementation of the garbled circuit evaluator. You can run this to check if your garbled circuit implementation matches ours exactly.
```
java -jar gc-reference-evaluator.jar ./gar_circuit.json
```


