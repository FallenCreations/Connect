/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.api;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Goblom
 */
public class ServiceProvider {
    private static final Map<String, Class<? extends SMSService>> SERVICES = new ServiceHashMap();
    protected static final Map<Class<? extends SMSService>, SMSService> INSTANCE = new ConcurrentHashMap();
    private static boolean LOADED = false;
    
    public static void finishLoading() {
        if (LOADED) return;
        
        SERVICES.values().forEach((serviceClass) -> {
            try {
                serviceClass.newInstance().done();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        LOADED = true;
    }
    
    public static Collection<SMSService> getServices() {
        return Collections.unmodifiableCollection(INSTANCE.values());
    }
    
    public static <T extends SMSService> T getSMSServiceInstance(Class<T> clazz) {
        SMSService service = INSTANCE.get(clazz);
        
        return service != null ? (T) service : null;
    }
    
    public static Class<? extends SMSService> getSMSServiceClass(String name) {
        return SERVICES.getOrDefault(name, null);
    }
    
    public static boolean isRegistered(String name) {
        return SERVICES.containsKey(name);
    }
    
    public static boolean isRegistered(Class<? extends SMSService> serviceClass) {
        return isValidateService(serviceClass) && SERVICES.containsKey(getServiceName(serviceClass));
    }
    
    public static void registerService(Class<? extends SMSService> serviceClass) {
        String serviceName = getServiceName(serviceClass);
        
        if (!isRegistered(serviceClass)) {
            SERVICES.put(serviceName, serviceClass);
        } else {
            throw new RuntimeException(String.format("Unable to register service %s. Service with same name has already been registered", serviceName));
        }
    }
    
    private static boolean isValidateService(Class<? extends SMSService> clazz) {
        ServiceName name = clazz.getAnnotation(ServiceName.class);
        
        return name != null && name.value() != null && !name.value().isEmpty();
    }
    
    protected static String getServiceName(Class<? extends SMSService> clazz) {
        if (isValidateService(clazz)) {
            ServiceName name = clazz.getAnnotation(ServiceName.class);
            
            return name.value();
        }
        
        return null;
    }
    
    private static class ServiceHashMap extends ConcurrentHashMap<String, Class<? extends SMSService>> {

        @Override
        public Class<? extends SMSService> replace(String key, Class<? extends SMSService> value) {
            validate(key);
            return super.replace(key, value);
        }

        @Override
        public boolean replace(String key, Class<? extends SMSService> oldValue, Class<? extends SMSService> newValue) {
            validate(key);
            return super.replace(key, oldValue, newValue);
        }

        @Override
        public boolean remove(Object key, Object value) {
            validate(key.toString());
            return super.remove(key, value);
        }

        @Override
        public Class<? extends SMSService> putIfAbsent(String key, Class<? extends SMSService> value) {
            validate(key);
            return super.putIfAbsent(key, value);
        }

        @Override
        public Class<? extends SMSService> remove(Object key) {
            validate(key.toString());
            return super.remove(key);
        }

//        @Override
//        public void putAll(Map<? extends String, ? extends Class<? extends SMSService>> m) {
//            super.putAll(m);
//        }

        @Override
        public Class<? extends SMSService> put(String key, Class<? extends SMSService> value) {
            validate(key);
            return super.put(key, value);
        }
        
        private String[] invalidServices = { "Connect", "ConnectPlugin" };
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
