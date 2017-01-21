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
package net.nandgr.cba.custom.visitor.base;

import java.util.Collection;
import net.nandgr.cba.custom.visitor.base.CustomVisitor;
import net.nandgr.cba.report.ReportItem;
import java.util.ArrayList;

public abstract class CustomAbstractVisitor<T> implements CustomVisitor {

  protected Collection<ReportItem> itemsFound = new ArrayList<>();
  protected boolean foundIssue = false;
  private final String ruleName;

  public CustomAbstractVisitor(String ruleName) {
    this.ruleName = ruleName;
  }

  @Override
  public boolean issueFound() {
    return foundIssue;
  }

  @Override
  public void setIssueFound(boolean issueFound) {
    this.foundIssue = issueFound;
  }

  @Override
  public Collection<ReportItem> itemsFound() {
    return itemsFound;
  }

  @Override
  public String getRuleName() {
    return this.ruleName;
  }

  @Override
  public void clear() {
    this.itemsFound.clear();
    this.foundIssue = false;
  }
}
