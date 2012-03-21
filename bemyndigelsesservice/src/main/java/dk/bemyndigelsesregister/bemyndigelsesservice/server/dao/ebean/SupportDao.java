package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import com.avaje.ebean.EbeanServer;

import javax.inject.Inject;

public abstract class SupportDao<T> {
    @Inject
    EbeanServer ebeanServer;

    protected final Class<T> klass;

    protected SupportDao(Class klass) {
        this.klass = klass;
    }

    public T get(long id) {
        return ebeanServer.find(klass, id);
    }

    public void save(T object) {
        ebeanServer.save(object);
    }
}
