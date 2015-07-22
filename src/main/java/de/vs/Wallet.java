package de.vs;

import akka.actor.ActorRef;
import de.vs.events.*;
import de.vs.events.snapshot.EventMarkerMessage;
import de.vs.events.snapshot.EventSendFinishSnapshot;

import java.util.*;

public class Wallet extends AbstractWallet {

  private Snapshot snapshot;
  private boolean recordingSnapshot;
  private ActorRef joiningNode;

  private List<Snapshot> snapshots;

  public Wallet(String name) {
    super(name);
    initWallet();
    snapshots = new ArrayList<>();
  }

  public Wallet(String name, ActorRef joiningNode) {
    super(name);
    initWallet();
    this.joiningNode = joiningNode;
    joiningNode.tell(new EventJoin(name), self());
  }

  private void initWallet() {
    knownNeighbors = new HashMap<>();
    backedUpNeighbors = new HashMap<>();
  }

  public String getAddress() {
    return self().toString();
  }

  public void onReceive(Object message) {
    if (recordingSnapshot && snapshot.isWaiting()) {
      snapshot.handleMessages(message, sender());
    } else {
      if (snapshot != null && !snapshot.isWaiting()) {
        joiningNode.tell(new EventSendFinishSnapshot(snapshot), self());
      }
      recordingSnapshot = false;
    }

    if (message instanceof EventMarkerMessage) {
      handleMarkerMessage();
    } else if (message instanceof EventAcceptJoin) {
      System.out.println("I'm accepted");
      handleAcceptJoin((EventAcceptJoin) message);
    } else if (message instanceof EventJoin) {
      System.out.println(((EventJoin) message).getName() + " is joint");
      handleJoin((EventJoin) message);
    } else if (message instanceof EventFoundMyWallet) {
      handleFoundMyWallet((EventFoundMyWallet) message);
    } else if (message instanceof EventSearchMyWallet) {
      handleSearchMyWallet((EventSearchMyWallet) message);
    } else if (message instanceof EventInvalidate) {
      handleInvalidate((EventInvalidate) message);
    } else if (message instanceof EventSearchWalletReference) {
      handleSearchWalletReference((EventSearchWalletReference) message);
    } else if (message instanceof EventSearchWalletReferenceAnswer) {
      handleSearchWalletReferenceAnswer((EventSearchWalletReferenceAnswer) message);
    } else if (message instanceof EventSyncWallet) {
      handleSyncWallet((EventSyncWallet) message);
    } else if (message instanceof EventTransaction) {
      handleTransaction((EventTransaction) message);
    } else if (message instanceof EventLeave) {
      handleLeave((EventLeave) message);
    }  else if (message instanceof EventGoDown) {
      handleGoDown();
    } else if (message instanceof EventSendFinishSnapshot) {
      handleSendFinishSnapshot((EventSendFinishSnapshot) message);
    } else {
        unhandled(message);
      }

  }

  private void handleSendFinishSnapshot(EventSendFinishSnapshot message) {
    snapshots.add(message.getSnapshot());
  }


  private void handleMarkerMessage() {
    if (!recordingSnapshot) {
      List<ActorRef> waitingNeighbors = (List<ActorRef>) knownNeighbors.values();
      waitingNeighbors.remove(sender());
      snapshot = new Snapshot(getName(), getAmount(), waitingNeighbors);
      this.notifyAll(new EventMarkerMessage());
      recordingSnapshot = true;
    }
  }


  private List<ActorRef> knownNeighborsToWaitingList() {
    return (List< ActorRef>) knownNeighbors.values();
  }

  private void handleGoDown() {
    notifyAll(new EventLeave(getName()));
    context().system().shutdown();
  }

  private void handleLeave(EventLeave message) {
    knownNeighbors.remove(message.getName());
  }

  private void handleTransaction(EventTransaction message) {
    String source = message.getSource();
    String target = message.getTarget();
    int valueOfTransfer = message.getValue();
    if (getName().equals(source)) {
      updateAmount(-valueOfTransfer);
    }
    if (getName().equals(target)) {
      updateAmount(valueOfTransfer);
    }
    updateWallets(valueOfTransfer, source, target);
  }

  private void updateWallets(int valueToTransfer, String source, String target) {
    backedUpNeighbors.keySet().stream().filter(key -> key.equals(source)).forEach(key -> {
      backedUpNeighbors.get(key).updateAmount(-valueToTransfer);
    });
    backedUpNeighbors.keySet().stream().filter(key -> key.equals(target)).forEach(key -> {
      backedUpNeighbors.get(key).updateAmount(valueToTransfer);
    });
  }

  private void handleSyncWallet(EventSyncWallet message) {
    backedUpNeighbors.put(message.getName(), message.getWallet());
  }

  private void handleSearchWalletReferenceAnswer(EventSearchWalletReferenceAnswer message) {
    knownNeighbors.put(message.getName(), message.getWalletRef());
  }

  private void handleSearchWalletReference(EventSearchWalletReference message) {
    String name = message.getName();
    ActorRef actorRef = knownNeighbors.get(name);
    if (actorRef != null) {
      getSender().tell(new EventSearchWalletReferenceAnswer(name, actorRef), self());
    }
  }

  private void handleInvalidate(EventInvalidate message) {
    backedUpNeighbors.remove(message.getName());
  }

  private void handleSearchMyWallet(EventSearchMyWallet message) {
    String name = message.getName();
    if (backedUpNeighbors.containsKey(name)) {
      getSender().tell(new EventFoundMyWallet(backedUpNeighbors.get(name)), self());
    }
  }

  private void handleFoundMyWallet(EventFoundMyWallet message) {
    AbstractWallet wallet = message.getWallet();
    setAmount(wallet.getAmount());
  }

  private void handleJoin(EventJoin message) {
    HashMap<String, ActorRef> someNeighbors = new HashMap<>();
    if (knownNeighbors.size() < 5) {
      someNeighbors = knownNeighbors;
    } else {
      for (String str : listOfRandomNumbers(knownNeighbors.keySet(), 5)) {
        someNeighbors.put(str, knownNeighbors.get(str));
      }
    }
    getSender().tell(new EventAcceptJoin(someNeighbors), self());
  }

  private List<String> listOfRandomNumbers(Set<String> keySet, int count) {
    Random random = new Random();
    String[] keySetArray = (String[]) keySet.toArray();
    List<String> rnd = new ArrayList<>();
    int max = keySet.size();
    int maxCount;
    maxCount = max < count ? max : count;
    int input;
    for (int i = 0; i < maxCount; i++) {
      input = random.nextInt(max + 1);
      while (!rnd.contains(keySetArray[input])) {
        input = random.nextInt(max + 1);
      }
      rnd.add(keySetArray[input]);
    }
    return rnd;
  }

  private void handleAcceptJoin(EventAcceptJoin message) {
    knownNeighbors = message.getSomeNeighbors();
    notifyAll(new EventSearchMyWallet(getName()));
  }

  private void notifyAll(Object event) {
      for (ActorRef actorRef : knownNeighbors.values()) {
        actorRef.tell(event, self());
      }
  }

  public void updateAmount(int amount) {
    setAmount(getAmount() + amount);
  }
}
