package eu.mikart.bungeemt.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VeryCommonUtils {
	public static int parseTimeToSeconds(String timeString) {
		// Define patterns to match different time units
		Pattern pattern = Pattern.compile("(\\d+)([dhms])");
		Matcher matcher = pattern.matcher(timeString);

		// Initialize total seconds
		int totalSeconds = 0;

		// Match and accumulate time units
		while (matcher.find()) {
			int value = Integer.parseInt(matcher.group(1));
			String unit = matcher.group(2);
			switch (unit) {
				case "d":
					totalSeconds += value * 24 * 60 * 60; // Convert days to seconds
					break;
				case "h":
					totalSeconds += value * 60 * 60; // Convert hours to seconds
					break;
				case "m":
					totalSeconds += value * 60; // Convert minutes to seconds
					break;
				case "s":
					totalSeconds += value; // Add seconds
					break;
			}
		}

		return totalSeconds;
	}
}
