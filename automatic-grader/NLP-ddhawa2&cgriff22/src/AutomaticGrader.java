import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.io.*;

import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

public class AutomaticGrader {
	static int count;
	public static void main(String args[]) {
		String f = "";
		File[] files = null;
		//BufferedReader fileReader = null;
		//System.out.println("Please put in the WHOLE address for the directory which houses your text files:");
		String userin = null;
		
		//userin = new BufferedReader(new InputStreamReader(
					//System.in));
		userin = args[0];
			files = new File(userin).listFiles();
		
		
		String userOutput = userin+"/output.txt";
		
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(userOutput, true));
			out.println("1a" + "," + "1b" + "," + "1c" + "," + "1d" + "," + "2a" + "," + "2b" + "," + "3a");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		showFiles(files,out);
		
		out.close();
		System.out.println("Beware that our files are sorted alphabetically, therefore file 12 would come before " +
				"file 2.");
		System.out.println("Your output is in the path you specified at the start. :D");


	}
	public static void showFiles(File[] files,PrintWriter out) {
		
		BufferedReader fileReader = null;
		
		for (File file : files) {
			
			
				if ( file.getName().contains(".txt") && !file.getName().contains("readme") && !file.getName().contains("output")){
					List<String> sentenceList = new LinkedList<String>();
					ArrayList<ArrayList<Word>> sentences = new ArrayList<ArrayList<Word>>();
					//Take user input and process it as a file name
					String paragraph = "";
					System.out.println("File: " + file.getName());
					try {
						BufferedReader userin = new BufferedReader(new InputStreamReader(
								System.in));
						String inputFile = file.getName();
						File filed = file;
						fileReader = new BufferedReader(new FileReader(filed));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					//Read the file into s
					try {
						String s = "";
						while (fileReader.ready()) {

							s = fileReader.readLine();
							paragraph += s;
							paragraph += ".";
						}
						// System.out.println(s);
					} catch (IOException e) {
						e.printStackTrace();
					}

					//Split sentences according to delimiters
					String[] punc = { ".", "\\n", "\r" };
					Reader reader = new StringReader(paragraph);
					DocumentPreprocessor dp = new DocumentPreprocessor(reader);
					dp.setSentenceFinalPuncWords(punc);
					Iterator<List<HasWord>> it = dp.iterator();
					while (it.hasNext()) {
						StringBuilder sentenceSb = new StringBuilder();
						List<HasWord> sentence = it.next();
						for (HasWord token : sentence) {
							if (sentenceSb.length() >= 1) {
								sentenceSb.append(" ");
							}
							sentenceSb.append(token);
						}
						sentenceList.add(sentenceSb.toString());
					}

					//Run all the sentences through the parser, getting the words and setting their tags
					LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
					List<TaggedWord> taggedWords = null;
					for (int i = 0; i < sentenceList.size(); i++) {
						List<CoreLabel> rawWords = Sentence.toCoreLabelList(sentenceList
								.get(i));
						Tree parse = lp.apply(rawWords);

						TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer
								.factory(new CoreLabelTokenFactory(), "");
						List<CoreLabel> rawWords2 = tokenizerFactory.getTokenizer(
								new StringReader(sentenceList.get(i))).tokenize();
						parse = lp.apply(rawWords2);
						taggedWords = parse.taggedYield();

						ArrayList<Word> temp = new ArrayList<Word>();
						for (int k = 0; k < taggedWords.size(); k++) {
							String to_tag = taggedWords.get(k).toString();
							Word w = new Word(to_tag);
							temp.add(w);
						}
						sentences.add(i, temp);
					}

					//This method takes capitalization into account with the parsing
					ArrayList<ArrayList<Word>> newList = furtherParse(sentences);

					//Calculates and prints final grades
					finalGrade(newList, paragraph, file.getName(), out);
				}
			}
		}
	
	
	// This method checks for correct word order
	private static int grade1A(ArrayList<ArrayList<Word>> arr_sentences)
	{
		//Start retval off as 5 or number of sentences
		double retval = 5.0;
		if(arr_sentences.size() < 6)
			retval = arr_sentences.size();

		//Run through each sentence
		for (int i = 0; i<arr_sentences.size() ; i++){
			//For each word in the sentence
			for(int j=1; j<arr_sentences.get(i).size(); j++)
			{
				//ignore periods
				if(arr_sentences.get(i).get(j).getTag().equalsIgnoreCase("."))
				{
					break;
				}
				//if DET is not followed by noun or adjective, deduct points
				else if(arr_sentences.get(i).get(j-1).getTag().equalsIgnoreCase("DET"))
				{
					if( !(arr_sentences.get(i).get(j).getTag().startsWith("NN") || arr_sentences.get(i).get(j).getTag().startsWith("JJ")) )
					{
						retval=retval-0.25;
					}
				}
				//if adjective is not followed by noun or adjective, deduct points
				else if(arr_sentences.get(i).get(j-1).getTag().startsWith("JJ"))
				{
					if( !(arr_sentences.get(i).get(j).getTag().startsWith("NN") || arr_sentences.get(i).get(j).getTag().startsWith("JJ")) )
					{
						retval=retval-0.25;
					}
				}
				//if adverb is not followed by adverb, verb, or adjective, deduct points
				else if(arr_sentences.get(i).get(j-1).getTag().startsWith("RB"))
				{
					if( !(arr_sentences.get(i).get(j).getTag().startsWith("RB") || arr_sentences.get(i).get(j).getTag().startsWith("VB") || arr_sentences.get(i).get(j).getTag().startsWith("JJ")) )
					{
						retval=retval-0.25;
					}
				}
				//if noun is followed by an adjective or preposition, deduct points
				else if(arr_sentences.get(i).get(j-1).getTag().startsWith("NN"))
				{
					if(arr_sentences.get(i).get(j).getTag().startsWith("JJ") || arr_sentences.get(i).get(j).getTag().startsWith("PRP"))
					{
						retval=retval-0.25;
					}
				}
			}
		}

		//if lost more points than possible
		if(retval < 0)
			retval = 0;

		//round to nearest whole number
		int returnval = (int) Math.round(retval);

		System.out.print("1A: " + returnval + "   ");
		return returnval;
	}

	//This method checks for subject-verb agreement
	public static int grade1B(ArrayList<ArrayList<Word>> arr_sentences) {
		double initialPoints = 0.0;
		int finalPoints = 0;

		String[] count1 = {"NN", "NNS", "PRP", "PRP", "PRP", "NN", "NNP", "PRP", "PRP", "PRP", "PRP", "NNP", "PRP", "NNS"};
		String[] count2 = {"VBZ", "VBZ", "VBG", "VBP", "VBP", "VBP", "VBZ", "VBD", "VB", "VB", "VBZ", "VBP", "VBP", "VBP"};

		//Loop through all the sentences
		for(int a = 0; a<arr_sentences.size(); a++){
			//loop through all the words in the sentence
			for (int b = 0; b <arr_sentences.get(a).size(); b++) {

				if ( (b+1)> (arr_sentences.get(a).size()-1) ){break;}
				if ( (b+2)> (arr_sentences.get(a).size()-1) ){break;}

				//comment here
				if ((arr_sentences.get(a).get(b).getTag().equals(count1[0])) && (arr_sentences.get(a).get(b+1).getTag().equals(count2[0]))) {
					initialPoints += 0.75;
				}
				//comment here
				else if ((arr_sentences.get(a).get(b).getTag().equals(count1[1])) && (arr_sentences.get(a).get(b+1).getTag().equals(count2[1]))){
					initialPoints += 0.75;
				}
				//comment here
				else if ((arr_sentences.get(a).get(b).getTag().equals(count1[2])) && (arr_sentences.get(a).get(b+1).getTag().equals(count2[2]))) {
					initialPoints += 0.2;
				}
				//comment here
				else if ((arr_sentences.get(a).get(b).getTag().equals(count1[3])) && (arr_sentences.get(a).get(b+1).getTag().equals(count2[3]))
						&& (arr_sentences.get(a).get(b+2).getTag().equals("VBD"))) {
					initialPoints += 0.3;
				}
				//comment here
				else if ((arr_sentences.get(a).get(b).getTag().equals(count1[4])) && (arr_sentences.get(a).get(b+1).getTag().equals(count2[4]))
						&& (arr_sentences.get(a).get(b+2).getTag().equals("VBD"))) {
					initialPoints += 0.35;
				}
				//comment here
				else if ((arr_sentences.get(a).get(b).getTag().equals(count1[5])) && (arr_sentences.get(a).get(b+1).getTag().equals(count2[5]))) {
					initialPoints += 0.1; //Negative points since we're grading for this
				}
				//comment here
				else if ((arr_sentences.get(a).get(b).getTag().equals(count1[6])) && (arr_sentences.get(a).get(b+1).getTag().equals(count2[6]))) {
					initialPoints += 0.5;
				}
				//comment here
				else if ((arr_sentences.get(a).get(b).getTag().equals(count1[7])) && (arr_sentences.get(a).get(b+1).getTag().equals(count2[7]))) {
					initialPoints += 0.7;
				}
				//comment here
				else if ((arr_sentences.get(a).get(b).getTag().equals(count1[8])) && (arr_sentences.get(a).get(b+1).getTag().equals(count2[8]))) {
					initialPoints += 0.7;
				}
				//comment here
				else if ((arr_sentences.get(a).get(b).getTag().equals(count1[9])) && (arr_sentences.get(a).get(b+2).getTag().equals(count2[9]))) {
					initialPoints += 0.7;
				}
				//comment here
				else if((arr_sentences.get(a).get(b).getTag().equals(count1[10])) && (arr_sentences.get(a).get(b+1).getTag().equals(count2[10]))) {
					initialPoints += 0.35;
				}
				//comment here
				else if ((arr_sentences.get(a).get(b).getTag().equals(count1[11])) && (arr_sentences.get(a).get(b+1).getTag().equals(count2[11]))) {
					initialPoints += 0.35;
				}
				//comment here
				else if ((arr_sentences.get(a).get(b).getTag().equals(count1[12])) && (arr_sentences.get(a).get(b+1).getTag().equals(count2[12]))) {
					initialPoints += 0.7;
				}
				//comment here
				else if ((arr_sentences.get(a).get(b).getTag().equals(count1[13])) && (arr_sentences.get(a).get(b+1).getTag().equals(count2[13]))) {
					initialPoints += 0.7;
				}
				else{continue;}
			}
		}//end for loop

		//If more than max points accumulated, give max
		if (initialPoints > 5) {
			System.out.print("1B: " + 5+ "  ");
			return 5;
		}
		//Round down to nearest whole number
		else {
			finalPoints = (int)Math.floor(initialPoints + 0.5);
			System.out.print("1B: " + finalPoints + "       ");
			return finalPoints; 
		}
	}

	// This method checks if the verb tenses correct
	private static Integer grade1C(ArrayList<ArrayList<Word>> arr_sentences)
	{
		int score = 0;
		int retval = 0;
		String[] verb = {"VBG", "VBP", "VBD", "VBN", "VBZ", "VB"};
		String[] aux = {"am", "be", "are", "is", "was", "were", "being", "care", "could"
				, "do", "does", "did", "have", "has", "had", "having", "may", "might",
				"must", "need", "outght", "shall", "should", "will", "would"};

		//Loop through the number of sentences
		for (int i = 0; i<arr_sentences.size(); i++){
			//Loop through the words in the sentence
			for (int j = 0; j<arr_sentences.get(i).size(); j++){
				//loop through the verb tags
				for (int u =0 ;u<verb.length;u++){
					//if the word is a verb
					if (arr_sentences.get(i).get(j).getTag().equals(verb[u])){
						//loop through the auxiliary verbs
						for (int k =0; k<aux.length;k++){
							//if it is an auxiliary verb
							if (arr_sentences.get(i).get(j).getWord().equalsIgnoreCase(aux[k])){
								//check if the next word is a verb
								if (!((j+1)< (arr_sentences.get(i).size()-1) 
										&&(arr_sentences.get(i).get(j+1).getTag().startsWith("VB")))){
									//auxiliary has a verb following it
									score++;
								}

							}
						}
					}
				}
			}
		}

		//if accumulated score is greater than the maximum possible score, max at 5
		if (score > 5){
			retval = 5;
		}else{
			retval = score;
		}

		System.out.print("1C: " + retval + "    ");
		return retval;
	}

	// Check if are sentences formed correctly
	private static Integer grade1D(ArrayList<ArrayList<Word>> arr_sentences, String paragraph) {
		int retval = 5;
		int numbad = 0;

		//Make a stack to parse for matching parens
		Stack<String> brackets = new Stack<String>();

		//for each character in the paragraph
		for(int i = 0; i < paragraph.length(); i++)
		{
			char c = paragraph.charAt(i);
			if(c == '(')
					{
				//add this to the stack
				brackets.push(paragraph.substring(i, paragraph.indexOf(' ', i)));
					}
			else if(c == ')')
			{
				//check if this is an SBAR
				if(brackets.peek().contains("SBAR"))
				{
					brackets.pop();
					//if parent is not a VP or S, SBAR is wrong
					if( !( (brackets.peek().equals("(VP")) || (brackets.peek().equals("(S")) ))
					{
						numbad++;
					}
				}
				else
				{
					brackets.pop();
				}
			}
		}

		//calculate score based on fragments
		double val = (double) numbad/arr_sentences.size() * 5;
		val = 5 - val;
		retval = (int) Math.round(val);

		if(retval < 0)
			retval = 0;
		if(retval > 5)
			retval = 5;

		System.out.print("1D: " + retval + "    ");
		return retval;
	}

	// Check if the essay is coherent / if it makes sense
	private static Integer grade2A(ArrayList<ArrayList<Word>> arr_sentences, String paragraph) {
		String[] third_pronoun = {"he", "she", "them", "his", "her", "they", "their"};
		
		int retval = 5;
		count = 0;
		int bad = 0;
		int male = 0;
		int female = 0;
		for (int i = 0; i<arr_sentences.size(); i++){
			for(int j = 0; j<arr_sentences.get(i).size(); j++){
				for (int k = 0; k<third_pronoun.length; k++){
					String word = arr_sentences.get(i).get(j).getWord();
					
					if (word.equalsIgnoreCase(third_pronoun[k])){
						count++;
					}
					if ((word.equalsIgnoreCase("he")) || (word.equalsIgnoreCase("him")) || (word.equalsIgnoreCase("his"))  ){
						male++;
					}
					if ((word.equalsIgnoreCase("she")) || (word.equalsIgnoreCase("her")) || (word.equalsIgnoreCase("hers")) ){
						female++;
					}
					
					
				}			
			}
		}
		
		if ((male > 0) && (female>0)){
			bad++;
		}
		if (count == 1){
			
			retval = 2-bad;
			
		}
		if (count == 2){
			retval = 3-bad;
		}
		if(count == 3){
			retval = 4-bad;
		}
		
		
		return retval;
	}

	// Check if the essay addresses the topic
	private static Integer grade2B(ArrayList<ArrayList<Word>> arr_sentences) {
		// look for words: name, live, work, school, like, born, learn, parents, etc
		int foundWords = 0;
		int retval = 0;

		// Add to corpus if required.
		String[] searchWords = { "name", "live", "work", "school", "from",
				"born", "learn", "parent", "brother", "sister", "mom",
				"mother", "dad", "father", "lives", "works", "learns",
				"parents", "brothers", "sisters", "cousin", "cousins",
				"aunt", "aunts", "uncle", "uncles", "family" }; //I my like they them neighbor

		//Loop through sentences
		for (int i = 0; i < arr_sentences.size(); i++) {
			//Loop through words in sentences
			for (int f = 0; f < arr_sentences.get(i).size(); f++) {
				//Loop through search words
				for (int j = 0; j < searchWords.length; j++) {
					//if the word equals a search word
					if (arr_sentences.get(i).get(f).getWord()
							.equalsIgnoreCase(searchWords[j])) {
						foundWords++;
					}
				}
			}
		}
		//System.out.println();
		//System.out.println(foundWords + " found");

		//Give full points if 4 "family" words are used
		if(foundWords >= 4)
		{
			retval = 5;
		}
		//if there's fewer than 4 sentences, but most of the sentences have a family word
		else if( ((double)(foundWords / arr_sentences.size())) > 0.75)
		{
			if(foundWords >= 3)
				retval = 5;
		}
		//otherwise, give them what they got
		else 
		{
			retval = foundWords;
		}
		System.out.print("2B: " + retval + "    ");
		return retval;
	}

	// Check if the length of the essay is appropriate
	private static Integer grade3A(ArrayList<ArrayList<Word>> arr_sentences) {
		int retval = 0;
		int conj = 0;

		//Loop through sentences
		for (int i = 0; i < arr_sentences.size(); i++) {
			//Loop through words in sentence
			for (int f = 0; f < arr_sentences.get(i).size(); f++) {
				//if the tag is a conjunction, add to conjunction counter
				String tag = arr_sentences.get(i).get(f).getTag();
				if ((tag.equals("CC"))) {
					conj++;
				}
			}
		}

		// full credit if 6 sentences or longer, partial credit otherwise
		int total = arr_sentences.size() + conj;
		if (total >= 6) {
			retval = 5;
		}else if(total ==5){
			retval = 4;
		}else if(total == 4){
			retval = 3;
		}else if(total ==3){
			retval = 2;
		}else if (total == 2){
			retval = 1;
		}else if (total <2){
			retval = 0;
		}

		System.out.println("3A: " + retval);
		return retval;
	}

	// Calculates and returns grade of essay
	private static double finalGrade(ArrayList<ArrayList<Word>> arr_sentences, String paragraph, String fileName
										,PrintWriter out) {
		String parseTree = tree(paragraph);
		
		
	//	System.out.println("Component grades: ");
		// calculate all component grades
		int a1 = grade1A(arr_sentences);
		int b1 = grade1B(arr_sentences);
		int c1 = grade1C(arr_sentences);
		int d1 = grade1D(arr_sentences, parseTree);
		int a2 = grade2A(arr_sentences, parseTree);
		int b2 = grade2B(arr_sentences);
		int a3 = grade3A(arr_sentences);

		// calculate final grade, normalize, and round
		double grade = (a1 + b1 + c1 + (2 * d1) + a2 + (3 * b2) + a3);
		grade = grade / 10;
		grade = (double) (long) (grade * 2 + 0.5) / 2;

			out.println(a1 + "," + b1 + "," + c1 + "," + d1 + "," + a2 + "," + b2 + "," + a3);
		//out.println("Filename: " + fileName + "count" + count + " 2a: " + a2)	;
		
		System.out.println("Final grade: " + grade);
		System.out.println();
		System.out.println();
		
		
		return grade;
	}

	//This method parses for capitalization
	static ArrayList<ArrayList<Word>> furtherParse(ArrayList<ArrayList<Word>> arr) {
		ArrayList<ArrayList<Word>> retSentences = new ArrayList<ArrayList<Word>>();
		boolean newSentence = false;

		//Loop through input
		for (int i = 0; i < arr.size(); i++) {
			for (int f = 0; f < arr.get(i).size(); f++) {
				char firstChar = arr.get(i).get(f).getWord().charAt(0);
				String tag = arr.get(i).get(f).getTag();
				//If it is a capital letter
				if ((firstChar >= 'A') && (firstChar <= 'Z')) {
					//If it is not a proper noun
					if ((!tag.equals("NNP")) && (!tag.equals("NNPS"))) {
						//Create new array for new sentence
						newSentence = true;
						ArrayList<Word> sent = new ArrayList<Word>();
						retSentences.add(sent);
						retSentences.get(retSentences.size() - 1).add(arr.get(i).get(f));
						continue;
					}
				}
				//Inserts new sentence into new sentence list
				if (newSentence == true) {
					retSentences.get(retSentences.size() - 1).add(arr.get(i).get(f));
				}
			}
		}
		return retSentences;
	}
	private static String tree(String paragraph){
		String grammar = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
		String[] options = { "-maxLength", "80", "-retainTmpSubcategories" };
		LexicalizedParser lp = LexicalizedParser.loadModel(grammar, options);
		TreebankLanguagePack tlp = lp.getOp().langpack();
		//GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();

		Iterable<List<? extends HasWord>> sentences; 
		Tokenizer<? extends HasWord> toke =
				tlp.getTokenizerFactory().getTokenizer(new StringReader(paragraph));
		List<? extends HasWord> sentence2 = toke.tokenize();
		List<List<? extends HasWord>> tmp =
				new ArrayList<List<? extends HasWord>>();
		tmp.add(sentence2);
		sentences = tmp;
		String parseLong = "";
		for (List<? extends HasWord> sentence : sentences) {
			Tree parse = lp.parse(sentence);
			parseLong += parse.pennString();
		}

		//System.out.println(parseLong);
		return parseLong;
	}
}