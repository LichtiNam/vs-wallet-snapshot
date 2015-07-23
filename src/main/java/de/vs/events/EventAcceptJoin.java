package de.vs.events;


import akka.actor.ActorRef;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class EventAcceptJoin implements Serializable {

  private final ConcurrentHashMap<String, ActorRef> someNeighbors;

  public EventAcceptJoin(ConcurrentHashMap<String, ActorRef> someNeighbors) {
    this.someNeighbors = someNeighbors;
  }

  public ConcurrentHashMap<String, ActorRef> getSomeNeighbors() {
    return someNeighbors;
  }
}
