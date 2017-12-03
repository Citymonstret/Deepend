package com.minecade.deepend.prot;

import com.minecade.deepend.nativeprot.NativeBuf;
import com.minecade.deepend.nativeprot.NativeObj;
import com.minecade.deepend.util.GzipUtil;

import java.nio.ByteBuffer;

/**
 * Created 3/14/2016 for Deepend
 *
 * @author Citymonstret
 */
public class JavaProtocol implements Protocol
{

    public static void test()
    {
        JavaProtocol protocol = new JavaProtocol();

        NativeObj[] objects = new NativeObj[ 3 ];
        objects[ 0 ] = new NativeObj( "Test" );
        objects[ 1 ] = new NativeObj( 32 );
        objects[ 2 ] = new NativeObj( (byte) 10 );

        NativeBuf wBuf = protocol.writeNativeBuf( 0, new NativeBuf( objects ) );
        NativeBuf rBuf = protocol.readNativeBuf( 0, wBuf.getBytes() );

        System.out.println( "String: " + rBuf.getObjects()[ 0 ].getS() );
        System.out.println( "Int: " + rBuf.getObjects()[ 1 ].getI() );
        System.out.println( "Byte: " + rBuf.getObjects()[ 2 ].getB() );
    }

    public static int bytesToInt(final byte[] bytes)
    {
        int result = 0;
        for ( int i = 0; i < 4; i++ )
        {
            result = ( result << 4 ) + bytes[ i ];
        }
        return result;
    }

    @Override
    public NativeBuf readNativeBuf(int i, byte[] b)
    {
        b = GzipUtil.extract( b );

        final Offset offset = new Offset();
        int objSize = bytesToInt( b, offset );

        NativeObj[] objs = new NativeObj[ objSize ];

        for ( int o = 0; o < objSize; o++ )
        {
            int required = offset.get() + 8;
            if ( b.length < required )
            {
                break;
            }
            int type = bytesToInt( b, offset );
            int osize = bytesToInt( b, offset );
            if ( type == NativeObj.TYPE_INT )
            {
                int in = bytesToInt( b, offset );
                objs[ o ] = new NativeObj( type, in, Byte.MIN_VALUE, "" );
            } else if ( type == NativeObj.TYPE_BYTE )
            {
                byte by = bytesToByte( b, offset );
                objs[ o ] = new NativeObj( type, 0, by, "" );
            } else if ( type == NativeObj.TYPE_STRING )
            {
                char[] chars = bytesToChars( b, osize, offset );
                objs[ o ] = new NativeObj( type, 0, Byte.MIN_VALUE, new String( chars ) );
            }
        }

        return new NativeBuf( objs );
    }

    @Override
    public NativeBuf writeNativeBuf(int i, NativeBuf buf)
    {
        int allocSize = 0;

        int bufSize = buf.getObjects().length;
        allocSize += 4;

        for ( NativeObj obj : buf.getObjects() )
        {
            allocSize += 8; // type int + size int
            switch ( obj.getType() )
            {
                case NativeObj.TYPE_BYTE:
                    allocSize += 1;
                    break;
                case NativeObj.TYPE_INT:
                    allocSize += 4;
                    break;
                case NativeObj.TYPE_STRING:
                    allocSize += obj.getS().getBytes().length;
                    break;
                default:
                    break;
            }
        }

        ByteBuffer buffer = ByteBuffer.allocate( allocSize );
        buffer.put( intToBytes( bufSize ) );

        for ( NativeObj obj : buf.getObjects() )
        {
            buffer.put( intToBytes( obj.getType() ) );
            switch ( obj.getType() )
            {
                case NativeObj.TYPE_BYTE:
                    buffer.put( intToBytes( 1 ) );
                    buffer.put( obj.getB() );
                    break;
                case NativeObj.TYPE_INT:
                    buffer.put( intToBytes( 4 ) );
                    buffer.put( intToBytes( obj.getI() ) );
                    break;
                case NativeObj.TYPE_STRING:
                    buffer.put( intToBytes( obj.getS().getBytes().length ) );
                    buffer.put( charsToBytes( obj.getS().toCharArray() ) );
                    break;
                default:
                    break;
            }
        }

        buf.setBytes( GzipUtil.compress( buffer.array() ) );

        return buf;
    }

    char[] bytesToChars(final byte[] bytes, final int size, final Offset offset)
    {
        char[] chars = new char[ size ];
        for ( int i = 0; i < size; i++ )
        {
            chars[ i ] = (char) bytes[ offset.get() + i ];
        }
        offset.add( size );
        return chars;
    }

    byte[] charsToBytes(final char[] chars)
    {
        byte[] bytes = new byte[ chars.length ];
        for ( int i = 0; i < chars.length; i++ )
        {
            bytes[ i ] = (byte) chars[ i ];
        }
        return bytes;
    }

    byte bytesToByte(final byte[] bytes, final Offset offset)
    {
        byte b = bytes[ offset.get() ];
        offset.add( 1 );
        return b;
    }

    int bytesToInt(final byte[] bytes, final Offset offset)
    {
        int result = 0;
        for ( int i = 0; i < 4; i++ )
        {
            result = ( result << 8 ) + bytes[ offset.get() + i ];
        }
        offset.add( 4 );
        return result;
    }

    byte[] intToBytes(final int value)
    {
        return new byte[]{
                (byte) ( value >>> 24 ),
                (byte) ( value >>> 16 ),
                (byte) ( value >>> 8 ),
                (byte) ( value )
        };
    }

    class Offset
    {

        int offset = 0;

        void set(int n)
        {
            offset = n;
        }

        void add(int n)
        {
            set( offset + n );
        }

        int get()
        {
            return offset;
        }
    }
}
