package Exception;

import Model.Model;

public class CheckingValidElectionYear {

	public CheckingValidElectionYear(int[] previousDate, int electionYear) throws OurException {
		if(previousDate[0] == electionYear) {
			if(previousDate[1] == 12) {
				throw new OurException("Last elections was at " + previousDate[1] + " in this year.\nTry again");
			}
		}
		if (previousDate[0] > electionYear) {
			throw new OurException("Last elections was at " + previousDate[0] + ".\nTry again");
		}
		if (previousDate[0] + 100 < electionYear) {
			throw new OurException("It does not make sense that it has been so long since the last election.\nTry again");
		}
	}
}
