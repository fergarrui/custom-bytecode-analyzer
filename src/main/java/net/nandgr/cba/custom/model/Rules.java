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
package net.nandgr.cba.custom.model;

import java.util.Arrays;
import java.util.List;
import net.nandgr.cba.exception.BadRulesException;
import org.apache.commons.lang.StringUtils;

import static net.nandgr.cba.custom.visitor.helper.RuleHelper.allEmpty;

public class Rules {

  private List<Rule> rules;

  public List<Rule> getRules() {
    return rules;
  }

  public void setRules(List<Rule> rules) {
    this.rules = rules;
  }

  @Override
  public String toString() {
    return "Rules{" +
            "rules=" + Arrays.toString(rules.toArray()) +
            '}';
  }

  public void validateRules() throws BadRulesException {
    Rules rules = this;
    for (Rule rule : rules.getRules()) {
      if (StringUtils.isBlank(rule.getName())) {
        throw new BadRulesException("Rule name cannot be blank.");
      }
      validateRule(rule);
    }
  }

  private static void validateRule(Rule rule) throws BadRulesException {
    if (rule.getInvocations() != null) {
      for (Invocation invocation : rule.getInvocations()) {
        Method notFrom = invocation.getNotFrom();
        if (allEmpty(notFrom)) {
          throw new BadRulesException("\"invocation.notFrom\" property cannot have all the fields blank.");
        }
        Method from = invocation.getFrom();
        if (allEmpty(from)) {
          throw new BadRulesException("\"invocation.from\" property cannot have all the fields blank.");
        }
        if (notFrom != null && from != null) {
          throw new BadRulesException("\"invocation.notFrom\" and \"invocation.from\" cannot be defined at the same time in the same rule.");
        }
        Method method = invocation.getMethod();
        if (allEmpty(method)) {
          throw new BadRulesException("\"invocation.method\" property cannot have all the fields blank.");
        }
      }
    }
  }
}
