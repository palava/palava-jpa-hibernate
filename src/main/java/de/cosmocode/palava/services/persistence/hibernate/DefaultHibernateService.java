/**
 * palava - a java-php-bridge
 * Copyright (C) 2007-2010  CosmoCode GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.cosmocode.palava.services.persistence.hibernate;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.internal.Lists;
import com.google.inject.name.Named;
import com.google.inject.servlet.RequestScoped;

import de.cosmocode.palava.core.lifecycle.Initializable;
import de.cosmocode.palava.registry.Registry;

/**
 * 
 *
 * @author Willi Schoenborn
 */
public final class DefaultHibernateService implements HibernateService, Initializable {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultHibernateService.class);
    
    private static final ImmutableMap<String, Class<?>> LISTENERS;
    
    static {
        final ImmutableMap.Builder<String, Class<?>> builder = ImmutableMap.builder();
        
        // TODO add listener interfaces
        
        LISTENERS = builder.build();
    }
    
    private final File config;
    
    private final File schema;
    
    private final Registry registry;
    
    private Interceptor interceptor;
    
    private SessionFactory sessionFactory;
    
    @Inject
    public DefaultHibernateService(
        @Named("hibernate.cfg") File config, 
        @Named("hibernate.schema") File schema,
        Registry registry) {
        this.config = Preconditions.checkNotNull(config, "Config");
        this.schema = Preconditions.checkNotNull(schema, "Schema");
        this.registry = Preconditions.checkNotNull(registry, "Registry");
    }
    
    @Inject(optional = true)
    public void setInterceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
    }
    
    @Override
    public void initialize() {
        final Configuration configuration = new AnnotationConfiguration();
    
        log.debug("Adding hibernate schema: {}", schema);
        configuration.addFile(schema);
//        configuration.addURL(schema);
        
        log.debug("Adding hibernate config file: {}", config);
        configuration.configure(config);
    
        if (interceptor == null) {
            log.info("No interceptor configured");
        } else {
            log.info("Using {} as interceptor", interceptor);
            configuration.setInterceptor(interceptor);
        }
        
        // TODO how can we make sure, all listeners are already registers when doing this?
        for (Map.Entry<String, Class<?>> entry : LISTENERS.entrySet()) {
            final Iterable<? extends Object> listeners = registry.getListeners(entry.getValue());
            final List<Object> list = Lists.newArrayList(listeners);
            if (list.isEmpty()) continue;
            final Object[] array = list.toArray(new Object[list.size()]);
            configuration.setListeners(entry.getKey(), array);
        }

        log.debug("Building session factory");
        this.sessionFactory = configuration.buildSessionFactory();
    }
    
    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    @Override
    @RequestScoped
    public Session get() {
        // TODO destroyable
        return sessionFactory.openSession();
    }
    
}

