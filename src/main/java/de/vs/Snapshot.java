package de.vs;

import akka.actor.ActorRef;
import de.vs.events.snapshot.EventMarkerMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Snapshot implements Serializable {

  private final String name;
  private final int amount;

  private List<ActorRef> waitingToResponse;
  private HashMap<ActorRef, List<Object>> recordedMessages;

  public Snapshot(String name, int amount, List<ActorRef> waitingToResponse) {
    this.name = name;
    this.amount = amount;
    this.waitingToResponse = waitingToResponse;
    recordedMessages = new HashMap<>();
  }


  public void handleMessages(Object message, ActorRef sender) {
    if (message instanceof EventMarkerMessage) {
      waitingToResponse.remove(sender);
    } else {
      if (recordedMessages.containsKey(sender)) {
        recordedMessages.get(sender).add(message);
      } else {
        List<Object> messages = new ArrayList<>();
        messages.add(message);
        recordedMessages.put(sender, messages);
      }
    }
  }


  public boolean isWaiting() {
    return waitingToResponse.size() > 0;
  }
}
