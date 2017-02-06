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
package net.nandgr.cba.callgraph.runnable;

import net.nandgr.cba.callgraph.model.Call;
import net.nandgr.cba.callgraph.model.GraphHolder;
import net.nandgr.cba.callgraph.model.MethodGraph;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassCallGraph {

  private static final Logger logger = LoggerFactory.getLogger(ClassCallGraph.class);
  private final ClassNode classNode;

  public ClassCallGraph(ClassNode classNode) {
    this.classNode = classNode;
  }

  public void populateClassGraph() {
    String className = Type.getObjectType(classNode.name).getClassName();
    logger.debug("Creating graph for class {}" , className);
    for (MethodNode methodNode : classNode.methods) {
      String methodName = methodNode.name;
      MethodGraph caller = new MethodGraph(className, methodName);
      InsnList instructions = methodNode.instructions;
      for (int i = 0; i < instructions.size(); i++) {
        AbstractInsnNode insnNode = instructions.get(i);
        if (insnNode.getType() == AbstractInsnNode.METHOD_INSN) {
          MethodInsnNode methodInsnNode = (MethodInsnNode)insnNode;
          String calledOwner = Type.getObjectType(methodInsnNode.owner).getClassName();
          String calledName = methodInsnNode.name;
          MethodGraph called = new MethodGraph(calledOwner, calledName);
          Call call = new Call(caller, called);
          if (!called.getOwner().equals("java.lang.Object") && !called.getName().equals("<init>")) {
            logger.trace("Adding call graph: {}", call.toString());
            GraphHolder.addCallGraph(call);
          }
        }
      }
    }
  }
}
