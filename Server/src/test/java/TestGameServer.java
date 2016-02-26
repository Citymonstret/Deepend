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

import com.minecade.deepend.channels.ChannelManager;
import com.minecade.deepend.data.DataHolder;
import com.minecade.deepend.data.DataManager;
import com.minecade.deepend.data.DataObject;
import com.minecade.deepend.data.MirrorDataHolder;
import com.minecade.deepend.game.GameCategory;
import com.minecade.deepend.values.ValueFactory;
import com.minecade.deepend.server.DeependServer;
import com.minecade.deepend.server.channels.impl.*;

import static com.minecade.deepend.game.GameCategory.*;

/**
 * Created 2/23/2016 for Deepend
 *
 * @author Citymonstret
 */
public class TestGameServer implements DeependServer.DeependServerApplication {

    public static void main(String[] args) {
        new DeependServer(8000, new TestGameServer()).run();
    }

    @Override
    public void registerDataHolders(DataManager dataManager) {
        DataManager.createDataHolders(PLAYERS, PLAYER_SERVERS, PROXIES);
        DataManager.instance.registerDataHolder(new MirrorDataHolder(
                GameCategory.PLAYER_SERVERS.name(),
                DataManager.instance.getDataHolder(GameCategory.PLAYERS.name()),
                (x) -> {
                    if (x instanceof DataHolder) {
                        DataHolder temp = (DataHolder) x;
                        return new DataObject(temp.getIdentifier(),
                                ((DataObject) temp.get("currentServer")).getValue());
                    }
                    return null;
                }
        ), GameCategory.PLAYER_SERVERS);
        DataManager.instance.registerDataHolder(new MirrorDataHolder(
                GameCategory.SERVER_PLAYERS.name(),
                DataManager.instance.getDataHolder(GameCategory.PLAYERS.name()),
                (x) -> {
                    if (x instanceof DataHolder) {
                        DataHolder temp = (DataHolder) x;
                        DataHolder server = new DataHolder(((DataObject) temp.get("currentServer")).getValue());
                        server.put(temp.getIdentifier(), temp.getFallback());
                        return server;
                    }
                    return null;
                }
        ), GameCategory.SERVER_PLAYERS);
    }

    @Override
    public void registerByteFactories() {
        ValueFactory.addValueFactory(ValueFactory.FactoryType.DATA_TYPE, new ValueFactory(GameCategory.class, GameCategory.UNKNOWN));
    }

    @Override
    public void registerChannels(ChannelManager channelManager) {
        channelManager.addChannel(new Authentication());
        channelManager.addChannel(new GetData());
        channelManager.addChannel(new DeleteData());
        channelManager.addChannel(new AddData());
    }

    @Override
    public void after() {
        { // Register data holders using a builder pattern
            DataHolder.DataHolderInitalizer.builder()
                    .name("notch")
                    .object("currentServer", "lobby1")
                    .object("name", null)
                    .object("uuid", "1-3-3-7")
                    .fallback("uuid")
                    .build().register(PLAYERS);
        }
        { // This is another way to do it
            DataHolder jeb_ = new DataHolder("jeb_");
            jeb_.put("currentServer", "lobby1");
            jeb_.put("uuid", "1-9-9-3");
            jeb_.put("name", null);
            jeb_.setFallback("uuid");
            DataManager.instance.getDataHolder(GameCategory.PLAYERS).put("jeb_", jeb_);
        }
    }
}
