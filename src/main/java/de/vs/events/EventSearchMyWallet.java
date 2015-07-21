package de.vs.events;

import java.io.Serializable;

public class EventSearchMyWallet implements Serializable {

  private final String name;

  public EventSearchMyWallet(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
