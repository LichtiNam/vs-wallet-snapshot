package de.vs.events;

import java.io.Serializable;

public class EventJoin implements Serializable {

  final private String name;

  public EventJoin(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
