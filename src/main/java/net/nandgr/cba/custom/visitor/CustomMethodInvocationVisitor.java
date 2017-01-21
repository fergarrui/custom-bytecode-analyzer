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
package net.nandgr.cba.custom.visitor;

import java.util.List;
import net.nandgr.cba.custom.model.Invocation;
import net.nandgr.cba.custom.model.Method;
import net.nandgr.cba.custom.visitor.base.CustomAbstractClassVisitor;
import net.nandgr.cba.custom.visitor.helper.RuleHelper;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomMethodInvocationVisitor extends CustomAbstractClassVisitor {

  private final Invocation invocation;

  private static final Logger logger = LoggerFactory.getLogger(CustomMethodInvocationVisitor.class);

  public CustomMethodInvocationVisitor(Invocation invocation, String ruleName) {
    super(ruleName);
    this.invocation = invocation;
  }

  @Override
  public void process() {
    for (MethodNode methodNode : getClassNode().methods) {
      InsnList instructions = methodNode.instructions;
      for (int i = 0; i < instructions.size(); i++) {
        AbstractInsnNode insnNode = instructions.get(i);
        if (insnNode.getType() == AbstractInsnNode.METHOD_INSN) {
          MethodInsnNode methodInsnNode = (MethodInsnNode)insnNode;
          int access = methodNode.access;
          String name = methodNode.name;
          String desc = methodNode.desc;
          String signature = methodNode.signature;
          List<String> exceptions = methodNode.exceptions;
          logger.trace("visitMethod: access={} name={} desc={} signature={} exceptions={}", access, name, desc, signature, exceptions);

          Method notFrom = invocation.getNotFrom();
          Method from = invocation.getFrom();
          if (RuleHelper.checkNotFrom(notFrom, access, name) && RuleHelper.checkFrom(from, access, name)) {
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
    }
  }

  @Override
  public boolean showInReport() {
    return invocation.isReport();
  }
}
