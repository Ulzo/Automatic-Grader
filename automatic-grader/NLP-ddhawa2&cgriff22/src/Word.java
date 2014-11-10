
public class Word {
private String word;
private String tag;
int person;
int tense;
int subject;

	Word(String w){
		
		String[] token = w.split("/");
		word = token[0];
		setTag(token[1]);
	}
	
public String getWord(){
	
	return this.word;
}

public String getTag(){
	return this.tag;
}

public void setWord(String w){
	word = w;
}

public void setTag(String t){
	tag = t;
	String[] subject_plural = {"NNPS", "NNS"};
	String[] subject_sing = {"NN" , "NNP"};
	
//	String[] verb_plural;
//	String[] verb_sing;
	String[] verb_present = {"VBG", "VBP"};
	String[] verb_past = {"VBD", "VBN"};
	String[] verb_third = {"VBZ"};
	String[] prep_sing = {"His", "Her", "my", "mine", "your", "yours", "thy", "hers", "herself"
						, "him", "himself", "hiself", "it", "itself", "me", "myself", "one", "oneself"
						, "ownself", "self", "she", "thee", "thou"};
	String[] prep_plural = {"ours", "ourselves", "theirs", "them", "themselves", "they", "us", "our", 
							"their"};
	
	String[] prep1 = {"Me", "mine", "my", "myself" , "us", "our", "ours"};
	String[] prep2 = {"you", "your", "yours"};
	
	for(String s: subject_plural){
		if(s.equals(t)){
			subject = 2;
			//System.out.println("Plural Subject");
		}
	}
	for (String s: subject_sing){
		if (s.equals(t))
			subject = 1;
	}
	//check for a plural verb
	/*String a = "es";
		if((t.equalsIgnoreCase("are")) || (t.equalsIgnoreCase("have"))
				||( t.endsWith(a))){
			verb  =2;
		}else {
			verb = 1;
		}
			*/
	for (String s: verb_present){
		if(s.equals(t)){
			tense = 2;
		}
	}
	
	
	
	for(String s: prep_sing){
		if(t.equalsIgnoreCase(s))
			subject = 1;
	}
	for (String s: prep_plural){
		if (t.equalsIgnoreCase(s))
			subject = 2;
	}
	for (String s: prep1){
		if (s.equalsIgnoreCase(t))
			person = 1;
	}
	for (String s: prep2){
		if(s.equalsIgnoreCase(t)){
			person = 2;
		}
	}
	if ((t.equalsIgnoreCase("PRP")) ||( t.equalsIgnoreCase("PRP$"))){
		if ((person != 1) && (person !=2)){
			person = 3;
		}
	}
}

public Integer getPerson (){
	return this.person;
}
public Integer getVerb(){
	return this.tense;
}
public Integer getSubject(){
	return this.subject;
}
public String toString (){
	return word + "/" + tag ;
}
}

