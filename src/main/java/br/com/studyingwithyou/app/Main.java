package br.com.studyingwithyou.app;

import br.com.studyingwithyou.tests.TestesAplicacao;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        System.out.println("StudyingWithYou - verificacao da Etapa 6");
        new TestesAplicacao().executar();
    }
}
