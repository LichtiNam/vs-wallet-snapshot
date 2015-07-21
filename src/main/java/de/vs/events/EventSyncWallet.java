package de.vs.events;

import de.vs.AbstractWallet;

import java.io.Serializable;

public class EventSyncWallet implements Serializable {
  private final String name;
  private final AbstractWallet wallet;

  public EventSyncWallet(String name, AbstractWallet wallet) {
    this.name = name;
    this.wallet = wallet;
  }

  public AbstractWallet getWallet() {
    return this.wallet;
  }

  public String getName() {
    return name;
  }
}
