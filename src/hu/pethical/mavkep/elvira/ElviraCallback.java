package hu.pethical.mavkep.elvira;

public abstract class ElviraCallback implements IElviraCallback {

	@Override
	public abstract void RequestDone(ElviraResponse response);
}
