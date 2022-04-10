package Exception;

public class CheckingValidMonth {
	public CheckingValidMonth(int month) throws OurException {
		if(month < 1 || month > 12) {
			throw new OurException("Error, wrong month input, try again.");
		}
	}
}
