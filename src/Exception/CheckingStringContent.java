package Exception;

public class CheckingStringContent {

	public CheckingStringContent(String str, String type) throws OurException {
		for (int i = 0; i < str.length(); i++) {
			if ((!Character.isWhitespace(str.charAt(i)) && !Character.isLetterOrDigit(str.charAt(i)))
					|| Character.isDigit(str.charAt(i))) {
				throw new OurException("Error, not all characters of " + type + " are letters.\nTry again");
			}
		}
	}
}
