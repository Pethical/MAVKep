package hu.pethical.mavkep.global;

public class Logger {

	public static void Log(String msg) {
		if (Constants.DEBUG)
		{
			android.util.Log.i("MavKep", msg);
		}
	}
}
