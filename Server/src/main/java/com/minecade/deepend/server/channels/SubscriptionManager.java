package com.minecade.deepend.server.channels;

import com.minecade.deepend.bytes.ByteProvider;
import com.minecade.deepend.channels.Channel;
import com.minecade.deepend.data.DataHolder;
import com.minecade.deepend.data.DataObject;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.nativeprot.NativeBuf;
import com.minecade.deepend.object.GenericResponse;
import com.minecade.deepend.pipeline.DeependContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SubscriptionManager
{

    private static final Map<Byte, ByteProvider> providerMap = new HashMap<>();
    private static Map<String, Subscriber> subscriberMap = new HashMap<>();

    private static ByteProvider getByteProvider(byte b)
    {
        if ( !providerMap.containsKey( b ) )
        {
            providerMap.put( b, () -> b );
        }
        return providerMap.get( b );
    }

    public static void addSubscription(String host, byte provider)
    {
        Subscriber subscriberItem;
        if ( subscriberMap.containsKey( host ) )
        {
            subscriberItem = subscriberMap.get( host );
        } else
        {
            subscriberItem = new Subscriber( host );
        }
        subscriberItem.addSubscription( getByteProvider( provider ) );
        if ( subscriberMap.containsKey( host ) )
        {
            return;
        }
        subscriberMap.put( host, subscriberItem );
    }

    public static void act(Channel channel, byte category, DataHolder holder)
    {
        new Thread()
        {
            @Override
            public void run()
            {
                getSubscribers( getByteProvider( category ) ).parallelStream().forEach( subscriber -> send( channel, getByteProvider( category ), holder, subscriber.getHost() ) );
            }
        }.start();
    }

    private static Set<Subscriber> getSubscribers(final ByteProvider provider)
    {
        Set<Subscriber> subscribers = new HashSet<>();
        subscriberMap.values().stream().filter( subscriber -> subscriber.getCategories().contains( provider ) ).forEach( subscribers::add );
        return subscribers;
    }

    private static void send(final Channel channel, final ByteProvider category, final DataHolder holder, final String host)
    {
        Logger.get().info( "Sending subscription message" );
        DeependBuf out = new NativeBuf();
        out.writeInt( Channel.GET_DATA );
        out.writeString( "subscription" );
        Logger.get().info( "Category: " + category );
        out.writeByte( channel == Channel.REMOVE_DATA );
        out.writeByte( GenericResponse.SUCCESS );
        out.writeByte( category );
        out.writeInt( holder.size() ); // TODO: Allow for more than one object
        // Send object(s)
        holder.forEach( (key, object) -> {
            if ( object instanceof DataHolder )
            {
                Logger.get().error( "Trying to send data holder, not supported" );
            } else
            {
                DataObject o = (DataObject) object;
                out.writeString( o.getName() );
                out.writeString( o.getValue() );
            }
        } );
        Socket socket;
        try
        {
            socket = new Socket( host, 4345 );
        } catch ( final Exception e )
        {
            Logger.get().error( "Failed to connect to remote subscription service", e );
            return;
        }
        DeependContext deependContext = new DeependContext( null, socket, (InetSocketAddress) socket.getRemoteSocketAddress() );
        out.writeAndFlush( deependContext );
        try
        {
            socket.close();
        } catch ( IOException e )
        {
            Logger.get().error( "Failed to close subscription socket", e );
        }
    }

    @RequiredArgsConstructor
    private static final class Subscriber
    {

        @Getter
        private final String host;
        @Getter
        private List<ByteProvider> categories = new ArrayList<>();

        private void addSubscription(ByteProvider provider)
        {
            this.categories.add( provider );
        }
    }
}
