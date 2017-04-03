import java.io.*;
import java.util.*;
import java.util.regex.*;

class SuggestSpelling {

	private final HashMap<String, Integer> DataBaseWords = new HashMap<String, Integer>();

	/**
	 * Constructor that reads the dictionary and checks for probability through word
	 * occurrences
	 */
	public SuggestSpelling(String file) throws IOException {
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			Pattern p = Pattern.compile("\\w+");
			// Reading the dictionary and updating the probabilistic values
			// accordingly
			for (String temp = ""; temp != null; temp = in.readLine()) {
				Matcher m = p.matcher(temp.toLowerCase());
				while (m.find()) {
					// This will serve as an indicator to probability of a word
					DataBaseWords.put((temp = m.group()),
							DataBaseWords.containsKey(temp) ? DataBaseWords.get(temp) + 1 : 1);
				}
			}
			in.close();
		} catch (IOException e) {
			System.out.println("Oh no! An exception occured!");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Method that returns an array containing all possible corrections to the
	 * word passed.
	 * 
	 */
	private final List<String> edits(String word) {
		
		List<String> result = new ArrayList<>();
		for (int i = 0; i < word.length(); ++i) {
			result.add(word.substring(0, i) + word.substring(i + 1));
		}
		for (int i = 0; i < word.length() - 1; ++i) {
			result.add(word.substring(0, i) + word.substring(i + 1, i + 2) + word.substring(i, i + 1)
					+ word.substring(i + 2));
		}
		for (int i = 0; i < word.length(); ++i) {
			for (char c = 'a'; c <= 'z'; ++c) {
				result.add(word.substring(0, i) + String.valueOf(c) + word.substring(i + 1));
			}
		}
		for (int i = 0; i <= word.length(); ++i) {
			for (char c = 'a'; c <= 'z'; ++c) {
				result.add(word.substring(0, i) + String.valueOf(c) + word.substring(i));
			}
		}
		return result;
	}

	/**
	 * 
	 * Method that compares input to dictionary words and returns words that are
	 * correct while checking for corrections on the others
	 * 
	 */
	public final String correct(String word) {
		if (DataBaseWords.containsKey(word)) {
			return word; // this is a perfectly safe word.
		}
		List<String> list_edits = edits(word);
		// put all possible typos into a HashMap to sort them quickly
		HashMap<Integer, String> candidates = new HashMap<Integer, String>();

		for (String s : list_edits) // Iterating through the list of all
									// possible corrections to the word.
		{
			if (DataBaseWords.containsKey(s)) {
				candidates.put(DataBaseWords.get(s), s);
			}
		}
		// In the first stage of error correction, any of the possible
		// corrections are found in our word database
		// DataBaseWords
		// then we return the most probably correction
		if (candidates.size() > 0) {
			return candidates.get(Collections.max(candidates.keySet()));
		}
		// In the second stage we apply the first stage method on the possible
		// collections of the list_edits.By the second stage statistics

		for (String s : list_edits) {
			for (String w : edits(s)) {
				if (DataBaseWords.containsKey(w)) {
					candidates.put(DataBaseWords.get(w), w);
				}
			}
		}

		return candidates.size() > 0 ? candidates.get(Collections.max(candidates.keySet()))
				: "This word is not in our dictionary.";
	}

	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			System.out.println((new SuggestSpelling("wordprobabilityDatabase.txt")).correct(args[0]));
		}
	}
}
