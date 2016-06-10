package hu.pethical.mavkep.global;

public class Jni {

	static {
		System.loadLibrary("MAVKep");
	}

	public static native String GetBaseUrl();

	public static native String GetLocalTransportUrl();

	public static native String GetMapUrl();

	public static native String GetStations();
}
