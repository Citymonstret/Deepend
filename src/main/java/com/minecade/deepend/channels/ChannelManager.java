/*
 * Copyright 2016 Minecade
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.minecade.deepend.channels;

import com.minecade.deepend.reflection.AnnotatedMethod;
import com.minecade.deepend.reflection.AnnotationUtil;
import com.minecade.deepend.reflection.ReflectionMethod;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This manages all channels
 *
 * @author Citymonstret
 */
public class ChannelManager {

    private boolean locked = false;

    private final Map<Channel, DeependChannel> channelMap = new ConcurrentHashMap<>();
    private final Map<Channel, ChannelStatus> channelStatusMap = new ConcurrentHashMap<>();

    /**
     * The global instance
     */
    public static final ChannelManager instance = new ChannelManager();

    ChannelManager() {}

    /**
     * Register a channel, can only
     * be done before the manager
     * gets locked
     *
     * @param channel Channel to add
     */
    @SneakyThrows(RuntimeException.class)
    public void addChannel(@NonNull final DeependChannel channel) {
        if (locked) {
            throw new RuntimeException("Cannot add channels to locked manager");
        }
        channelMap.put(channel.getChannelType(), channel);
        channelStatusMap.put(channel.getChannelType(), new ChannelStatus(channel.getChannelType()));
    }

    public void generate(@NonNull Object instance) {
        this.generate(instance.getClass(), instance);
    }

    public void generate(@NonNull final Class<?> clazz, Object instance) {
        final Collection<AnnotatedMethod<ChannelListener>> annotatedMethods = AnnotationUtil.getAnnotatedMethods(ChannelListener.class, clazz);

        for (final AnnotatedMethod<ChannelListener> annotatedMethod : annotatedMethods) {
            final Method m =  annotatedMethod.getMethod();

            if (!void.class.equals(m.getReturnType())) {
                new IllegalArgumentException("M doesn't return null").printStackTrace();
            } else {
                addChannel(new ChannelBody(annotatedMethod.getAnnotation().channel(), new ReflectionMethod<>(m, instance, Void.class)));
            }
        }
    }

    /**
     * Get the channel implementation
     * @param channel Channel Enum
     * @return Implementation of the requested channel
     */
    public DeependChannel getChannel(@NonNull Channel channel) {
        return channelMap.get(channel);
    }

    public ChannelStatus getChannelStatus(@NonNull Channel channel) {
        return channelStatusMap.get(channel);
    }

    /**
     * Lock the manager,
     * which means that no
     * new channels may
     * be added
     */
    public void lock() {
        locked = true;
    }
}
