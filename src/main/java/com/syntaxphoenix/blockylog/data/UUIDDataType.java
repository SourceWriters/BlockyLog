package com.syntaxphoenix.blockylog.data;

import java.nio.ByteBuffer;
import java.util.UUID;

import net.sourcewriters.minecraft.versiontools.shaded.syntaxapi.data.DataAdapterContext;
import net.sourcewriters.minecraft.versiontools.shaded.syntaxapi.data.DataType;

public final class UUIDDataType implements DataType<byte[], UUID> {
    
    public static final UUIDDataType TYPE = new UUIDDataType();
    
    private UUIDDataType() {}

    @Override
    public Class<UUID> getComplex() {
        return UUID.class;
    }

    @Override
    public Class<byte[]> getPrimitive() {
        return byte[].class;
    }

    @Override
    public byte[] toPrimitive(DataAdapterContext var1, UUID var2) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(var2.getMostSignificantBits());
        buffer.putLong(var2.getLeastSignificantBits());
        return buffer.array();
    }

    @Override
    public UUID fromPrimitive(DataAdapterContext var1, byte[] var2) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        long most = buffer.getLong();
        long least = buffer.getLong();
        return new UUID(most, least);
    }

}
