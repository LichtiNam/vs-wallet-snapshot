package de.vs.events;

import java.io.Serializable;

public class EventInvalidate implements Serializable {

  private final String name;

  public EventInvalidate(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
