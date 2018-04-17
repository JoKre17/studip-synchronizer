package de.luh.kriegel.studip.synchronizer.content.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.luh.kriegel.studip.synchronizer.content.model.data.Id;

public class RegexHelper {

	private final static Logger log = LogManager.getLogger(RegexHelper.class);
	
	public static Id extractIdFromString(String value) {
		Pattern idPattern = Pattern.compile(".*?\\/([0-9a-f]{32})\\.*");
		Matcher matcher = idPattern.matcher(value);

		matcher.find();

		if (matcher.groupCount() >= 1) {
			return new Id(matcher.group(1));
		}

		return null;
	}

}
