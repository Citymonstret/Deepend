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

import com.minecade.deepend.bits.EnumBitField;
import com.minecade.deepend.client.DeependClient;
import com.minecade.deepend.data.DataObject;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.game.GameCategory;
import com.minecade.deepend.game.GamePlayer;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.object.ObjectManager;
import com.minecade.deepend.object.ProviderGroup;
import com.minecade.deepend.object.StringList;
import com.minecade.deepend.request.*;
import com.minecade.deepend.values.ValueFactory;

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
        // This is defined outside of the connection itself, as this
        // shouldn't be re-created
        EnumBitField<Byte, GameCategory> categoryEnumBitField = new EnumBitField<>(GameCategory.class);
        int field = categoryEnumBitField.construct(GameCategory.PLAYERS, GameCategory.PROXIES, GameCategory.SERVERS);

        //
        //  RE-USABLE LAMBDAS
        //
        GamePlayer.PlayerCallback serverAnnouncement = player ->
                Logger.get().info(player.getPlayerName() + " is on server " + player.getPlayerServer());

        DataRequest.DataRecipient debugRecipient = o -> o.forEach(oo -> {
            if (oo instanceof DataObject) {
                Logger.get().debug("Found object:");
                Logger.get().dump(((DataObject) oo));
            }
        });

        // Just a simple debug statement for categories, should
        // be used to add new requests
        StatusRequest.StatusRecipient statusRecipient = f -> categoryEnumBitField.extract(f)
                .forEach(category -> Logger.get().info("Updated category: " + category.name()));

        //
        //  END OF RE-USABLE LAMBDAS
        //

        // This will simply fetch the updated categories
        client.addPendingRequest(new StatusRequest(field, statusRecipient, DeependClient.currentConnection));

        // These three requests does the same thing, it just
        // shows that the syntax can be adapted to many different
        // usage scenarios
        client.addPendingRequest(GamePlayer.requestPlayer("jeb_,notch", currentConnection(), serverAnnouncement));
        client.addPendingRequest(GamePlayer.requestPlayer("*", currentConnection(), serverAnnouncement));
        client.addPendingRequest(GamePlayer.requestPlayers(new StringList("jeb_", "notch"), currentConnection(), serverAnnouncement));

        // Let's add some data to the PROXIES holder
        client.addPendingRequest(new AddRequest(debugRecipient, currentConnection()) {
            @Override
            protected void buildRequest(DeependBuf buf) {
                buf.writeByte(GameCategory.PROXIES);
                buf.writeString("lobby1");
                buf.writeInt(1);
                buf.writeString("players:10");
            }
        });

        // Now let's read it, to make sure it's there
        client.addPendingRequest(new GetRequest(debugRecipient, currentConnection()) {
            @Override
            protected void buildRequest(DeependBuf buf) {
                buf.writeByte(GameCategory.PROXIES);
                buf.writeString("lobby1");
                buf.writeString("*");
            }
        });

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
    public void registerFactories() {
        ValueFactory.addValueFactory(ValueFactory.FactoryType.DATA_TYPE, new ValueFactory<>(ProviderGroup.fromEnumClass(GameCategory.class), GameCategory.UNKNOWN));
    }
}
