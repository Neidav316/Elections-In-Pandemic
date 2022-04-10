package Exception;

import Model.Elections;

public class CheckingValidElectionMonth {
	public CheckingValidElectionMonth(int previousMonth, int electionMonth) throws OurException {
		//if (fastRound.getPreviousElectionsYear() == fastRound.getElectionYear()) {
			if (previousMonth >= electionMonth) {
				throw new OurException(
						"Last elections was at " + previousMonth + ", try again");
			}
		//}
	}
}
