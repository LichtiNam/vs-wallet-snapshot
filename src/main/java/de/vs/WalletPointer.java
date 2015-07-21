package de.vs;

import java.io.Serializable;

public class WalletPointer implements Serializable {
  private final String address;

  public WalletPointer(String address) {
    this.address = address;
  }

  public String getAddress() {
    return address;
  }
}
