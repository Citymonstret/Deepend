package com.minecade.deepend.request;

import com.minecade.deepend.channels.Channel;

/**
 * Created 2/26/2016 for Deepend
 *
 * @author Citymonstret
 */
public abstract class AddRequest extends DataRequest {

    public AddRequest(DataRecipient dataRecipient, UUIDProvider provider) {
        super(Channel.ADD_DATA, dataRecipient, provider);
    }

}
