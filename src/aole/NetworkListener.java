package aole;

public interface NetworkListener {
	public void networkUpdated(int epoch, String msg, double error);
	public void networkUpdated();
}
