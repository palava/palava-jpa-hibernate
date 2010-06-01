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

import org.hibernate.Session;

import com.google.common.base.Preconditions;

import de.cosmocode.palava.scope.Destroyable;

/**
 * {@link Destroyable} version of a Hibernate {@link Session}.
 *
 * @author Willi Schoenborn
 */
final class DestroyableSession extends ForwardingSession implements Destroyable {

    private static final long serialVersionUID = -3229611777799970807L;
    
    private final Session session;
    
    public DestroyableSession(Session session) {
        this.session = Preconditions.checkNotNull(session, "Session");
    }
    
    @Override
    protected Session delegate() {
        return session;
    }

    @Override
    public void destroy() {
        if (session.isOpen()) session.close();
    }

}
