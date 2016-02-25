/*
 * Copyright 2016 Minecade
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
                new ByteFactory<>(GameCategory.class,
                        GameCategory.UNKNOWN));
    }
}
