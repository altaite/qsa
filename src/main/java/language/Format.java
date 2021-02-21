package language;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Format {

	public static String digits(double d, int digits, int width) {
		return String.format("%" + width + "." + digits + "f", d);
	}

	public static String percentOneDigit(double d) {
		return String.format("%.1f", d * 100);
	}

	public static String oneDigit(double d) {
		return String.format("%.1f", d);
	}

	public static String date(long stamp) {
		Date date = new Date(stamp);
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String dateText = df2.format(date);
		return dateText;
	}

	public static Date parseDate(String date) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	public static String zeroPad(int i, int width) {
		return String.format("%05d", i);
	}

	public static void main(String[] args) {
		//System.out.println(digits(111.1234, 1, 5));
		//System.out.println(percentOneDigit(1.123235353));
		System.out.println(zeroPad(11, 5));
	}

}
