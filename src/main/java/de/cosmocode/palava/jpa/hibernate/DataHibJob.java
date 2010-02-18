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

package de.cosmocode.palava.jpa.hibernate;

import java.util.Map;

import org.hibernate.Session;

import de.cosmocode.palava.bridge.Server;
import de.cosmocode.palava.bridge.call.Call;
import de.cosmocode.palava.bridge.call.DataCall;
import de.cosmocode.palava.bridge.call.MissingArgumentException;
import de.cosmocode.palava.bridge.command.Response;

public abstract class DataHibJob extends HibernateJob {

    private Map<String, String> args;
    
    @Override
    public final void process(Call request, Response response, de.cosmocode.palava.bridge.session.HttpSession s, Server server, 
        Map<String, Object> caddy, Session session) throws Exception {

        DataCall dataRequest = (DataCall) request;
        args = dataRequest.getStringedArguments();
        
        process(args, response, s, server, caddy, session);
    }
    
    protected final void validate(String... keys) throws MissingArgumentException {
        for (String key : keys) {
            if (!args.containsKey(key)) throw new MissingArgumentException(key);
        }
    }
    
    protected abstract void process(Map<String, String> args, Response response, de.cosmocode.palava.bridge.session.HttpSession s, Server server,
        Map<String, Object> caddy, Session session) throws Exception;
    
}