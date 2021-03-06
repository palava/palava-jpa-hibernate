/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cosmocode.palava.jpa.hibernate;

import java.io.File;
import java.net.URL;
import java.util.Map.Entry;

import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.AutoFlushEventListener;
import org.hibernate.event.DeleteEventListener;
import org.hibernate.event.DirtyCheckEventListener;
import org.hibernate.event.EvictEventListener;
import org.hibernate.event.FlushEntityEventListener;
import org.hibernate.event.FlushEventListener;
import org.hibernate.event.InitializeCollectionEventListener;
import org.hibernate.event.LoadEventListener;
import org.hibernate.event.LockEventListener;
import org.hibernate.event.MergeEventListener;
import org.hibernate.event.PersistEventListener;
import org.hibernate.event.PostCollectionRecreateEventListener;
import org.hibernate.event.PostCollectionRemoveEventListener;
import org.hibernate.event.PostCollectionUpdateEventListener;
import org.hibernate.event.PostDeleteEventListener;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostLoadEventListener;
import org.hibernate.event.PostUpdateEventListener;
import org.hibernate.event.PreCollectionRecreateEventListener;
import org.hibernate.event.PreCollectionRemoveEventListener;
import org.hibernate.event.PreCollectionUpdateEventListener;
import org.hibernate.event.PreDeleteEventListener;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreLoadEventListener;
import org.hibernate.event.PreUpdateEventListener;
import org.hibernate.event.RefreshEventListener;
import org.hibernate.event.ReplicateEventListener;
import org.hibernate.event.SaveOrUpdateEventListener;
import org.hibernate.jmx.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.cosmocode.palava.core.Registry;
import de.cosmocode.palava.core.Registry.Key;
import de.cosmocode.palava.core.lifecycle.Disposable;
import de.cosmocode.palava.core.lifecycle.Initializable;
import de.cosmocode.palava.core.lifecycle.LifecycleException;
import de.cosmocode.palava.jmx.MBeanService;

/**
 * Default implementation of the {@link HibernateService} interface.
 *
 * @author Willi Schoenborn
 */
public final class DefaultHibernateService implements HibernateService, Initializable, Disposable {
    
    private static final Logger LOG = LoggerFactory.getLogger(DefaultHibernateService.class);
    
    private static final ImmutableMap<String, Class<?>> LISTENERS;
    
    static {
        final ImmutableMap.Builder<String, Class<?>> builder = ImmutableMap.builder();

        builder.put("auto-flush", AutoFlushEventListener.class);
        builder.put("merge", MergeEventListener.class);
        builder.put("create", PersistEventListener.class);
        builder.put("create-onflush", PersistEventListener.class);
        builder.put("delete", DeleteEventListener.class);
        builder.put("dirty-check", DirtyCheckEventListener.class);
        builder.put("evict", EvictEventListener.class);
        builder.put("flush", FlushEventListener.class);
        builder.put("flush-entity", FlushEntityEventListener.class);
        builder.put("load", LoadEventListener.class);
        builder.put("load-collection", InitializeCollectionEventListener.class);
        builder.put("lock", LockEventListener.class);
        builder.put("refresh", RefreshEventListener.class);
        builder.put("replicate", ReplicateEventListener.class);
        builder.put("save-update", SaveOrUpdateEventListener.class);
        builder.put("save", SaveOrUpdateEventListener.class);
        builder.put("update", SaveOrUpdateEventListener.class);
        builder.put("pre-load", PreLoadEventListener.class);
        builder.put("pre-update", PreUpdateEventListener.class);
        builder.put("pre-delete", PreDeleteEventListener.class);
        builder.put("pre-insert", PreInsertEventListener.class);
        builder.put("pre-collection-recreate", PreCollectionRecreateEventListener.class);
        builder.put("pre-collection-remove", PreCollectionRemoveEventListener.class);
        builder.put("pre-collection-update", PreCollectionUpdateEventListener.class);
        builder.put("post-load", PostLoadEventListener.class);
        builder.put("post-update", PostUpdateEventListener.class);
        builder.put("post-delete", PostDeleteEventListener.class);
        builder.put("post-insert", PostInsertEventListener.class);
        builder.put("post-commit-update", PostUpdateEventListener.class);
        builder.put("post-commit-delete", PostDeleteEventListener.class);
        builder.put("post-commit-insert", PostInsertEventListener.class);
        builder.put("post-collection-recreate", PostCollectionRecreateEventListener.class);
        builder.put("post-collection-remove", PostCollectionRemoveEventListener.class);
        builder.put("post-collection-update", PostCollectionUpdateEventListener.class);
        
        LISTENERS = builder.build();
    }
    
    private final File config;
    
    private final URL schema;
    
    private final Registry registry;
    
    private Interceptor interceptor;
    
    private boolean propagateEvents;
    
    private final MBeanService mBeanService;
    
    private final StatisticsService statistics = new StatisticsService();
    
    private String name = HibernateService.class.getSimpleName();
    
    private SessionFactory factory;
    
    @Inject
    public DefaultHibernateService(
        @Named("hibernate.cfg") File config, 
        @Named("hibernate.schema") URL schema,
        Registry registry, MBeanService mBeanService) {
        this.config = Preconditions.checkNotNull(config, "Config");
        this.schema = Preconditions.checkNotNull(schema, "Schema");
        this.registry = Preconditions.checkNotNull(registry, "Registry");
        this.mBeanService = Preconditions.checkNotNull(mBeanService, "MBeanService");
    }
    
    @Inject(optional = true)
    void setInterceptor(Interceptor interceptor) {
        this.interceptor = Preconditions.checkNotNull(interceptor, "Interceptor");
    }
    
    @Inject(optional = true)
    void setPropagateEvents(@Named("hibernate.events.propagate") boolean propagateEvents) {
        this.propagateEvents = propagateEvents;
    }

    @Inject(optional = true)
    void setName(@Named("hibernate.jmx.name") String name) {
        this.name = Preconditions.checkNotNull(name, "Name");
    }
    
    @Override
    public void initialize() {
        final Configuration configuration = new AnnotationConfiguration();
    
        LOG.debug("Adding hibernate schema: {}", schema);
        configuration.addURL(schema);
        
        LOG.debug("Adding hibernate config file: {}", config);
        configuration.configure(config);
    
        if (interceptor == null) {
            LOG.info("No interceptor configured");
        } else {
            LOG.info("Using {} as interceptor", interceptor);
            configuration.setInterceptor(interceptor);
        }
        
        if (propagateEvents) {
            LOG.info("Registering event listeners");
            for (Entry<String, Class<?>> entry : LISTENERS.entrySet()) {
                final String event = entry.getKey();
                final Class<?> type = entry.getValue();
                final Key<?> key = Key.get(type, event);
                final Object listener = registry.proxy(key);
                LOG.info("Registering {} for {}", listener, event);
                configuration.setListener(event, listener);
            }
        } else {
            LOG.info("Events are not propagated through the registry");
        }
        
        LOG.debug("Building session factory");
        this.factory = configuration.buildSessionFactory();
            
        statistics.setSessionFactory(factory);
        statistics.setStatisticsEnabled(true);
        mBeanService.register(statistics, "name", name);
    }
    
    @Override
    public SessionFactory getSessionFactory() {
        return factory;
    }
    
    @Override
    public Session get() {
        return new DestroyableSession(factory.openSession());
    }
    
    @Override
    public void dispose() throws LifecycleException {
        mBeanService.unregister(statistics, "name", name);
    }
    
}
