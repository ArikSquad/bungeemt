package eu.mikart.bungeemt.utils;

/**
 * <a href="https://github.com/MrIvanPlays/Skins/blob/dev/v2/core/src/main/java/com/mrivanplays/skins/core/Logger.java">...</a>
 * OH GOD WHY: because different frameworks use different logging bukkit uses java's logger, while
 * velocity uses slf4j and that's why we need that.
 */
public interface Logger {

	void info(String message);

	void info(String message, Throwable e);

	void warn(String message);

	void warn(String message, Throwable e);

	void error(String message);
}
