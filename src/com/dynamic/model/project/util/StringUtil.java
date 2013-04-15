package com.dynamic.model.project.util;

public class StringUtil {
	public static String firstCharToUpcase(String s)
	  {
	    if ((((s == null) ? 1 : 0) | ((s.length() < 1) ? 1 : 0)) != 0) return s;

	    if (Character.isUpperCase(s.charAt(0))) {
	      return s;
	    }
	    String first = s.substring(0, 1);
	    String last = s.substring(1, s.length());
	    return first.toUpperCase() + last;
	  }

	  public static String firstCharToLowcase(String s)
	  {
	    if ((((s == null) ? 1 : 0) | ((s.length() < 1) ? 1 : 0)) != 0) return s;

	    if (Character.isLowSurrogate(s.charAt(0))) {
	      return s;
	    }
	    String first = s.substring(0, 1);
	    String last = s.substring(1, s.length());
	    return first.toLowerCase() + last;
	  }
}
