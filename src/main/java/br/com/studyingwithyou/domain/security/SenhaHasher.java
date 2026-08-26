package br.com.studyingwithyou.domain.security;

public interface SenhaHasher {

    String gerarHash(String senha);

    boolean corresponde(String senha, String hashArmazenado);
}
