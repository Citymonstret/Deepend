import com.minecade.deepend.client.DeependClient;
import com.minecade.deepend.game.GameCategory;
import com.minecade.deepend.game.GamePlayer;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.object.ByteFactory;
import com.minecade.deepend.object.ObjectManager;
import com.minecade.deepend.object.StringList;
import com.minecade.deepend.request.ShutdownRequest;

/**
 * Created 2/23/2016 for Deepend
 *
 * @author Citymonstret
 */
public class TestGameClient implements DeependClient.DeependClientApplication {

    public static void main(String[] args) {
        new DeependClient(new TestGameClient());
    }

    @Override
    public void registerInitialRequests(DeependClient client) {
        // Let's re-use this lambda ;))
        GamePlayer.PlayerCallback serverAnnouncement = player ->
                Logger.get().info(player.getPlayerName() + " is on server " + player.getPlayerServer());

        // These three requests does the same thing, it just
        // shows that the syntax can be adapted to many different
        // usage scenarios
        client.addPendingRequest(GamePlayer.requestPlayer("jeb_,notch", currentConnection(), serverAnnouncement));
        client.addPendingRequest(GamePlayer.requestPlayer("*", currentConnection(), serverAnnouncement));
        client.addPendingRequest(GamePlayer.requestPlayers(new StringList("jeb_", "notch"), currentConnection(), serverAnnouncement));

        // This makes the client shut down, quite handy
        client.addPendingRequest(new ShutdownRequest());
    }

    @Override
    public void registerObjectMapping(ObjectManager objectManager) {
        // Will make sure that all requests using the category PLAYERS
        // returns a GamePlayer instance, rather than returning
        // raw data
        ObjectManager.instance.registerMapping(GamePlayer.class);
    }

    @Override
    public void registerByteFactories() {
        ByteFactory.addByteFactory(ByteFactory.FactoryType.DATA_TYPE,
                new ByteFactory(GameCategory.class,
                        GameCategory.UNKNOWN));
    }
}
