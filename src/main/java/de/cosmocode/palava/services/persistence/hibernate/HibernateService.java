/**
 * palava - a java-php-bridge
 * Copyright (C) 2007  CosmoCode GmbH
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
import java.net.URL;
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
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.internal.Lists;
import com.google.inject.name.Named;
import com.google.inject.servlet.RequestScoped;

import de.cosmocode.palava.core.registry.Registry;
import de.cosmocode.palava.core.service.Service;
import de.cosmocode.palava.core.service.lifecycle.Initializable;

/**
 * 
 *
 * @author Willi Schoenborn
 */
@Singleton
public final class HibernateService implements Service, Initializable, Provider<Session> {
    
    private static final Logger log = LoggerFactory.getLogger(HibernateService.class);
    
    private static final ImmutableMap<String, Class<?>> LISTENERS;
    
    static {
        final ImmutableMap.Builder<String, Class<?>> builder = ImmutableMap.builder();
        
        // TODO add listener interfaces
        
        LISTENERS = builder.build();
    }
    
    private final File config;
    
    private final URL schema;
    
    private final Registry registry;
    
    private Interceptor interceptor;
    
    private SessionFactory sessionFactory;
    
    public HibernateService(
        @Named("hibernate.cfg") File config, 
        @Named("hibernate.schema") URL schema,
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
    
        log.debug("Adding hibernate schema");
        configuration.addURL(schema);
        
        log.debug("Adding hibernate config file");
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
    
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    @Override
    @RequestScoped
    public Session get() {
        return sessionFactory.openSession();
    }
    
}

