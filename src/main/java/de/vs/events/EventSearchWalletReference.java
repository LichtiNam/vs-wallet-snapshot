package de.vs.events;

import java.io.Serializable;

public class EventSearchWalletReference implements Serializable {

  private final String name;

  public EventSearchWalletReference(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
