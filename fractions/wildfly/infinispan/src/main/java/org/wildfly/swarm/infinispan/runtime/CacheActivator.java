package org.wildfly.swarm.infinispan.runtime;

import org.jboss.msc.service.AbstractService;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceActivator;
import org.jboss.msc.service.ServiceActivatorContext;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceRegistryException;

/** Activator to force a cache to start prior to deployment.
 * Created by bob on 8/15/17.
 */
public class CacheActivator implements ServiceActivator {

    public enum Type {
        DEFAULT_CACHE("org.wildfly.clustering.infinispan.default-cache"),
        CACHE_CONTAINER_CONFIGURATION("org.wildfly.clustering.infinispan.cache-container-configuration");

        final ServiceName serviceNameBase;

        Type(String serviceNameBase) {
            this.serviceNameBase = ServiceName.parse(serviceNameBase);
        }
    }

    // for WildFly 11, it was enough to add an artificial dependency
    // on org.wildfly.clustering.infinispan.cache-container-configuration.<cache container>,
    // because the Infinispan services were actually always started (albeit in a passive mode)
    //
    // since WildFly 14, we need to add an artificial dependency
    // on org.wildfly.clustering.infinispan.default-cache.<cache container>,
    // because the Infinispan services are now only started on demand
    private ServiceName BASE = ServiceName.parse("org.wildfly.clustering.infinispan.default-cache");

    public CacheActivator(String cacheContainerName, Type type) {
        this.cacheContainerName = cacheContainerName;
        this.type = type;
    }

    @Override
    public void activate(ServiceActivatorContext context) throws ServiceRegistryException {
        Service<Void> service = new AbstractService<Void>() { };
        context.getServiceTarget()
                .addService(ServiceName.of("thorntail", "cache-activator").append(this.cacheContainerName), service)
                .addDependency(this.type.serviceNameBase.append(this.cacheContainerName))
                .setInitialMode(ServiceController.Mode.ACTIVE)
                .install();
    }

    private final String cacheContainerName;

    private final Type type;
}
