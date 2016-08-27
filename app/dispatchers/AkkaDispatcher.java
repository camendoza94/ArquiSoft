package dispatchers;

import akka.dispatch.MessageDispatcher;
import play.libs.Akka;

public class AkkaDispatcher {

    public static MessageDispatcher jdbcDispatcher =  Akka.system().dispatchers().lookup("contexts.jdbc-dispatcher");

}