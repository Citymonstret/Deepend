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
package com.minecade.deepend.client.test;
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
import com.minecade.deepend.game.GameServer;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.object.ObjectManager;
import com.minecade.deepend.object.ProviderGroup;
import com.minecade.deepend.request.*;
import com.minecade.deepend.values.ValueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
        _registerInitialRequests(client);
    }

    public void _registerInitialRequests(DeependClient client) {
        // This is defined outside of the connection itself, as this
        // shouldn't be re-created
        EnumBitField<Byte, GameCategory> categoryEnumBitField = new EnumBitField<>(GameCategory.class);
        int field = categoryEnumBitField.construct(GameCategory.PLAYERS, GameCategory.SERVERS);

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

        DataRequest.DataRecipient nullRecipient = o -> {};

        // Just a simple debug statement for categories, should
        // be used to add new requests
        StatusRequest.StatusRecipient statusRecipient = f -> categoryEnumBitField.extract(f)
                .forEach(category -> Logger.get().info("Updated category: " + category.name()));

        //
        //  END OF RE-USABLE LAMBDAS
        //

        // This will simply fetch the updated categories
        client.addPendingRequest(new StatusRequest(field, statusRecipient));

        // These three requests does the same thing, it just
        // shows that the syntax can be adapted to many different
        // usage scenarios
        // client.addPendingRequest(GamePlayer.requestPlayer("jeb_,notch", currentConnection(), serverAnnouncement));
        // client.addPendingRequest(GamePlayer.requestPlayerNames("*", currentConnection(), serverAnnouncement));

        /*

        UPDATE_CATEGORIES
            -> Add to category list

        GET_SERVER_NAMES
            -> Update each server on its own

         */

        // client.addPendingRequest(GamePlayer.requestPlayers(new StringList("jeb_", "notch"), currentConnection(), serverAnnouncement));

        // This makes the client shut down, quite handy
        List<String> categories = new ArrayList<>();

        final RequestChain requestChain = new RequestChain();
        requestChain.add(
            new GetRequest((x) -> x.forEach(y -> {
                Logger.get().info("Found category: " + y.toString());
                categories.add(y.toString());
            })) {
                @Override
                protected void buildRequest(DeependBuf buf) {
                    buf.writeByte(GameCategory.SERVER_CATEGORIES);
                    buf.writeString("*");
                }
            }
        ).add(
            new DummyDataRequest(() -> {
                Logger.get().info("Searching for servers");
                for (String category : categories) {
                    requestChain.add(
                        new GetRequest((servers) -> {
                            servers.forEach(server -> Logger.get().info("Found: " + server + " in " + category));
                        }) {
                            @Override
                            protected void buildRequest(DeependBuf buf) {
                                buf.writeByte(GameCategory.CATEGORY_SERVERS);
                                buf.writeString(category);
                            }
                        }
                    );
                }
            })
        ).addLast(new ShutdownRequest());

        DeependClient.getInstance().addPendingRequest(requestChain);
    }

    @Override
    public void registerObjectMappings(ObjectManager objectManager) {
        // Will make sure that all requests using the category PLAYERS
        // returns a GamePlayer instance, rather than returning
        // raw data
        ObjectManager.instance.registerMapping(GamePlayer.class);
        ObjectManager.instance.registerMapping(GameServer.class);
    }

    @Override
    public void registerFactories() {
        ValueFactory.addValueFactory(ValueFactory.FactoryType.DATA_TYPE, new ValueFactory<>(ProviderGroup.fromEnumClass(GameCategory.class), GameCategory.UNKNOWN));
    }
}
