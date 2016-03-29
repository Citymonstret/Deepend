package com.minecade.deepend.util;

import lombok.Synchronized;
import lombok.experimental.UtilityClass;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Created 3/26/2016 for Deepend
 *
 * @author Citymonstret
 */
@UtilityClass
public class GzipUtil {

    public static final boolean enabled = true;

    private static DeflaterCloud deflaterCloud;
    private static InflaterCloud inflaterCloud;

    static {
        if (enabled) {
            deflaterCloud = new DeflaterCloud();
            inflaterCloud = new InflaterCloud();
        }
    }

    public static byte[] compress(byte[] in) {
        if (!enabled) {
            return in;
        }
        int deflaterIndex = deflaterCloud.getIndex();
        Deflater deflater = deflaterCloud.get(deflaterIndex);
        byte[] output = null;
        deflater.setInput(in);
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(in.length)) {
            deflater.finish();
            byte[] buffer = new byte[2048];
            while (!deflater.finished()) {
                int count = deflater.deflate(buffer);
                byteArrayOutputStream.write(buffer, 0, count);
            }
            output = byteArrayOutputStream.toByteArray();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        deflaterCloud.setStatus(deflaterIndex, true);
        return output;
    }

    public static byte[] extract(byte[] in) {
        if (!enabled) {
            return in;
        }
        int inflaterIndex = inflaterCloud.getIndex();
        Inflater inflater = inflaterCloud.get(inflaterIndex);
        byte[] output = null;
        inflater.setInput(in);
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(in.length)) {
            inflater.finished();
            byte[] buffer = new byte[2048];
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                byteArrayOutputStream.write(buffer, 0, count);
            }
            output = byteArrayOutputStream.toByteArray();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        inflaterCloud.setStatus(inflaterIndex, true);
        return output;
    }

    private static final class InflaterCloud extends Cloud<Inflater> {

        @Override
        Inflater generate() {
            return new Inflater();
        }

        @Override
        void reset(int index) {
            map.get(index).reset();
        }
    }

    private static final class DeflaterCloud extends Cloud<Deflater> {

        @Override
        Deflater generate() {
            return new Deflater();
        }

        @Override
        void reset(int index) {
            map.get(index).reset();
        }
    }

    private static abstract class Cloud<T> {
        int index = 0;

        protected final Map<Integer, T> map = new ConcurrentHashMap<>();
        protected final Map<Integer, Boolean> statusMap = new ConcurrentHashMap<>();

        {
            add(generate(), true);
        }

        int add(T t, boolean status) {
            int index = this.index++;
            map.put(index, t);
            statusMap.put(index, status);
            return index;
        }

        abstract T generate();

        @Synchronized
        int getIndex() {
            int index = -1;

            for (Map.Entry<Integer, Boolean> entry : statusMap.entrySet()) {
                if (entry.getValue()) {
                    index = entry.getKey();
                    break;
                }
            }

            if (index == -1) {
                return add(generate(), false);
            } else {
                setStatus(index, false);
                return index;
            }
        }

        abstract void reset(int index);

        void setStatus(int index, boolean status) {
            if (status) {
                reset(index);
                cleanup();
            }
            statusMap.put(index, status);
        }

        private void cleanup() {
            new Thread() {
                @Override
                public void run() {
                    Stream<Map.Entry<Integer, Boolean>> stream = new HashMap<>(statusMap).entrySet().parallelStream().filter(Map.Entry::getValue);
                    if (stream.count() < 5) {
                        return;
                    }
                    stream.limit(stream.count() - 5).forEach(e -> {
                        statusMap.remove(e.getKey());
                        map.remove(e.getKey());
                    });
                }
            }.start();
        }

        T get(int index) {
            return map.get(index);
        }
    }
}
