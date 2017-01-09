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
package net.nandgr.cba.visitor.checks.util;

import org.objectweb.asm.Opcodes;

public class SerializationHelper {

  private static final String READ_OBJECT_ARGUMENT = "java/io/ObjectInputStream";
  private static final String READ_OBJECT_NAME = "readObject";

  private SerializationHelper() {
    throw new IllegalAccessError("Cannot instantiate this utility class.");
  }

  public static boolean isCustomDeserializationMethod(int access, String name, String desc) {
    return access == Opcodes.ACC_PRIVATE && READ_OBJECT_NAME.equals(name) && desc.contains(READ_OBJECT_ARGUMENT);
  }
}
