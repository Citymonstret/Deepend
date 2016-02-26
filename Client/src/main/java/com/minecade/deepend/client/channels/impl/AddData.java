package com.minecade.deepend.client.channels.impl;

import com.minecade.deepend.channels.Channel;
import com.minecade.deepend.channels.DeependChannel;
import com.minecade.deepend.connection.DeependConnection;
import com.minecade.deepend.data.DataObject;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.object.GenericResponse;
import com.minecade.deepend.request.DataRequest;
import com.minecade.deepend.values.ValueFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Citymonstret
 */
public class AddData extends DeependChannel {

    public AddData() {
        super(Channel.ADD_DATA);
    }

    @Override
    public void act(DeependConnection connection, DeependBuf buf) {
        DeependBuf in = connection.getBuf("in");
        // ByteBuf in = connection.getObject("in", ByteBuf.class);

        String getID = in.getString();
        Logger.get().debug("Getting response for: " + getID);
        DataRequest request = DataRequest.getRequest(getID);

        if (request == null) {
            Logger.get().error("Got response for unregistered request, throwing!");
            return;
        }

        GenericResponse response = in.getResponse();

        if (response == GenericResponse.SUCCESS) {
            byte categoryByte = in.getByte();

            String category = ValueFactory.getFactory(ValueFactory.FactoryType.DATA_TYPE)
                    .getName(categoryByte);

            Logger.get().debug("Type: " + category);

            int num = in.getInt();
            Logger.get().debug("Found " + num + " objects");

            List<Object> objects = new ArrayList<>();

            for (int i = 0; i < num; i++) {
                String identifier = in.getString();
                String data = in.getString();
                Logger.get().info("Data added: \"" + identifier + "\":\"" + data + "\"");
                objects.add(new DataObject(identifier, data));
            }

            request.call(objects);
            request.delete();
        } else {
            Logger.get().error("Failed to add data: \""+ in.getString() + "\"");
        }
    }
}
