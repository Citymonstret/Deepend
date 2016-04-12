package com.minecade.deepend.prot

import com.minecade.deepend.nativeprot.NativeBuf
import com.minecade.deepend.nativeprot.NativeObj

class JavaProtocolTest extends GroovyTestCase {

    void test() {
        def protocol = new JavaProtocol()

        def objects = new NativeObj[3]
        objects[0] = new NativeObj("Test")
        objects[1] = new NativeObj(32)
        objects[2] = new NativeObj((byte) 10)

        def writtenBuf =
                protocol.writeNativeBuf(0, new NativeBuf(objects))
        def readBuf =
                protocol.readNativeBuf(0, writtenBuf.getBytes())

        assert readBuf.getObjects()[0].getS().equals("Test")
        assert readBuf.getObjects()[1].getI() == 32
        assert readBuf.getObjects()[2].getB() == (byte) 10
    }

}
