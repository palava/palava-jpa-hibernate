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

package de.cosmocode.palava.services.persistence;

import java.io.File;
import java.net.URL;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.name.Named;

import de.cosmocode.palava.core.service.Service;

/**
 * 
 *
 * @author Willi Schoenborn
 */
public class HibernateService implements Service {
    
    private static final Logger log = LoggerFactory.getLogger(HibernateService.class);

    private final SessionFactory sessionFactory;

    public HibernateService(@Named("hibernate.cfg") File cfg, @Named("hibernate.schema") URL schema) {
        final Configuration configuration = new AnnotationConfiguration();
        
        log.debug("Adding hibernate schema");
        configuration.addURL(schema);
        
        log.debug("Adding hibernate config file");
        configuration.configure(cfg);

        // TODO add services as interceptors, event listeners, filters, ...

        log.debug("Building session factory");
        this.sessionFactory = configuration.buildSessionFactory();
    }
    
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}

