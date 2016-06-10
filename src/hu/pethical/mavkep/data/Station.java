package hu.pethical.mavkep.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "station")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Station {

	public enum LocationType {
		station, stop
	}

	@DatabaseField(columnName = "_id")
	public int			id;

	@DatabaseField(canBeNull = false)
	public String		name;

	@DatabaseField(canBeNull = true)
	public long			latitude;

	@DatabaseField(canBeNull = true)
	public long			longitude;

	@DatabaseField(defaultValue = "0")
	public boolean		BKSZ;

	@DatabaseField(defaultValue = "0")
	public boolean		MAV;

	@DatabaseField(defaultValue = "0")
	public boolean		BKSZ_Discount;

	@DatabaseField(unknownEnumName = "stop")
	public LocationType	locationType;

	public Station() {

	}

}
