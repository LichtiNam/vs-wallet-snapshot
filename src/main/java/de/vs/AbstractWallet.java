package de.vs;


import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.io.Serializable;
import java.util.HashMap;

public abstract class AbstractWallet extends UntypedActor implements Serializable {

  private final String name;
  private int amount;

  public transient HashMap<String, ActorRef> knownNeighbors;
  public transient HashMap<String, ActorRef> localNeighbors;
  public transient HashMap<String, AbstractWallet> backedUpNeighbors;

  public AbstractWallet(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public abstract String getAddress();

  public abstract void updateAmount(int amount);

  public abstract void onReceive(Object message);
}
