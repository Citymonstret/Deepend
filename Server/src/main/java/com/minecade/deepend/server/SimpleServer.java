package com.minecade.deepend.server;

import com.minecade.deepend.channels.ChannelManager;
import com.minecade.deepend.data.DataManager;
import com.minecade.deepend.game.GameCategory;
import com.minecade.deepend.object.ProviderGroup;
import com.minecade.deepend.server.channels.impl.*;
import com.minecade.deepend.values.ValueFactory;

import static com.minecade.deepend.game.GameCategory.SERVERS;

public final class SimpleServer implements DeependServer.DeependServerApplication {

    public static void main(String[] args) {
        new SimpleServer(args);
    }

    private SimpleServer(String[] args) {
        new DeependServer(args, this).run();
    }

    @Override
    public void registerDataHolders(DataManager dataManager) {
        DataManager.createDataHolders( SERVERS);
    }

    @Override
    public void registerFactories() {
        ValueFactory.addValueFactory(ValueFactory.FactoryType.DATA_TYPE, new ValueFactory<>(ProviderGroup.fromEnumClass(GameCategory.class), GameCategory.UNKNOWN));
    }

    @Override
    public void registerChannels(ChannelManager channelManager) {
        channelManager.addChannel(new Authentication());
        channelManager.addChannel(new GetData());
        channelManager.addChannel(new DeleteData());
        channelManager.addChannel(new AddData());
        channelManager.addChannel(new CheckData());
    }

    @Override
    public void after(DeependServer context) {
        {
            /*
            ADDS DATABASE FUNCTIONALITY - DO WE WANT THIS?

            context.storageBase = new SQLite("server_storage");
            context.storageBase.setup();

            new Timer("SaveData", true).scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    context.storageBase.saveDataHolder("", DataManager.instance.getDataHolder(SERVERS.name()));
                }
            }, 0L, 1000 * 60 * 30);
            */
        }
    }
}