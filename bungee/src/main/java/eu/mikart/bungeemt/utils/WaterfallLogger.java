package eu.mikart.bungeemt.utils;

import lombok.AllArgsConstructor;

import java.util.logging.Logger;

@AllArgsConstructor
public class WaterfallLogger implements eu.mikart.bungeemt.utils.Logger {
	private final Logger parent;

	@Override
	public void info(String message) {
		parent.info(message);
	}

	@Override
	public void info(String message, Throwable e) {
		parent.info(message);
		e.printStackTrace();
	}

	@Override
	public void warn(String message) {
		parent.warning(message);
	}

	@Override
	public void warn(String message, Throwable e) {
		parent.warning(message);
		e.printStackTrace();
	}

	@Override
	public void error(String message) {
		parent.severe(message);
	}
}