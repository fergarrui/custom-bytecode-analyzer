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

import net.nandgr.cba.custom.model.Variable;
import org.objectweb.asm.MethodVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomVariableVisitor extends CustomAbstractVisitor {

  private static final Logger logger = LoggerFactory.getLogger(CustomVariableVisitor.class);

  private final Variable variable;

  public CustomVariableVisitor(Variable variable, String ruleName) {
    super(ruleName);
    this.variable = variable;
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    logger.trace("visitMethod: access={} name={} desc={} signature={} exceptions={}", access, name, desc, signature, exceptions);
    CustomMethodVariableVisitor customMethodVariableVisitor = new CustomMethodVariableVisitor(this, variable, name);
    return customMethodVariableVisitor;
  }

  @Override
  public boolean showInReport() {
    return variable.isReport();
  }
}
