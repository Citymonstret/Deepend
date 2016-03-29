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
public class GameServer extends DeependObject {

    @Getter
    @ObjectProperty(name = "serverName")
    protected String serverName = "";

    @Getter
    @ObjectProperty(name = "gameType")
    protected String serverType = "";

    @Getter
    @ObjectProperty(name = "playerCount")
    protected int playerCountCurrent = 0;

    @Getter
    @ObjectProperty(name = "maxCount")
    protected int playerCountMax = 0;

    public GameServer() {
        super(GameCategory.SERVERS, true, GameServer.class);
    }

    public GameServer(String name) {
        super(GameCategory.SERVERS, false, GameServer.class);
        this.serverName = name;
        // We'll need to delay this until
        // the field has been set
        scan(GameServer.class);
    }

    @Override
    public String toString() {
        return serverName;
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

    public static GetRequest requestServers(List<String> names, UUIDProvider provider, ServerCallback callback) {
        StringBuilder name = new StringBuilder();
        Iterator<String> i = names.iterator();
        while (i.hasNext()) {
            name.append(i.next());
            if (i.hasNext()) {
                name.append(",");
            }
        }

        DeependObject object = new GameServer() {
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
                    if (o instanceof GameServer) {
                        callback.act((GameServer) o);
                    } else {
                        Logger.get()
                                .error("Unknown type recieved when reading servers; "
                                        + o.getClass().getName());
                    }
                }
            }
        };

        return new ObjectGetRequest(name.toString(), object, recipient);
    }

    public static GetRequest requestServer(String name, UUIDProvider provider, ServerCallback callback) {
        if (name.contains(",")) {
            return requestServers(new StringList(name.split(",")), provider, callback);
        }
        return requestServers(Collections.singletonList(name), provider, callback);
    }

    public interface ServerCallback extends ObjectCallback<GameServer> {
        void act(GameServer server);
    }
}
