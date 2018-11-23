/*
 * Copyright 2018 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.swarm.microprofile.faulttolerance.deployment;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

/**
 *
 * @author Martin Kouba
 */
public class MicroProfileFaultToleranceExtension implements Extension {

    void beforeBeanDiscovery(@Observes BeforeBeanDiscovery event, BeanManager beanManager) {
        event.addAnnotatedType(beanManager.createAnnotatedType(RequestContextCommandListener.class), RequestContextCommandListener.class.getName());
        event.addAnnotatedType(beanManager.createAnnotatedType(WeldCommandListenersProvider.class), WeldCommandListenersProvider.class.getName());
        event.addAnnotatedType(beanManager.createAnnotatedType(ThorntailHystrixConcurrencyStrategy.class), ThorntailHystrixConcurrencyStrategy.class.getName());
    }

}
