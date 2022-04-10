package Exception;

public class CheckingNumAsStringContent {
	public CheckingNumAsStringContent(String id, String type) throws OurException {
		for (int i = 0; i < id.length(); i++) {
			if (!Character.isDigit(id.charAt(i))) {
				throw new OurException("Error, not all characters of " + type + " are digits.\nTry again");
			}
		}
	}
}
