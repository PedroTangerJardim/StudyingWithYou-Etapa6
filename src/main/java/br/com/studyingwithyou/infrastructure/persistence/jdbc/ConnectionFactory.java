package br.com.studyingwithyou.infrastructure.persistence.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface ConnectionFactory {

    Connection abrir() throws SQLException;
}
