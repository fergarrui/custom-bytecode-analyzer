package net.nandgr.cba.custom.model;

import java.util.Arrays;
import java.util.List;

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
}
