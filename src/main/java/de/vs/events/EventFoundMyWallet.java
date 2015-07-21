package de.vs.events;



import de.vs.AbstractWallet;

import java.io.Serializable;

public class EventFoundMyWallet implements Serializable {
  private final AbstractWallet wallet;

  public EventFoundMyWallet(AbstractWallet wallet) {
    this.wallet = wallet;
  }

  public AbstractWallet getWallet() {
    return wallet;
  }
}
