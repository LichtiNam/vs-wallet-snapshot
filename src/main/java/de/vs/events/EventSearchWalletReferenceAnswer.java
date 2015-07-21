package de.vs.events;

import akka.actor.ActorRef;

import java.io.Serializable;

public class EventSearchWalletReferenceAnswer implements Serializable {

  private final ActorRef walletRef;
  private final String name;

  public EventSearchWalletReferenceAnswer(String name, ActorRef walletRef) {
    this.walletRef = walletRef;
    this.name = name;
  }

  public ActorRef getWalletRef() {
    return walletRef;
  }

  public String getName() {
    return name;
  }
}
