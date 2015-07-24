package de.vs;

import akka.actor.ActorRef;
import de.vs.events.*;
import de.vs.events.snapshot.EventMarkerMessage;
import de.vs.events.snapshot.EventSendFinishSnapshot;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Wallet extends AbstractWallet {

  private Snapshot snapshot;
  private boolean recordingSnapshot;
  private boolean sendSnapshot = true;
  private ActorRef joiningNode;

  private List<Snapshot> snapshots;

  public Wallet(String name) {
    super(name);
    initWallet();
    snapshots = new ArrayList<>();
    joiningNode = self();
  }

  public Wallet(String name, int amount, ActorRef joiningNode) {
    super(name);
    setAmount(amount);
    initWallet();
    this.joiningNode = joiningNode;
    joiningNode.tell(new EventJoin(name), self());
  }

  private void initWallet() {
    knownNeighbors = new ConcurrentHashMap<>();
    backedUpNeighbors = new HashMap<>();
  }

  public String getAddress() {
    return self().toString();
  }

  public void onReceive(Object message) {
    if (recordingSnapshot) {
      snapshot.handleMessages(message, sender());
    }
    if (snapshot != null && !snapshot.isWaiting() && sendSnapshot) {
      consoleLogging(getName() + ": Snapshot finished.");
      if (joiningNode == self()) {
        snapshots.add(snapshot);
      } else {
        joiningNode.tell(new EventSendFinishSnapshot(snapshot), self());
      }
      sendSnapshot = false;
      recordingSnapshot = false;
    }

    if (message instanceof EventMarkerMessage) {
      handleMarkerMessage();
    } else if (message instanceof EventAcceptJoin) {
      consoleLogging(getName() + ": I'm accepted.");
      handleAcceptJoin((EventAcceptJoin) message);
    } else if (message instanceof EventJoin) {
      consoleLogging(((EventJoin) message).getName() + " is joint.");
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
    } else if (message instanceof EventGoDown) {
      handleGoDown();
    } else if (message instanceof EventSendFinishSnapshot) {
      handleSendFinishSnapshot((EventSendFinishSnapshot) message);
    } else {
      unhandled(message);
    }

  }

  private void handleSendFinishSnapshot(EventSendFinishSnapshot message) {
    snapshots.add(message.getSnapshot());
    if (snapshots.size() == (knownNeighbors.size() + 1)) {
      printGlobalSnapshot();
    }
  }

  private void printGlobalSnapshot() {
    System.out.println("\nSnapshot\n--------\n");
    for (Snapshot snapshot : snapshots) {
      consoleLogging(snapshot.getName() + " have amount: " + snapshot.getAmount());
    }
  }

  private void handleMarkerMessage() {
    consoleLogging(getName() + ": Receive marker message from " + sender().path().name());
    if (!recordingSnapshot && snapshot == null) {
      consoleLogging(getName() + ": Start Snapshot");
      List<ActorRef> waitingNeighbors = new ArrayList<>(knownNeighbors.values());
      waitingNeighbors.remove(sender());
      snapshot = new Snapshot(getName(), getAmount(), waitingNeighbors);
      notifyAll(new EventMarkerMessage());
      recordingSnapshot = true;
    }
    if (!knownNeighbors.containsValue(sender())) {
      sender().tell(new EventMarkerMessage(), self());
      printMarker(sender().path().name());
    }
  }

  private void printMarker(String sender) {
    consoleLogging(getName() + ": send marker message to " + sender);
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
    getSender().tell(new EventAcceptJoin(knownNeighbors), self());
    knownNeighbors.put(message.getName(), sender());
  }

  private void handleAcceptJoin(EventAcceptJoin message) {
    knownNeighbors = message.getSomeNeighbors();
    notifyAll(new EventSearchMyWallet(getName()));
  }

  private void notifyAll(Object event) {
    for (ActorRef actorRef : knownNeighbors.values()) {
      if (event instanceof EventMarkerMessage) {
        printMarker(actorRef.path().name());
      }
      actorRef.tell(event, self());
    }
  }

  public void updateAmount(int amount) {
    setAmount(getAmount() + amount);
  }

  private void consoleLogging(String log) {
    System.out.println(log);
  }
}
