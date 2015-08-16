
/*SRM 456 DIV2 250
 
 An Apple Word is a word that consists of only the letters A, P, L, and E, in that exact relative order. There must be exactly one A, two or more Ps, exactly one L and exactly one E. Case does not matter. For example, "Apple" and "apPpPPlE" are Apple Words, while "APLE", "Apples", "Aplpe" and "coconut" are not.
 
 You are given a String word which you must turn into an Apple Word. For each character in word, you can either replace it with a different letter or leave it unchanged. No other operations, like inserting new characters or deleting existing characters, are allowed. Return the minimal number of characters you must replace to get an Apple Word. If it's impossible, return -1.
 */
public class AppleWord2 {

	public static void main(String[] args){
		AppleWord2 apple = new AppleWord2();
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
		
		char[] wordArr = word.toCharArray();
		int wordLength = wordArr.length;
		
		if (wordLength >= 5) {

			// Convert all characters to lowercase; numbers stay unchanged
			for (int i = 0; i < wordLength; i++) {
				if (Character.isUpperCase(wordArr[i])) {
					wordArr[i] = Character.toLowerCase(wordArr[i]);
				}
			}

			// Check characters and replace them if necessary
			for (int i = 0; i < wordLength; i++) {

				if (i == 0) {
					if (wordArr[i] != 'a')
						replaceCount++;
				} else if (i == 1) {
					if (wordArr[i] != 'p')
						replaceCount++;
				} else if (i == 2) {
					if (wordArr[i] != 'p')
						replaceCount++;
				} else if (i == (wordLength - 2)) {
					if (wordArr[i] != 'l')
						replaceCount++;
				} else if (i == (wordLength - 1)) {
					if (wordArr[i] != 'e')
						replaceCount++;
				} else {
					if (wordArr[i] != 'p')
						replaceCount++;
				}
			}

		} else {
			
			replaceCount = -1;
		}
		
        return replaceCount;		
	}
		
}
