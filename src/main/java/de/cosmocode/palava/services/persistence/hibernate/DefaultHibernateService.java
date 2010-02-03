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
import java.net.URL;

import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.servlet.RequestScoped;

import de.cosmocode.palava.core.lifecycle.Initializable;

/**
 * Default implementation of the {@link HibernateService} interface.
 *
 * @author Willi Schoenborn
 */
public final class DefaultHibernateService implements HibernateService, Initializable {
    
    private static final Logger LOG = LoggerFactory.getLogger(DefaultHibernateService.class);
    
    private final File config;
    
    private final URL schema;
    
    private Interceptor interceptor;
    
    private SessionFactory factory;
    
    @Inject
    public DefaultHibernateService(
        @Named("hibernate.cfg") File config, 
        @Named("hibernate.schema") URL schema) {
        this.config = Preconditions.checkNotNull(config, "Config");
        this.schema = Preconditions.checkNotNull(schema, "Schema");
    }
    
    @Inject(optional = true)
    void setInterceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
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
        
        LOG.debug("Building session factory");
        this.factory = configuration.buildSessionFactory();
    }
    
    @Override
    public SessionFactory getSessionFactory() {
        return factory;
    }
    
    @Override
    @RequestScoped
    public Session get() {
        return new DestroyableSession(factory.openSession());
    }
    
}
