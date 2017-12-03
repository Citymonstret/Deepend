package com.minecade.deepend.channels;

import com.minecade.deepend.connection.DeependConnection;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.reflection.ReflectionMethod;

/**
 * Created 3/5/2016 for Deepend
 *
 * @author Citymonstret
 */
public final class ChannelBody extends DeependChannel
{

    private final ReflectionMethod<Void> method;

    protected ChannelBody(final Channel channel, final ReflectionMethod<Void> method)
    {
        super( channel );
        this.method = method;
    }

    @Override
    public void act(final DeependConnection connection, final DeependBuf buf)
    {
        this.method.handle( connection, buf );
    }
}
