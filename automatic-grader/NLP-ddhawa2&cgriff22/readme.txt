Project 1 for CS 421 - University of Illinois at Chicago
Devina Dhawan ddhawa2@uic.edu
Chantiny Griffith cgriff22@uic.edu
--------------------------------------------------------
--->SETUP<----------------------------------------------

1. Unzip file
2. Load Eclipse
3. File -> Import -> Existing Projects Into Workspace
4. Select wherever you unzipped the file, click finish
5. Right click on JRE System Library
	Build Path -> Configure Build Path
	Make sure the four "stanford-parser*.jar" paths are
	connected to their corresponding files. Click on 
	each one and press 'edit' then select the correct 
	filepath and click 'ok'.
	
--------------------------------------------------------
--->INPUT<----------------------------------------------

Upon running our program in eclipse, the console will 
prompt you to enter a path name. You should then enter
the name of the path, the full path, for example
 "C:\Users\Devina\Desktop\Ein\repo\NLP-ddhawa2&cgriff22"
 where your files are stored.

--------------------------------------------------------
--->OUTPUT<---------------------------------------------

Our program writes the following to the console/standard
output. (Obviously the X's are replaced with numbers).
The file will iterate through all of your files and give
you all of the output as follows:

File #
1A: X	1B: X	1C: X	1D: X	2A: X	2B: X	3A: X
Final grade: X

There will also be a output.txt file which will be stored 
in the same path you provided.  
--------------------------------------------------------
--->FILES<----------------------------------------------

The files of the essays to be read should be in the
StanfordParserTrial folder, not in a subfolder.

--------------------------------------------------------
--->TECHNIQUE<------------------------------------------

For 1A: We started the score off with full points, then
		deducted 0.25 for each instance of the following:
			If a determinant is not followed by a noun 
				or an adjective
			If an adjective is not followed by another 
				adjective or a noun
			If an adverb is not followed by another
				adverb, a verb, or an adjective
			If a noun is followed by an adjective or
				a preposition
For 1B: We started the score at 0, and awarded points
		for each instance where a noun followed by a
		verb had person and plurality agreement, up to
		5 points maximum.
For 1C: We started the score at 0, and awarded points
		for each instance where an auxiliary verb is
		correctly followed by a verb, up to 5 points
		maximum.
For 1D: We start by counting the number of times bad 
		things happen (ex. SBAR is at the start of the
		sentence or if SBAR is a verb). Then we calculate
		based on the number of poor scores/number of sentences
		to get an accurate idea of the essay grade
For 2A: We rewarded points if the male/female use was correct. 
For 2B: We rewarded points if the essay included any 
		familial terms and if the familial terms are included
		they get points.
For 3A: Our initial parsing for sentences separates
		on the following: . \n \r and capital letters.
		We then count the sentences and add the number 
		of conjunctions to this number. We then score
		any final count above 6 as full points. We give
		partial credit for fewer sentences.
		

--------------------------------------------------------
--->TODO<-----------------------------------------------

We still must implement criteria 2A.
