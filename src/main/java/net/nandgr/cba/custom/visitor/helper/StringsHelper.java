package net.nandgr.cba.custom.visitor.helper;

public class StringsHelper {

  private StringsHelper() {
    throw new IllegalAccessError("Cannot instantiate this utility class.");
  }

  public static String dotsToSlashes(String s) {
    return s.replaceAll("\\.", "/");
  }

  public static String spacesToDashesLowercase(String s) {
    return s.replaceAll(" ", "-").toLowerCase();
  }
}
