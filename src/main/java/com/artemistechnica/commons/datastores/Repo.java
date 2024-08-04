package com.artemistechnica.commons.datastores;

import com.artemistechnica.commons.datatypes.EitherE;

public interface Repo<T, IN, COUT, QOUT, UOUT, DOUT,
        C extends Repo.Create<IN, COUT>, Q extends Repo.Query<IN, QOUT>, U extends Repo.Update<IN, UOUT>, D extends Repo.Delete<IN, DOUT>> {

    EitherE<T> create(IN queryInput, C query, T entity);

    EitherE<T> read(Q query);

    EitherE<T> update(U updateStatement);

    EitherE<T> delete(D deleteStatement);

    interface Create<IN, OUT> {
        OUT mk(IN input);
    }

    interface Query<IN, OUT> {
        OUT mk(IN input);
    }

    interface Update<IN, OUT> {
        OUT mk(IN input);
    }

    interface Delete<IN, OUT> {
        OUT mk(IN input);
    }
}
