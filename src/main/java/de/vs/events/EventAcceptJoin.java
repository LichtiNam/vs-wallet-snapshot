package de.vs.events;


import akka.actor.ActorRef;

import java.io.Serializable;
import java.util.HashMap;

public class EventAcceptJoin implements Serializable {

  private final HashMap<String, ActorRef> someNeighbors;

  public EventAcceptJoin(HashMap<String, ActorRef> someNeighbors) {
    this.someNeighbors = someNeighbors;
  }

  public HashMap<String, ActorRef> getSomeNeighbors() {
    return someNeighbors;
  }
}
