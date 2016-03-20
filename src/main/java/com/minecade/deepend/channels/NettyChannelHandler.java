package com.minecade.deepend.channels;

import com.minecade.deepend.ConnectionFactory;
import com.minecade.deepend.connection.DeependConnection;
import com.minecade.deepend.connection.SimpleAddress;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.nativeprot.NativeBuf;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * Created 3/20/2016 for Deepend
 *
 * @author Citymonstret
 */
public abstract class NettyChannelHandler extends ChannelInboundHandlerAdapter implements DeependChannelHandler {

    @Override
    final public void channelRead(final ChannelHandlerContext context, Object message) {
        try {
            NativeBuf in = new NativeBuf((ByteBuf) message);
            NativeBuf out = new NativeBuf(context.alloc().buffer());
            try {
                this.handle(in, out, context);
            } catch (final Exception e) {
                Logger.get().error("Failed to handle channel", e);
            }
            // Write and listen for receiving of data
            final ChannelFuture f = out.writeAndFlush(context);
            f.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    assert f == future;
                    context.close();
                }
            });
        } finally {
            ReferenceCountUtil.release(message);
        }
    }

    @Override
    public DeependBuf generateBuf(Object context) {
        return new NativeBuf(((ChannelHandlerContext) context).alloc().buffer());
    }

    @Override
    public DeependConnection generateConnection(Object context) {
        return new DeependConnection(new SimpleAddress(
                ((InetSocketAddress)((ChannelHandlerContext) context).channel().remoteAddress()).getHostName()
        ));
    }

    @Override
    public DeependConnection generateConnection(Object context, UUID uuid) {
        return ConnectionFactory.instance.getOrCreate(
                ((ChannelHandlerContext) context).channel().remoteAddress(), uuid
        );
    }

    @Override
    final public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        cause.printStackTrace();
        context.close();
    }

}
