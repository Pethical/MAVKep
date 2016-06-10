package hu.pethical.mavkep.data;

public abstract class ProgressCallback {
	public ProgressCallback() {
	}

	public abstract void onProgress(long current, long all);

	public abstract void onDone();

	public abstract void onStart(long all);

}
