package de.vs.events;

import java.io.Serializable;

public class EventTransaction implements Serializable {
  private final int value;
  private final String source;
  private final String target;

  public EventTransaction(int value, String source, String target) {
    this.value = value;
    this.source = source;
    this.target = target;
  }

  public int getValue() {
    return value;
  }

  public String getSource() {
    return source;
  }

  public String getTarget() {
    return target;
  }
}
