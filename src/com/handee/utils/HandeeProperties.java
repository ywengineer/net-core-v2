package com.handee.utils;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

/**
 * @author wang
 * 
 */
public class HandeeProperties extends Properties {
	private static final long serialVersionUID = -4599023842346938325L;

	private static final Logger _log = Logger.getLogger(HandeeProperties.class);

	private boolean _warn = true;

	public HandeeProperties() {
	}

	public HandeeProperties setLog(boolean warn) {
		_warn = warn;

		return this;
	}

	// ===================================================================================

	public HandeeProperties(String name) throws IOException {
		load(new FileInputStream(name));
	}

	public HandeeProperties(File file) throws IOException {
		load(new FileInputStream(file));
	}

	public HandeeProperties(InputStream inStream) throws IOException {
		load(inStream);
	}

	public HandeeProperties(Reader reader) throws IOException {
		load(reader);
	}

	// ===================================================================================

	public void load(String name) throws IOException {
		load(new FileInputStream(name));
	}

	public void load(File file) throws IOException {
		load(new FileInputStream(file));
	}

	@Override
	public void load(InputStream inStream) throws IOException {
		try {
			super.load(inStream);
		} finally {
			inStream.close();
		}
	}

	@Override
	public void load(Reader reader) throws IOException {
		try {
			super.load(reader);
		} finally {
			reader.close();
		}
	}

	// ===================================================================================

	@Override
	public String getProperty(String key) {
		String property = super.getProperty(key);

		if (property == null) {
			if (_warn)
				_log.warn("DaProperties: Missing property for key - " + key);

			return null;
		}

		return property.trim();
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		String property = super.getProperty(key, defaultValue);

		if (property == null) {
			if (_warn)
				_log.warn("DaProperties: Missing defaultValue for key - " + key);

			return null;
		}

		return property.trim();
	}
}