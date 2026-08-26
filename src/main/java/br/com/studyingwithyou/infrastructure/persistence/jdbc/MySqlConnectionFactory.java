package br.com.studyingwithyou.infrastructure.persistence.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class MySqlConnectionFactory implements ConnectionFactory {

    private final String url;
    private final String usuario;
    private final String senha;

    public MySqlConnectionFactory(String url, String usuario, String senha) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("A URL do banco e obrigatoria.");
        }
        if (usuario == null || usuario.isBlank()) {
            throw new IllegalArgumentException("O usuario do banco e obrigatorio.");
        }
        this.url = url.trim();
        this.usuario = usuario.trim();
        this.senha = senha == null ? "" : senha;
    }

    @Override
    public Connection abrir() throws SQLException {
        return DriverManager.getConnection(url, usuario, senha);
    }
}
