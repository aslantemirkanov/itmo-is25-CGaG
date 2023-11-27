package ru.squad1332.cg.parsers;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.CRC32;

public class Chunk {
    String type;
    byte[] data;

    public Chunk(String type, byte[] data) {
        this.type = type;
        this.data = data;
    }

    public static Chunk readChunk(FileInputStream fis) throws IOException {
        byte[] lengthBytes = new byte[4];
        fis.read(lengthBytes);
        int length = ByteBuffer.wrap(lengthBytes).order(ByteOrder.BIG_ENDIAN).getInt();

        byte[] typeBytes = new byte[4];
        fis.read(typeBytes);
        String type = new String(typeBytes, "UTF-8");

        byte[] data = new byte[length];
        fis.read(data);

        byte[] crcBytes = new byte[4];
        fis.read(crcBytes);
        long crc = ByteBuffer.wrap(crcBytes).order(ByteOrder.BIG_ENDIAN).getInt() & 0xffffffffL;

        CRC32 crc32 = new CRC32();
        crc32.update(typeBytes);
        crc32.update(data);
        if (crc != crc32.getValue()) {
            throw new IOException("CRC mismatch for chunk: " + type);
        }

        return new Chunk(type, data);
    }

}