
/*SRM 456 DIV2 250

An Apple Word is a word that consists of only the letters A, P, L, and E, in that exact relative order. There must be exactly one A, two or more Ps, exactly one L and exactly one E. Case does not matter. For example, "Apple" and "apPpPPlE" are Apple Words, while "APLE", "Apples", "Aplpe" and "coconut" are not.

You are given a String word which you must turn into an Apple Word. For each character in word, you can either replace it with a different letter or leave it unchanged. No other operations, like inserting new characters or deleting existing characters, are allowed. Return the minimal number of characters you must replace to get an Apple Word. If it's impossible, return -1.
*/
public class AppleWord {

	public static void main(String[] args){
		AppleWord apple = new AppleWord();
		int Apple = apple.minRep("Apple");
		int apPpPPlE = apple.minRep("apPpPPlE");
		int APLE = apple.minRep("APLE");
		int TopCoder = apple.minRep("TopCoder");

		System.out.println(Apple);
		System.out.println(apPpPPlE);
		System.out.println(APLE);
		System.out.println(TopCoder);	
	}
	
	public int minRep(String word){		
		int replaceCount = 0;	
		int wordLength = word.length();
		StringBuilder str = new StringBuilder(word);
		if (wordLength >= 5) {		
			for (int i = 0; i < wordLength; i++) {
				if (Character.isUpperCase(str.charAt(i))) {
					str.setCharAt(i, Character.toLowerCase(str.charAt(i)));
				}
			}			
			for (int i = 0; i < wordLength; i++) {
				if (i == 0) {
					if (str.charAt(i) != 'a')
						replaceCount++;
				} else if (i == 1) {
					if (str.charAt(i) != 'p')
						replaceCount++;
				} else if (i == 2) {
					if (str.charAt(i) != 'p')
						replaceCount++;
				} else if (i == (wordLength - 2)) {
					if (str.charAt(i) != 'l')
						replaceCount++;
				} else if (i == (wordLength - 1)) {
					if (str.charAt(i) != 'e')
						replaceCount++;
				} else {
					if (str.charAt(i) != 'p')
						replaceCount++;
				}
			}
		} else {
						replaceCount = -1;
		}	
        return replaceCount;		
	}		
}