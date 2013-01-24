package dk.statsbiblioteket.broadcasttranscoder.persistence;


public interface GenericDAO<T, PK extends java.io.Serializable>   {

    PK create(T t);

    T read(PK pk);

    void update(T t);

    void delete(T t);
}

