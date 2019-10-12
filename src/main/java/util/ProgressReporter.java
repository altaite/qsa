package util;

public class ProgressReporter {

	private long total;
	private long counter;
	private long last;

	public ProgressReporter(long total) {
		this.total = total;
		last = System.currentTimeMillis();
	}

	public void inc() {
		counter++;
		long now = System.currentTimeMillis();
		if (now - last > 1000) {
			reportPercentage();
			last = now;
		}
	}

	public void reportPercentage() {
		long p = counter * 1000 / total;
		String s = (p / 1000) + "." + (p % 1000);
		System.out.println(s);
	}
}
