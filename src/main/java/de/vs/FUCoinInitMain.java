package de.vs;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import de.vs.events.snapshot.EventMarkerMessage;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class FUCoinInitMain {
  public static void main(String[] args) {
    Config config = ConfigFactory.load().getConfig("InitWalletSys");
    ActorSystem actorSystem = ActorSystem.create("FUCoin", config);
    Props props = Props.create(Wallet.class , "initNode");
    ActorRef initNode = actorSystem.actorOf(props, "initNode");

    ActorRef[] actorRefs = new ActorRef[5];
    Random random = new Random();
    for (int i = 0; i < actorRefs.length; i++) {
      props = Props.create(Wallet.class , "Node" + i, random.nextInt(100), initNode);
      actorRefs[i] = actorSystem.actorOf(props, "Node" + i);
    }

    try {
      TimeUnit.MILLISECONDS.sleep(100);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    initNode.tell(new EventMarkerMessage(), null);
  }
}
