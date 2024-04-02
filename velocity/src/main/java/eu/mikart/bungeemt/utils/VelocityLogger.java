package eu.mikart.bungeemt.utils;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;

@AllArgsConstructor
public class VelocityLogger implements eu.mikart.bungeemt.utils.Logger {

	private final Logger parent;

	@Override
	public void info(String message) {
		parent.info(message);
	}

	@Override
	public void info(String message, Throwable e) {
		parent.info(message, e);
	}

	@Override
	public void warn(String message) {
		parent.warn(message);
	}

	@Override
	public void warn(String message, Throwable e) {
		parent.warn(message, e);
	}

	@Override
	public void error(String message) {
		parent.error(message);
	}
}
