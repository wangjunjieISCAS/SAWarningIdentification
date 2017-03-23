/**
 * Copyright (c) 2006, 2007, 2008 Marwan Abi-Antoun, Jonathan Aldrich, Nels E. Beckman,
 * Kevin Bierhoff, David Dickey, Ciera Jaspan, Thomas LaToza, Gabriel Zenarosa, and others.
 *
 * This file is part of Crystal.
 *
 * Crystal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Crystal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Crystal.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.cmu.cs.crystal.internal;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Similar to ShortFormatter, but also prints stack traces. 
 * 
 * @author Ciera Jaspan
 *
 */
public class ShortExceptionFormatter extends Formatter {

	/* (non-Javadoc)
	 * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
	 */
	@Override
	public String format(LogRecord record) {
		try {
			String result = formatMessage(record);
			try {
				String sourceClassName = record.getSourceClassName();
				String sourceMethodName = record.getSourceMethodName();
				result = record.getLevel().getLocalizedName() + ": " 
					+ result 	
					+ " [" + (sourceClassName != null ? sourceClassName : "??")
					+ "#" 
					+ (sourceMethodName != null ? sourceMethodName : "??") 
					+ "]\n";
				if (record.getThrown() != null) {
					result += record.getThrown().getMessage() + "\n";
						for (StackTraceElement element : record.getThrown().getStackTrace()) {
							result += "   " + element.toString() + "\n";
						}
					}
			}
			catch(Throwable t) {
				result = "Error formatting log message \"" + result + "\": " + t.getLocalizedMessage();
			}
			return result;
		}
		catch(Throwable t) {
			return "Error formatting log record: " + t.getLocalizedMessage();
		}
	}

}
