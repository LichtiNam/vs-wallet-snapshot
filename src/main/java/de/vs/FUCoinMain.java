package de.vs;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import de.vs.events.EventJoin;

public class FUCoinMain {
  public static void main(String[] args) {
    String knownAddress = "akka://FUcoin@127.0.0.1:2552/user/initNode";
    String name = "Max";
    Config config = ConfigFactory.load().getConfig("WalletSys");
    ActorSystem actorSystem = ActorSystem.create(name, config);
    Props props = Props.create(Wallet.class, name);
    ActorRef node = actorSystem.actorOf(props);
    ActorRef test = actorSystem.actorFor(knownAddress);
    System.out.println(test);
    test.tell(new EventJoin(name), node);
  }
}
