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

package com.minecade.deepend.game;

import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.object.DeependObject;
import com.minecade.deepend.object.ObjectProperty;
import com.minecade.deepend.object.StringList;
import com.minecade.deepend.request.*;
import lombok.Getter;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Citymonstret
 */
public class GamePlayer extends DeependObject {

    @Getter
    @ObjectProperty(name = "currentServer")
    protected String playerServer = "";

    @Getter
    @ObjectProperty(name = "uuid")
    protected String playerID = "";

    @Getter
    @ObjectProperty(name = "name")
    protected String playerName = "";

    public GamePlayer() {
        super(GameCategory.PLAYERS, true, GamePlayer.class);
    }

    public GamePlayer(String name) {
        super(GameCategory.PLAYERS, false, GamePlayer.class);
        this.playerName = name;
        // We'll need to delay this until
        // the field has been set
        scan(GamePlayer.class);
    }

    @Override
    public String toString() {
        return playerName;
    }

    @Override
    public void write(DeependBuf buf) {
        writeValues(buf);
    }

    @Override
    public void read(DeependBuf buf) {
        convertAndRead(buf);
    }

    @Override
    public void request(String key, DeependBuf buf) {
        buf.writeString(key);
        sendKeys(buf);
    }

    public static GetRequest requestPlayers(List<String> names, PlayerCallback callback) {
        StringBuilder name = new StringBuilder();
        Iterator<String> i = names.iterator();
        while (i.hasNext()) {
            name.append(i.next());
            if (i.hasNext()) {
                name.append(",");
            }
        }

        DeependObject object = new GamePlayer() {
            @Override
            public void request(String key, DeependBuf buf) {
                buf.writeString(name.toString());
                sendKeys(buf);
            }
        };

        DataRequest.DataRecipient recipient = data -> {
            if (data.isEmpty()) {
                callback.act(null);
            } else {
                for (Object o : data) {
                    if (o instanceof GamePlayer) {
                        callback.act((GamePlayer) o);
                    } else {
                        Logger.get()
                                .error("Unknown type received when reading players; "
                                        + o.getClass().getName());
                    }
                }
            }
        };

        return new ObjectGetRequest(name.toString(), object, recipient);
    }

    public static GetRequest requestPlayer(String name, UUIDProvider provider, PlayerCallback callback) {
        if (name.contains(",")) {
            return requestPlayers(new StringList(name.split(",")), callback);
        }
        return requestPlayers(Collections.singletonList(name), callback);
    }

    public String get(String key) {
        return (String) getValue(key);
    }

    public interface PlayerCallback extends ObjectCallback<GamePlayer> {
        void act(GamePlayer player);
    }
}
