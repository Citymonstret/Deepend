package com.minecade.deepend.bukkit;

import com.minecade.deepend.client.DeependClient;
import com.minecade.deepend.object.DeependObject;
import com.minecade.deepend.object.ObjectManager;
import com.minecade.deepend.request.PendingRequest;
import com.minecade.deepend.request.ShutdownRequest;
import com.minecade.deepend.values.ValueFactory;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class DeependBukkit extends JavaPlugin {

    protected static Logger logger;

    private List<Class<? extends DeependObject>> objects = new ArrayList<>();
    private Map<ValueFactory.FactoryType, ValueFactory> factories = new HashMap<>();
    private List<PendingRequest> requests = new ArrayList<>();

    boolean created = false;

    public void addMapping(Class<? extends DeependObject> deependObject) {
        this.objects.add(deependObject);
    }

    public void addFactory(ValueFactory.FactoryType type, ValueFactory factory) {
        this.factories.put(type, factory);
    }

    public void addRequest(PendingRequest request) {
        this.requests.add(request);
    }

    @Override
    public void onEnable() {
        logger = this.getLogger();
        com.minecade.deepend.logging.Logger.logHandler = BukkitLogger.class;
    }

    @SneakyThrows
    public void create() {
        if (created) {
            throw new IllegalAccessException("Cannot re-create client");
        }
        getServer().getScheduler().runTaskAsynchronously(this,
                () -> new DeependClient(new DeependClient.DeependClientApplication() {
                    @Override
                    public void registerInitialRequests(DeependClient client) {
                        requests.forEach(client::addPendingRequest);
                        requests = null; // Delete reference
                    }

                    @Override
                    public void registerObjectMappings(ObjectManager objectManager) {
                        objects.forEach(objectManager::registerMapping);
                        objects = null; // Delete reference
                    }

                    @Override
                    public void registerFactories() {
                        factories.forEach(ValueFactory::addValueFactory);
                        factories = null; // Delete reference
                    }
                }));
        created = true;
    }

    @Override
    public void onDisable() {
        DeependClient.getInstance().addPendingRequest(new ShutdownRequest());
        //noinspection StatementWithEmptyBody
        while (!DeependClient.getInstance().isShutdown()) {}
    }

    public boolean isWriteLocked() {
        return !DeependClient.getCurrentConnection().isAuthenticated();
    }
}
