package util;

public class ProgressReporter {

	private long total;
	private long counter;
	private long last;

	private long localCounter = 0;
	private long localCounterMax = 0;

	public ProgressReporter(long total) {
		this.total = total;
		last = System.currentTimeMillis();
		localCounterMax = total / 100;
	}

	public void inc() {
		counter++;
		if (localCounter >= localCounterMax) {
			localCounter = 0;
			long now = System.currentTimeMillis();
			if (now - last > 1000) {
				reportPercentage();
				last = now;
			}
		}
		localCounter++;
	}

	public void reportPercentage() {
		long p = counter * 1000 / total;
		String s = (p / 1000) + "." + (p % 1000);
		System.out.println(s);
	}
}
