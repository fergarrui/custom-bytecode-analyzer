/*
 * Copyright (c) 2016-2017, Fernando Garcia
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

  public static String simpleDescriptorToHuman(String s) {
    String result = s;
    if(result.startsWith("[")) {
      result = result.substring(1, result.length());
    }
    if (result.startsWith("L")) {
      result = result.substring(1, result.length());
    }
    if (result.endsWith(";")) {
      result = result.substring(0, result.length()-1);

    }
    return result;
  }
}
