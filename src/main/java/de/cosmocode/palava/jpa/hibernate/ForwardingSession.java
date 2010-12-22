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

import java.io.Serializable;
import java.sql.Connection;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.UnknownProfileException;
import org.hibernate.jdbc.Work;
import org.hibernate.stat.SessionStatistics;

import com.google.common.collect.ForwardingObject;

/**
 * Decorates a {@link Session} to alter behaviour for certain methods.
 *
 * @author Willi Schoenborn
 */
abstract class ForwardingSession extends ForwardingObject implements Session {

    private static final long serialVersionUID = -3449067627280246157L;

    /**
     * Provide the {@link Session} delegate.
     * 
     * @return the delegate
     */
    @Override
    protected abstract Session delegate();
    
    @Override
    public Transaction beginTransaction() throws HibernateException {
        return delegate().beginTransaction();
    }

    @Override
    public void cancelQuery() throws HibernateException {
        delegate().cancelQuery();
    }

    @Override
    public void clear() {
        delegate().clear();
    }

    @Override
    public Connection close() throws HibernateException {
        return delegate().close();
    }

    @Override
    @SuppressWarnings("deprecation")
    public Connection connection() throws HibernateException {
        return delegate().connection();
    }

    @Override
    public boolean contains(Object object) {
        return delegate().contains(object);
    }

    @Override
    public Criteria createCriteria(@SuppressWarnings("rawtypes") Class persistentClass, String alias) {
        return delegate().createCriteria(persistentClass, alias);
    }

    @Override
    public Criteria createCriteria(@SuppressWarnings("rawtypes") Class persistentClass) {
        return delegate().createCriteria(persistentClass);
    }

    @Override
    public Criteria createCriteria(String entityName, String alias) {
        return delegate().createCriteria(entityName, alias);
    }

    @Override
    public Criteria createCriteria(String entityName) {
        return delegate().createCriteria(entityName);
    }

    @Override
    public Query createFilter(Object collection, String queryString) throws HibernateException {
        return delegate().createFilter(collection, queryString);
    }

    @Override
    public Query createQuery(String queryString) throws HibernateException {
        return delegate().createQuery(queryString);
    }

    @Override
    public SQLQuery createSQLQuery(String queryString) throws HibernateException {
        return delegate().createSQLQuery(queryString);
    }

    @Override
    public void delete(Object object) throws HibernateException {
        delegate().delete(object);
    }

    @Override
    public void delete(String entityName, Object object) throws HibernateException {
        delegate().delete(entityName, object);
    }

    @Override
    public void disableFilter(String filterName) {
        delegate().disableFilter(filterName);
    }

    @Override
    public Connection disconnect() throws HibernateException {
        return delegate().disconnect();
    }

    @Override
    public void doWork(Work work) throws HibernateException {
        delegate().doWork(work);
    }

    @Override
    public Filter enableFilter(String filterName) {
        return delegate().enableFilter(filterName);
    }

    @Override
    public void evict(Object object) throws HibernateException {
        delegate().evict(object);
    }

    @Override
    public void flush() throws HibernateException {
        delegate().flush();
    }

    @Override
    @SuppressWarnings("deprecation")
    public Object get(@SuppressWarnings("rawtypes") Class clazz, Serializable id, 
            LockMode lockMode) throws HibernateException {
        return delegate().get(clazz, id, lockMode);
    }

    @Override
    public Object get(@SuppressWarnings("rawtypes") Class clazz, Serializable id) throws HibernateException {
        return delegate().get(clazz, id);
    }

    @Override
    @SuppressWarnings("deprecation")
    public Object get(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
        return delegate().get(entityName, id, lockMode);
    }

    @Override
    public Object get(String entityName, Serializable id) throws HibernateException {
        return delegate().get(entityName, id);
    }

    @Override
    public CacheMode getCacheMode() {
        return delegate().getCacheMode();
    }

    @Override
    public LockMode getCurrentLockMode(Object object) throws HibernateException {
        return delegate().getCurrentLockMode(object);
    }

    @Override
    public Filter getEnabledFilter(String filterName) {
        return delegate().getEnabledFilter(filterName);
    }

    @Override
    public EntityMode getEntityMode() {
        return delegate().getEntityMode();
    }

    @Override
    public String getEntityName(Object object) throws HibernateException {
        return delegate().getEntityName(object);
    }

    @Override
    public FlushMode getFlushMode() {
        return delegate().getFlushMode();
    }

    @Override
    public Serializable getIdentifier(Object object) throws HibernateException {
        return delegate().getIdentifier(object);
    }

    @Override
    public Query getNamedQuery(String queryName) throws HibernateException {
        return delegate().getNamedQuery(queryName);
    }

    @Override
    public Session getSession(EntityMode entityMode) {
        return delegate().getSession(entityMode);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return delegate().getSessionFactory();
    }

    @Override
    public SessionStatistics getStatistics() {
        return delegate().getStatistics();
    }

    @Override
    public Transaction getTransaction() {
        return delegate().getTransaction();
    }

    @Override
    public boolean isConnected() {
        return delegate().isConnected();
    }

    @Override
    public boolean isDirty() throws HibernateException {
        return delegate().isDirty();
    }

    @Override
    public boolean isOpen() {
        return delegate().isOpen();
    }

    @Override
    @SuppressWarnings("deprecation")
    public Object load(@SuppressWarnings("rawtypes") Class theClass, 
            Serializable id, LockMode lockMode) throws HibernateException {
        return delegate().load(theClass, id, lockMode);
    }

    @Override
    public Object load(@SuppressWarnings("rawtypes") Class theClass, Serializable id) throws HibernateException {
        return delegate().load(theClass, id);
    }

    @Override
    public void load(Object object, Serializable id) throws HibernateException {
        delegate().load(object, id);
    }

    @Override
    @SuppressWarnings("deprecation")
    public Object load(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
        return delegate().load(entityName, id, lockMode);
    }

    @Override
    public Object load(String entityName, Serializable id) throws HibernateException {
        return delegate().load(entityName, id);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void lock(Object object, LockMode lockMode) throws HibernateException {
        delegate().lock(object, lockMode);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void lock(String entityName, Object object, LockMode lockMode) throws HibernateException {
        delegate().lock(entityName, object, lockMode);
    }

    @Override
    public Object merge(Object object) throws HibernateException {
        return delegate().merge(object);
    }

    @Override
    public Object merge(String entityName, Object object) throws HibernateException {
        return delegate().merge(entityName, object);
    }

    @Override
    public void persist(Object object) throws HibernateException {
        delegate().persist(object);
    }

    @Override
    public void persist(String entityName, Object object) throws HibernateException {
        delegate().persist(entityName, object);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void reconnect() throws HibernateException {
        delegate().reconnect();
    }

    @Override
    public void reconnect(Connection connection) throws HibernateException {
        delegate().reconnect(connection);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void refresh(Object object, LockMode lockMode) throws HibernateException {
        delegate().refresh(object, lockMode);
    }

    @Override
    public void refresh(Object object) throws HibernateException {
        delegate().refresh(object);
    }

    @Override
    public void replicate(Object object, ReplicationMode replicationMode) throws HibernateException {
        delegate().replicate(object, replicationMode);
    }

    @Override
    public void replicate(String entityName, Object object, ReplicationMode replicationMode) throws HibernateException {
        delegate().replicate(entityName, object, replicationMode);
    }

    @Override
    public Serializable save(Object object) throws HibernateException {
        return delegate().save(object);
    }

    @Override
    public Serializable save(String entityName, Object object) throws HibernateException {
        return delegate().save(entityName, object);
    }

    @Override
    public void saveOrUpdate(Object object) throws HibernateException {
        delegate().saveOrUpdate(object);
    }

    @Override
    public void saveOrUpdate(String entityName, Object object) throws HibernateException {
        delegate().saveOrUpdate(entityName, object);
    }

    @Override
    public void setCacheMode(CacheMode cacheMode) {
        delegate().setCacheMode(cacheMode);
    }

    @Override
    public void setFlushMode(FlushMode flushMode) {
        delegate().setFlushMode(flushMode);
    }

    @Override
    public void setReadOnly(Object entity, boolean readOnly) {
        delegate().setReadOnly(entity, readOnly);
    }

    @Override
    public void update(Object object) throws HibernateException {
        delegate().update(object);
    }

    @Override
    public void update(String entityName, Object object) throws HibernateException {
        delegate().update(entityName, object);
    }

    @Override
    public boolean isDefaultReadOnly() {
        return delegate().isDefaultReadOnly();
    }

    @Override
    public void setDefaultReadOnly(boolean readOnly) {
        delegate().setDefaultReadOnly(readOnly);
    }

    @Override
    public Object load(@SuppressWarnings("rawtypes") Class theClass, Serializable id, 
            LockOptions lockOptions) throws HibernateException {
        return delegate().load(theClass, id, lockOptions);
    }

    @Override
    public Object load(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
        return delegate().load(entityName, id, lockOptions);
    }

    @Override
    public LockRequest buildLockRequest(LockOptions lockOptions) {
        return delegate().buildLockRequest(lockOptions);
    }

    @Override
    public void refresh(Object object, LockOptions lockOptions) throws HibernateException {
        delegate().refresh(object, lockOptions);
    }

    @Override
    public Object get(@SuppressWarnings("rawtypes") Class clazz, Serializable id, 
            LockOptions lockOptions) throws HibernateException {
        return delegate().get(clazz, id, lockOptions);
    }

    @Override
    public Object get(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
        return delegate().get(entityName, id, lockOptions);
    }

    @Override
    public boolean isReadOnly(Object entityOrProxy) {
        return delegate().isReadOnly(entityOrProxy);
    }

    @Override
    public boolean isFetchProfileEnabled(String name) throws UnknownProfileException {
        return delegate().isFetchProfileEnabled(name);
    }

    @Override
    public void enableFetchProfile(String name) throws UnknownProfileException {
        delegate().enableFetchProfile(name);
    }

    @Override
    public void disableFetchProfile(String name) throws UnknownProfileException {
        delegate().disableFetchProfile(name);
    }

}
