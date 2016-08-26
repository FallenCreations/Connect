/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.api;

import codes.goblom.connect.ConnectPlugin;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Goblom
 */
public class ServiceProvider {
    private static final Map<String, Class<? extends ConnectService>> SERVICES = new ServiceHashMap();
    protected static final Map<Class<? extends ConnectService>, ConnectService> INSTANCE = new ConcurrentHashMap();
    private static boolean LOADED = false;
    
    public static void finishLoading(ConnectPlugin plugin) {
        if (LOADED) return;
        
        SERVICES.values().forEach((serviceClass) -> {
            try {
                ConnectService service = serviceClass.newInstance();
                
                service.connect(plugin);
                service.done();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        LOADED = true;
    }
    
    public static Collection<ConnectService> getServices() {
        return Collections.unmodifiableCollection(INSTANCE.values());
    }
    
    public static <T extends ConnectService> T getSMSServiceInstance(Class<T> clazz) {
        ConnectService service = INSTANCE.get(clazz);
        
        return service != null ? (T) service : null;
    }
    
    public static Class<? extends ConnectService> getSMSServiceClass(String name) {
        return SERVICES.getOrDefault(name, null);
    }
    
    public static boolean isRegistered(String name) {
        return SERVICES.containsKey(name);
    }
    
    public static boolean isRegistered(Class<? extends ConnectService> serviceClass) {
        return isValidateService(serviceClass) && SERVICES.containsKey(getServiceName(serviceClass));
    }
    
    public static void registerService(Class<? extends ConnectService> serviceClass) {
        String serviceName = getServiceName(serviceClass);
        
        if (!isRegistered(serviceClass)) {
            SERVICES.put(serviceName, serviceClass);
        } else {
            throw new RuntimeException(String.format("Unable to register service %s. Service with same name has already been registered", serviceName));
        }
    }
    
    private static boolean isValidateService(Class<? extends ConnectService> clazz) {
        ServiceName name = clazz.getAnnotation(ServiceName.class);
        
        return name != null && name.value() != null && !name.value().isEmpty();
    }
    
    public static String getServiceName(Class<? extends ConnectService> clazz) {
        if (isValidateService(clazz)) {
            ServiceName name = clazz.getAnnotation(ServiceName.class);
            
            return name.value();
        }
        
        return null;
    }
    
    private static class ServiceHashMap extends ConcurrentHashMap<String, Class<? extends ConnectService>> {

        @Override
        public Class<? extends ConnectService> replace(String key, Class<? extends ConnectService> value) {
            validate(key);
            return super.replace(key, value);
        }

        @Override
        public boolean replace(String key, Class<? extends ConnectService> oldValue, Class<? extends ConnectService> newValue) {
            validate(key);
            return super.replace(key, oldValue, newValue);
        }

        @Override
        public boolean remove(Object key, Object value) {
            validate(key.toString());
            return super.remove(key, value);
        }

        @Override
        public Class<? extends ConnectService> putIfAbsent(String key, Class<? extends ConnectService> value) {
            validate(key);
            return super.putIfAbsent(key, value);
        }

        @Override
        public Class<? extends ConnectService> remove(Object key) {
            validate(key.toString());
            return super.remove(key);
        }

//        @Override
//        public void putAll(Map<? extends String, ? extends Class<? extends SMSService>> m) {
//            super.putAll(m);
//        }

        @Override
        public Class<? extends ConnectService> put(String key, Class<? extends ConnectService> value) {
            validate(key);
            return super.put(key, value);
        }
        
        private String[] invalidServices = { "Connect", "ConnectPlugin", "" };
        private void validate(String key) {
            if (key.isEmpty()) throw new RuntimeException("Invalid Service Name");
            for (String invalid : invalidServices) {
                if (invalid.equals(key)) {
                    throw new RuntimeException("Invalid Service Name");
                }
            }
        }
    }
}
