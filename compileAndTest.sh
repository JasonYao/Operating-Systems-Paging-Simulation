#!/bin/bash -e

testNumber="01"

# Checks whether or not the output file is created, creates one if not
if [ ! -d output ]; then
	mkdir output
	echo "Creating output directory"
fi

javac src/*.java -d output
input=$(cat testing/input/input-$testNumber)

java -cp output SimulatePagingLab --show-random $input
#cat testing/output/output-$testNumber
