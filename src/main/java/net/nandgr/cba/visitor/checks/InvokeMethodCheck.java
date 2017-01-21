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

import java.util.List;
import net.nandgr.cba.custom.model.Invocation;
import net.nandgr.cba.custom.model.Method;
import net.nandgr.cba.custom.visitor.base.CustomAbstractClassVisitor;
import net.nandgr.cba.custom.visitor.CustomInvocationFinderInsnVisitor;
import net.nandgr.cba.custom.visitor.helper.RuleHelper;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvokeMethodCheck extends CustomAbstractClassVisitor {

  private static final Logger logger = LoggerFactory.getLogger(InvokeMethodCheck.class);

  private static final String INVOKE_METHOD_OWNER = "java/lang/reflect/Method";
  private static final String INVOKE_METHOD_NAME = "invoke";

  private final Invocation invocation;

  public InvokeMethodCheck() {
    super("InvokeMethodCheck");
    Invocation invocation = new Invocation();
    invocation.setOwner(INVOKE_METHOD_OWNER);
    Method method = new Method();
    method.setName(INVOKE_METHOD_NAME);
    invocation.setMethod(method);
    this.invocation = invocation;
  }

  @Override
  public void process() {
    getClassNode().methods.stream().forEach(method -> {
      InsnList instructions = method.instructions;
      for (int i = 0; i < instructions.size(); i++) {
        AbstractInsnNode abstractInsnNode = instructions.get(i);
        if (abstractInsnNode.getType() == AbstractInsnNode.METHOD_INSN) {
          MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
          int access = method.access;
          String name = method.name;
          String desc = method.desc;
          String signature = method.signature;
          List<String> exceptions = method.exceptions;
          logger.trace("visitMethod: access={} name={} desc={} signature={} exceptions={}", access, name, desc, signature, exceptions);

          Method notFrom = invocation.getNotFrom();
          Method from = invocation.getFrom();
          if (RuleHelper.checkNotFrom(notFrom, access, name, desc) && RuleHelper.checkFrom(from, access, name, desc)) {
            CustomInvocationFinderInsnVisitor customInvocationFinderInsnVisitor = new CustomInvocationFinderInsnVisitor(invocation, getRuleName());
            customInvocationFinderInsnVisitor.setNode(methodInsnNode);
            customInvocationFinderInsnVisitor.process();
            if (customInvocationFinderInsnVisitor.issueFound()) {
              itemsFound().addAll(customInvocationFinderInsnVisitor.itemsFound());
              setIssueFound(true);
            }
          }
        }
      }

    });
  }

  @Override
  public boolean showInReport() {
    return true;
  }
}
