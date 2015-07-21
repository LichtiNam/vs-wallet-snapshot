package de.vs;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import de.vs.events.EventGoDown;

public class FUCoinInitMain {
  public static void main(String[] args) {
    Config config = ConfigFactory.load().getConfig("InitWalletSys");
    ActorSystem actorSystem = ActorSystem.create("FUcoin", config);
    Props props = Props.create(Wallet.class , "FirstNode");
    ActorRef initNode = actorSystem.actorOf(props, "initNode");
    System.out.println(initNode.toString());
    System.out.println(initNode.path());
    initNode.tell(new EventGoDown(), initNode);
  }
}
