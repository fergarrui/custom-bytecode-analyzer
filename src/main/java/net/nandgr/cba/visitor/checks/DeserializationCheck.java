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
package net.nandgr.cba.visitor.checks;

import net.nandgr.cba.custom.model.Invocation;
import net.nandgr.cba.custom.model.Method;
import net.nandgr.cba.custom.visitor.CustomAbstractVisitor;
import net.nandgr.cba.custom.visitor.CustomInvocationFinderVisitor;
import net.nandgr.cba.visitor.checks.util.SerializationHelper;
import org.objectweb.asm.MethodVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeserializationCheck extends CustomAbstractVisitor {

  private static final Logger logger = LoggerFactory.getLogger(DeserializationCheck.class);

  private final CustomInvocationFinderVisitor methodVisitor;
  private static final String READ_OBJECT_OWNER = "java/io/ObjectInputStream";
  private static final String READ_OBJECT_NAME = "readObject";
  private static final String READ_OBJECT_DESCRIPTOR = "java/lang/Object";

  public DeserializationCheck() {
    super("DeserializationCheck");
    Invocation invocation = new Invocation();
    invocation.setOwner(READ_OBJECT_OWNER);
    Method method = new Method();
    method.setName(READ_OBJECT_NAME);
    method.setParameter(READ_OBJECT_DESCRIPTOR);
    invocation.setMethod(method);
    this.methodVisitor = new CustomInvocationFinderVisitor(this, invocation);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    logger.trace("visitMethod: access={} name={} desc={} signature={} exceptions={}", access, name, desc, signature, exceptions);
    if (SerializationHelper.isCustomDeserializationMethod(access, name, desc)) {
      return super.visitMethod(access, name, desc, signature, exceptions);
    }
    return methodVisitor;
  }

  @Override
  public boolean showInReport() {
    return true;
  }
}
