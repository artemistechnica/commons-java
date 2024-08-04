package com.artemistechnica.commons.datastores.postgres;

import com.artemistechnica.commons.datastores.Repo;
import com.artemistechnica.commons.datatypes.EitherE;
import com.artemistechnica.commons.errors.Retry;

import java.sql.Connection;
import java.sql.SQLException;

interface PostgresRepo<T> extends Repo<T, String, String, String, String, String, PostgresRepo.PCreate, PostgresRepo.PQuery, PostgresRepo.PUpdate, PostgresRepo.PDelete>, Retry {

    Connection connection();

    @Override
    default EitherE<T> create(String queryInput, PCreate query, T entity) {
        return tryFunc(() -> {
            try { return connection().prepareStatement(query.mk(queryInput)); }
            catch (SQLException e) { throw new RuntimeException(e); }
        }).flatMapE(statement -> tryFunc(() -> {
            try { return statement.executeUpdate(); }
            catch (SQLException e) { throw new RuntimeException(e); }
        })).map(i -> entity);
    }

    @Override
    default EitherE<T> read(PQuery query) {
        return null;
    }

    @Override
    default EitherE<T> update(PUpdate updateStatement) {
        return null;
    }

    @Override
    default EitherE<T> delete(PDelete deleteStatement) {
        return null;
    }

    final class PCreate implements Repo.Create<String, String> {

        @Override
        public String mk(String input) {
            return input;
        }
    }

    final class PQuery implements Repo.Query<String, String> {

        @Override
        public String mk(String input) {
            return input;
        }
    }

    final class PUpdate implements Repo.Update<String, String> {

        @Override
        public String mk(String input) {
            return input;
        }
    }

    final class PDelete implements Repo.Delete<String, String> {

        @Override
        public String mk(String input) {
            return input;
        }
    }
}
