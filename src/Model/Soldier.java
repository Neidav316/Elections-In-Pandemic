package Model;

@SuppressWarnings("serial")
public class Soldier extends Citizen {

	boolean carryWeapon;

	public Soldier() {
		this("", "", 0, null, 0, false);
	}

	public Soldier(String name, String ID, int birthYear, Ballot<?> ballot, int numberOfDaysInIsolation,
			boolean p_carryWeapon) {
		super(name, ID, birthYear, ballot, numberOfDaysInIsolation);
		setCarryWeapon(p_carryWeapon);
	}

	public boolean setCarryWeapon(boolean p_carryWeapon) {
		carryWeapon = p_carryWeapon;
		return true;
	}

	public boolean getCarryWeapon() {
		return carryWeapon;
	}

	public String toString() {
		if (getCarryWeapon() == false) {
			return super.toString() + "The soldier doesn't carry weapon.\n";
		} else {
			return super.toString() + "The soldier does carry weapon.\n";
		}
	}

}
