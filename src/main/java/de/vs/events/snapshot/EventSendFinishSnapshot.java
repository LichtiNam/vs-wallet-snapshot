package de.vs.events.snapshot;

import de.vs.Snapshot;

import java.io.Serializable;

public class EventSendFinishSnapshot implements Serializable {

  private final Snapshot snapshot;

  public EventSendFinishSnapshot(Snapshot snapshot) {
    this.snapshot = snapshot;
  }

  public Snapshot getSnapshot() {
    return snapshot;
  }
}
