package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.ExpressionFactory;
import com.avaje.ebean.Query;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by obj on 05-02-2016.
 */
public abstract  class SupportDao10<T> {
    @Inject
    EbeanServer ebeanServer;

    protected final Class<T> klass;

    protected SupportDao10(Class klass) {
        this.klass = klass;
    }

    public T get(long id) {
        return ebeanServer.find(klass, id);
    }

    public List<T> list() {
        return ebeanServer.findList(query(), null);
    }

    public void save(T entity) {
        ebeanServer.save(entity);
    }

    public T findByKode(String kode) {
        return query().where().eq("kode", kode).findUnique();
    }

    protected Query<T> query() {
        return ebeanServer.find(klass);
    }

    protected ExpressionFactory expr() {
        return ebeanServer.getExpressionFactory();
    }
}