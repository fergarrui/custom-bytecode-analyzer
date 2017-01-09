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
import org.objectweb.asm.MethodVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvokeMethodCheck extends CustomAbstractVisitor {

  private static final Logger logger = LoggerFactory.getLogger(InvokeMethodCheck.class);

  private final CustomInvocationFinderVisitor customInvocationFinderVisitor;
  private static final String INVOKE_METHOD_OWNER = "java/lang/reflect/Method";
  private static final String INVOKE_METHOD_NAME = "invoke";
  private static final String INVOKE_METHOD_DESCRIPTOR = "java/lang/Object";

  public InvokeMethodCheck() {
    super("InvokeMethodCheck");
    Invocation invocation = new Invocation();
    invocation.setOwner(INVOKE_METHOD_OWNER);
    Method method = new Method();
    method.setName(INVOKE_METHOD_NAME);
    method.setParameter(INVOKE_METHOD_DESCRIPTOR);
    invocation.setMethod(method);
    this.customInvocationFinderVisitor = new CustomInvocationFinderVisitor(this, invocation);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    logger.trace("visitMethod: access={} name={} desc={} signature={} exceptions={}", access, name, desc, signature, exceptions);
    return customInvocationFinderVisitor;
  }

  @Override
  public boolean showInReport() {
    return true;
  }
}
