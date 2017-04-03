import java.io.*;
import java.util.*;

public class SpellChecker {

	static HashMap<String, String> dictionary;// To store all the words of the
												// dictionary
	
	static boolean suggestWord; // To indicate whether the word is spelled
								// correctly or not.

	public static void main(String[] args) throws IOException {
		System.out.println("Let's get started!");
		System.out.println("Pick an english website to scan.");
		System.out.println("Enter a URL, starting with http://");
		parser.HTMLParser();
		System.out.println("Searching for spelling errors ... ");
		spellChecker(); // Spell check the cleaned page
		System.out.println("Thanks for using the spell checker!");
	}
// create a parser object of the SimpleHTMLParser class
	static SimpleHTMLParser parser = new SimpleHTMLParser();

	Scanner urlInput = new Scanner(System.in);
	

	/**
	 * SPELL CHECKER METHOD
	 */
	public static void spellChecker() throws IOException {
		
		dictionary = new HashMap<String, String>();

		try {
			// Read and store the words of the dictionary
			BufferedReader dictReader = new BufferedReader(new FileReader("dictionary.txt"));

			while (dictReader.ready()) {
				String dictInput = dictReader.readLine();
				String[] dict = dictInput.split("\\s"); // create an array of
														// dictionary words

				for (int i = 0; i < dict.length; i++) {
					// key and value are identical
					dictionary.put(dict[i], dict[i]);
				}
			}
			dictReader.close();
			String userText = "";

			// creates a suggest object of class SuggestSpelling
			SuggestSpelling suggest = new SuggestSpelling("wordprobabilityDatabase.txt");

			// get user input for correction
			{

				userText = parser.getCleanWords();
				String[] words = userText.split(" ");
				// Remove repetitive words by storing them in a HashSet
				Set<String> wordSet = new HashSet<>();
				int error = 0;
				for (String word : words) {
					if (!wordSet.contains(word)) {
						removePunct(word);
						suggestWord = true;
						String outputWord = removePunct(word);
						if (suggestWord) {
							System.out.println(
									"Suggestions for " + word + " are:  " + suggest.correct(outputWord) + "\n");
							error++;
						}
					}
					// If a word appears more than once, store inside the
					// HashSet to avoid re-checking
					wordSet.add(word);
				}

				if (error == 0) {
					System.out.println("No mistakes found");
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * METHOD TO REMOVE PUNCTUATION
	 */

	public static String removePunct(String wordToBeChecked) {
		String wordInDictionary, unpunctWord;
		String wordEntered = wordToBeChecked.toLowerCase();

		// if word is found in dictionary then it is spelled correctly, so
		// return as it is.

		if ((wordInDictionary = dictionary.get(wordEntered)) != null) {
			suggestWord = false; // no need to ask for suggestion for a correct
									// word.
			return wordInDictionary;
		}

		// Removing punctuation at end of word and giving it a shot ("." or "."
		// or "?!")
		int length = wordEntered.length();

		// Checking for the beginning of quotes(example: "she )
		if (length > 1 && wordEntered.substring(0, 1).equals("\"")) {
			unpunctWord = wordEntered.substring(1, length);

			if ((wordInDictionary = dictionary.get(unpunctWord)) != null) {
				suggestWord = false; // no need to ask for suggestion for a
										// correct word.
				return wordInDictionary;
			} else // not found
				return unpunctWord; // removing the punctuation and returning
		}

		// Checking if "." or "," etc. at the end is the problem (example: book.
		// when book is present in the dictionary).
		if (wordEntered.substring(length - 1).equals(".") 
				|| wordEntered.substring(length - 1).equals(",")
				|| wordEntered.substring(length - 1).equals("!") 
				|| wordEntered.substring(length - 1).equals(";")
				|| wordEntered.substring(length - 1).equals(":") 
				|| wordEntered.substring(length - 1).equals(" \" ")
				|| wordEntered.substring(length - 1).equals("\",") 
				|| wordEntered.substring(length - 1).equals("\"."))
		{
			unpunctWord = wordEntered.substring(0, length - 1);

			if ((wordInDictionary = dictionary.get(unpunctWord)) != null) {
				suggestWord = false; // no need to ask for suggestion for a
										// correct word.
				return wordInDictionary;
			} else {
				return unpunctWord; // removing the punctuation and returning it
									// clean
			}
		}

		// Checking for (!,\,",etc) ... in the problem (example: watch!" when
		// watch is present in the dictionary)

		if (length > 2 && (wordEntered.substring(length - 2).equals(",\"")
				|| wordEntered.substring(length - 2).equals(".\"") 
				|| wordEntered.substring(length - 2).equals("?\"")
				|| wordEntered.substring(length - 2).equals("!\"")
				|| wordEntered.substring(length - 2).equals("\""))) {
			unpunctWord = wordEntered.substring(0, length - 2);

			if ((wordInDictionary = dictionary.get(unpunctWord)) != null) {
				suggestWord = false; // no need to ask for suggestion for a
										// correct word.
				return wordInDictionary;
			} else{ // not found
				suggestWord = false;
				return unpunctWord;
			}// removing the inflections and returning
		}

		// If after all of these checks a word could not be corrected, return as
		// a misspelled word.
		return wordEntered;
	}

}
