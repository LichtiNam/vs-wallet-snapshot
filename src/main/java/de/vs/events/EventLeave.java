package de.vs.events;

import java.io.Serializable;

public class EventLeave implements Serializable {
  private final String name;

  public EventLeave(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
