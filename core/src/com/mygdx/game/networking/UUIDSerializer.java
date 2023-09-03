package com.mygdx.game.networking;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

import java.util.UUID;

public class UUIDSerializer extends FieldSerializer<UUID> {

    public UUIDSerializer(Kryo kryo) {
        super(kryo, UUID.class);
    }

    @Override
    public void write(Kryo kryo, Output output, UUID object) {
        output.writeLong(object.getMostSignificantBits());
        output.writeLong(object.getLeastSignificantBits());
    }

    @Override
    public UUID read(Kryo kryo, Input input, Class<? extends UUID> type) {
        long mostSigBits = input.readLong();
        long leastSigBits = input.readLong();
        return new UUID(mostSigBits, leastSigBits);
    }
}
