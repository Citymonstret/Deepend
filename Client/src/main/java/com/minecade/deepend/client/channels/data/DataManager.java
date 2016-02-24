package com.minecade.deepend.client.channels.data;

import com.minecade.deepend.client.DeependClient;
import com.minecade.deepend.object.ByteProvider;
import com.minecade.deepend.object.DeependObject;
import com.minecade.deepend.object.ObjectManager;
import com.minecade.deepend.request.ObjectCallback;
import com.minecade.deepend.request.ObjectGetRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class DataManager<T extends DeependObject, O extends ByteProvider> {

    @NonNull
    @Getter
    private final Class<T> clazz;

    private T instance;

    @NonNull
    @Getter
    private final O type;

    @Getter(AccessLevel.PROTECTED)
    private final Map<String, T> internalStorage = new ConcurrentHashMap<>();

    public void addData(@NonNull T object) {
        internalStorage.put(object.toString(), object);
    }

    public Optional<T> getObject(@NonNull String key) {
        if (!internalStorage.containsKey(key)) {
            return Optional.empty();
        }
        return Optional.of(internalStorage.get(key));
    }

    public void getObject(@NonNull String key, @NonNull ObjectCallback<T> callback) {
        Optional<T> result = getObject(key);
        if (result.isPresent()) {
            callback.act(result.get());
        } else {
            DeependClient.getInstance().addPendingRequest(
                    new ObjectGetRequest(key, getInstance(), (data) -> data.stream().filter(clazz::isInstance)
                            .forEach(item -> callback.act(clazz.cast(item))), DeependClient.currentConnection)
            );
        }
    }

    protected T getInstance() {
        if (instance == null) {
            instance = ObjectManager.instance.getInstance(clazz);
        }
        return instance;
    }
}