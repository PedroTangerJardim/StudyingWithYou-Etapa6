package br.com.studyingwithyou.infrastructure.security;

import br.com.studyingwithyou.domain.security.SenhaHasher;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class Pbkdf2SenhaHasher implements SenhaHasher {

    private static final int ITERACOES = 65_536;
    private static final int TAMANHO_CHAVE = 256;
    private static final int TAMANHO_SALT = 16;
    private final SecureRandom random = new SecureRandom();

    @Override
    public String gerarHash(String senha) {
        byte[] salt = new byte[TAMANHO_SALT];
        random.nextBytes(salt);
        byte[] hash = derivar(senha.toCharArray(), salt, ITERACOES);
        return "pbkdf2-sha256$" + ITERACOES + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    @Override
    public boolean corresponde(String senha, String hashArmazenado) {
        if (senha == null || hashArmazenado == null) {
            return false;
        }
        String[] partes = hashArmazenado.split("\\$");
        if (partes.length != 4 || !"pbkdf2-sha256".equals(partes[0])) {
            return false;
        }
        try {
            int iteracoes = Integer.parseInt(partes[1]);
            byte[] salt = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            byte[] obtido = derivar(senha.toCharArray(), salt, iteracoes);
            return MessageDigest.isEqual(esperado, obtido);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private byte[] derivar(char[] senha, byte[] salt, int iteracoes) {
        PBEKeySpec especificacao = new PBEKeySpec(senha, salt, iteracoes, TAMANHO_CHAVE);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(especificacao)
                    .getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new IllegalStateException("Nao foi possivel proteger a senha.", ex);
        } finally {
            especificacao.clearPassword();
        }
    }
}
