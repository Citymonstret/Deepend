package com.minecade.deepend.request;

import com.minecade.deepend.channels.ChannelHandler;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.pipeline.DeependContext;
import com.minecade.deepend.util.Assert;
import lombok.Synchronized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestChain extends Request
{

    private final Cloud requests;
    private final AtomicInteger requestCount;

    private long idPool = 1L;

    {
        requests = new Cloud();
        requestCount = new AtomicInteger( 0 );
    }

    public RequestChain add(DataRequest request)
    {
        return this.add( () -> request );
    }

    public RequestChain add(RequestPromise promise)
    {
        this.requests.add( promise );
        this.requestCount.incrementAndGet();
        return this;
    }

    @Override
    public boolean handle(DeependContext context, ChannelHandler handler)
    {
        int lastNum = requestCount.get();
        if ( lastNum == 0 )
        {
            return true;
        }

        RequestPromise temp;
        while ( ( temp = requests.get() ) != null )
        {
            if ( temp instanceof MultiplePromise )
            {
                Collection<RequestPromise> generated =
                        ( (MultiplePromise) temp ).generate();
                Logger.get().info( "Generated " + generated.size() + " requests" );
                generated.forEach( this::add );
                continue;
            }

            DataRequest request = temp.makeRequest();

            if ( request == null )
            {
                Logger.get().info( "skipping null reqeust" );
                continue;
            }

            request.addRecipient( data -> {
                requestCount.decrementAndGet();
                Logger.get().debug( "Finished request" );
            } );
            if ( !request.handle( context, handler ) )
            {
                Logger.get().error( "Something went very wrong :///" );
            }
            while ( lastNum == requestCount.get() )
            {
                // Wait
            }
        }

        Logger.get().info( "Done!" );
        return true;
    }

    public interface MultiplePromise extends RequestPromise
    {

        default DataRequest makeRequest()
        {
            return null;
        }

        Collection<RequestPromise> generate();
    }

    public interface RequestPromise
    {

        DataRequest makeRequest();
    }

    private class Cloud
    {

        private final List<RequestPromise> requestMap;
        private int requestID = 0;

        Cloud()
        {
            this.requestMap = new ArrayList<>();
        }

        @Synchronized
        void add(final RequestPromise request)
        {
            Assert.notNull( request );
            requestMap.add( request );
        }

        @Synchronized
        public RequestPromise get()
        {
            int index = requestID++;
            if ( requestMap.isEmpty() || requestMap.size() <= index )
            {
                return null;
            }
            return requestMap.get( index );
        }
    }
}
