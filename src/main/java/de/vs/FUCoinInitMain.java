package de.vs;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class FUCoinInitMain {
  public static void main(String[] args) {
    Config config = ConfigFactory.load().getConfig("InitWalletSys");
    ActorSystem actorSystem = ActorSystem.create("FUcoin", config);
    Props props = Props.create(Wallet.class , "FirstNode");
    ActorRef initNode = actorSystem.actorOf(props, "initNode");

    ActorRef[] actorRefs = new ActorRef[10];
    for (int i = 0; i < actorRefs.length; i++) {
      props = Props.create(Wallet.class , "Node" + i, initNode);
      actorRefs[i] = actorSystem.actorOf(props, "initNode");
    }
    initNode.tell(new EventStartSnapshot(), null);
  }
}
