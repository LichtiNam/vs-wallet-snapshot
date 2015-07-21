/*
package de.vs;

import akka.actor.UntypedActor;
import de.vs.events.*;

import java.util.Vector;

public class Node extends UntypedActor {

  private Wallet wallet;
  private int numberOfSearchRequest;

  public Node() {

  }

  public Node(String address) {
    wallet = new WalletImpl(address);
  }

  @Override
  public void preStart() {
  }

  @Override
  public void onReceive(Object message) {
    if (message instanceof EventJoin) {
      handleJoinEvent();
    }
    if (message instanceof EventAcceptJoin) {
      handleAcceptJoinEvent((EventAcceptJoin) message);
    }
    if (message.equals("searchMe")) {
      Wallet foundWallet = wallet.searchWallet(getSender().path().toString());
      EventFoundMyWallet fw = new EventFoundMyWallet(foundWallet);
      getSender().tell(fw, getSelf());
    }
    if (message instanceof EventFoundMyWallet) {
      handleFoundWalletEvent((EventFoundMyWallet) message);
    }
    if (message instanceof EventSyncWallet) {
      Wallet toSyncWallet = ((EventSyncWallet) message).getWallet();
      wallet.storeOrUpdateWallet(toSyncWallet);
    }
    if (message instanceof EventTransaction) {
      handleTransactionEvent((EventTransaction) message);
    }
    if (message.equals("leave")) {
      wallet.removeKnownNeighbor(getSender().path().toString());
    }
  }

  private void handleFoundWalletEvent(EventFoundMyWallet eventFoundMyWallet) {
    if (numberOfSearchRequest > 0) {
      if (eventFoundMyWallet.getWallet() == null) {
        numberOfSearchRequest--;
        if (numberOfSearchRequest == 0) {
          synchronizeOwnWalletToAllNodes();
        }
      } else {
        numberOfSearchRequest = 0;
        wallet = eventFoundMyWallet.getWallet();
        synchronizeOwnWalletToAllNodes();
      }
    }
  }

  private void handleTransactionEvent(EventTransaction eventTransaction) {
    int valueToTransform = eventTransaction.getValue();
    String source = eventTransaction.getSource();
    String target = eventTransaction.getTarget();
    wallet.updateWallets(valueToTransform, source, target);
  }

  private void synchronizeOwnWalletToAllNodes() {
    notifyAll(new EventSyncWallet(name, this.wallet));
  }

  private void handleJoinEvent() {
    String acceptedAddress = getSender().path().toString();
    WalletPointer walletPointer = new WalletPointer(acceptedAddress);
    EventAcceptJoin eventAcceptJoin = new EventAcceptJoin(wallet.join(walletPointer), acceptedAddress);
    getSender().tell(eventAcceptJoin, getSelf());
  }

  private void handleAcceptJoinEvent(EventAcceptJoin eventAcceptJoin) {
    initKnownNodes(eventAcceptJoin);
    searchOwnWallInNetwork();
  }

  private void initKnownNodes(EventAcceptJoin eventAcceptJoin) {
    String ownAddressInNetwork = eventAcceptJoin.getAcceptedAddress();
    wallet = new WalletImpl(ownAddressInNetwork);
    System.out.println("Join accept");
    Vector<WalletPointer> walletPointers = eventAcceptJoin.getSomeNeighbors();
    wallet.initKnownNodes(walletPointers);
  }

  private void searchOwnWallInNetwork() {
    this.numberOfSearchRequest = wallet.getAllKnownNeighbors().size();
    notifyAll("searchMe");
  }

  private void notifyAll(Object event) {
    String address;
    for (WalletPointer walletPointer : wallet.getAllKnownNeighbors()) {
      address = walletPointer.getAddress();
      getContext().actorFor(address).tell(event, getSelf());
    }
  }
}
//</editor-fold>
*/
