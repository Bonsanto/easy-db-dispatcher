package pack;

/**
 * Created by Bonsanto on 5/4/2015.
 */
public class Query {
	private String sentence;

	public String getSentence() {
		return sentence;
	}

	//Gets the query and cleans the query, removing the tabulators, and extra spaces.
	public void setSentence(String sentence) {
		this.sentence = sentence.replaceAll("\t*|\n*| +", " ");
	}

	//Counts the number of parameters in the array of transactions.
	public int countParameters() {
		int number = 0;

		//Counts the number of ? symbols in all the queries of this transaction.
		for (int i = 0; i < this.sentence.length(); i++) {
			if (this.sentence.charAt(i) == '?')
				number++;
		}
		return number;
	}

	public Query(String sentence) {
		this.sentence = sentence;
	}

	public Query() {
	}
}
