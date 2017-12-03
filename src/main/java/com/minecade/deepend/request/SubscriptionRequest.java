package com.minecade.deepend.request;

import com.minecade.deepend.bytes.ByteProvider;
import com.minecade.deepend.channels.Channel;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.logging.Logger;
import lombok.Getter;
import lombok.Setter;

public final class SubscriptionRequest extends PendingRequest
{

    private static final byte TYPE = 1;
    @Setter
    @Getter
    private static DataRequest.DataRecipient subscriptionRecipient =
            (list) -> Logger.get().error( "Subscription recipient not setup!" );
    @Getter
    public final ByteProvider[] channels;
    @Getter
    public final String string;

    public SubscriptionRequest(final ByteProvider... channels)
    {
        super( Channel.CHECK_DATA );

        this.channels = channels;
        StringBuilder builder = new StringBuilder();
        for ( int i = 0; i < channels.length; i++ )
        {
            builder.append( channels[ i ].getValue().toString() );
            if ( ( i + 1 ) < channels.length )
            {
                builder.append( "," );
            }
        }
        this.string = builder.toString();
    }

    public static DataRequest getDummyRequest()
    {
        return new DataRequest( Channel.UNKNOWN, subscriptionRecipient )
        {
            @Override
            public int getIndex()
            {
                return super.getIndex();
            }
        };
    }

    @Override
    final protected void makeRequest(DeependBuf buf)
    {
        buf.writeByte( TYPE );
        buf.writeString( getString() );
    }
}
