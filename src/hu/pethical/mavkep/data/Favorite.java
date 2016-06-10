package hu.pethical.mavkep.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "favorite")
public class Favorite {

	@DatabaseField(generatedId = true, columnName = "_id")
	public int		_id;

	@DatabaseField(canBeNull = false)
	public String	departure;

	@DatabaseField(canBeNull = false)
	public String	destination;

	@DatabaseField(canBeNull = true)
	public boolean	transfer;

	@DatabaseField(canBeNull = true)
	public boolean	bpmonthlycard;

	@DatabaseField(canBeNull = true)
	public boolean	bycicle;

	@DatabaseField(canBeNull = true)
	public boolean	reserved;

	@DatabaseField(canBeNull = false, defaultValue = "0")
	public boolean	sync;

	public Favorite() {

	}

	@Override
	public String toString() {
		return String.format("%s - %s", departure, destination);
	}

}
