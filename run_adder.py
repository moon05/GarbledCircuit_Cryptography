#!/usr/bin/env python
import json,sys
from collections import OrderedDict
import subprocess

'''
  -This script takes file name as an argument but provide without extension.
  -It will help you to set inputs of the simple circuit JSON file.
  -It will set inputs for bob and alice both but converting given digits into their binary bits.
  -It assumes inputs for Aice and Bob are specified alternatively. 
  -For example consider inputs in circuit.json file:
  		  "inputs":{
   			"w1":1,
    			"w2":1,
    			"w3":1,
    			"w4":0
  }
	This script will consider w1,w3 as inputs of alice and w2,w4 of bob.
	w1,w2 are least significat bits respectively.

'''

# Usage:
#   ./run_adder.py <./32adder.json> <x> <y>
#
# Sets the input based on two input numbers, x=argv[2], y=argv[3]

if len(sys.argv) < 4:
    print "Usage: ./run_adder <./32adder.json> <x> <y>"
    sys.exit(1)
             
file_name= sys.argv[1]
A = int(sys.argv[2])
B = int(sys.argv[3])

if not file_name.endswith(".json"):
    print "File doesn't end with .json, probably a mistake!"
    sys.exit(1)

with open(file_name) as data_file:    
    data = json.load(data_file, object_pairs_hook=OrderedDict)

total_inp_size=len(data['inputs'])
max_inp=2**(total_inp_size/2)-1

print "Total input wires are: "+ str(total_inp_size)
print "Max input should not be more than: "+ str(max_inp)


if A > max_inp:
    print "x input (%d) is too high, must be less than" % A, max_inp
    sys.exit(1)

if B > max_inp:
    print "y input (%d) is too high, must be less than" % B, max_inp

# Format the input as a binary string, padded to 32 bits
b= "{0:b}".format(A)
for r in range((total_inp_size/2)-len(b)):
	b='0'+b
inputX =b[::-1] # Reverse the order

# Format the input as a binary string, padded to 32 bits
b= "{0:b}".format(B)
for r in range((total_inp_size/2)-len(b)):
	b='0'+b
inputY =b[::-1] # Reverse the order


# Set the inputs in the JSON object to each bit, and write the output file
# Input wires for the adder circuit are in the format:
#   x0 y0 x1 y1 x2 y2 ... ... xN yN
j=0
for i in range(0,total_inp_size/2):
	data['inputs'][data['inputs'].keys()[j]]=int(inputX[i])
	j+=1
	data['inputs'][data['inputs'].keys()[j]]=int(inputY[i])
	j+=1

with open(file_name, 'w') as outfile:
	json.dump(data, outfile, indent=4)

# Compute the expected output
C=A+B
b= "{0:b}".format(C)
print 'output of circuit should be '+ b

# Run the process
cmd = 'mvn exec:java -Dexec.mainClass="com.applied_crypto.Circuit" -Dexec.args="%s"' % file_name
print "Executing command:", cmd
try:
    output = subprocess.check_output(cmd, shell=True)
    if 'ERROR' in output:
        print 'Build Error'
        print output
        sys.exit(1)
    # Build a regular expression to condense the output
    # Output looks like:
    #s00=0, s01=0, s02=0, s03=1, s04=1, s05=0, s06=1, s07=0, s08=0, s09=0, s10=0, s11=0, s12=0, s13=0, s14=0, s15=0, s16=0, s17=0, s18=0, s19=0, s20=0, s21=0, s22=0, s23=0, s24=0, s25=0, s26=0, s27=0, s28=0, s29=0, s30=0, s31=0
    import re
    print ''.join(re.findall("=(\d+)", output)[1:][::-1])
except subprocess.CalledProcessError as grepexc:
    print grepexc.output
    sys.exit(1)
