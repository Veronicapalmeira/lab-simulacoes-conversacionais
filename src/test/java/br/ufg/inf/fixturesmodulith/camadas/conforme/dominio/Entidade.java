package br.ufg.inf.fixturesmodulith.camadas.conforme.dominio;

import java.util.List;

public class Entidade {

    public enum Status { ATIVO, INATIVO }

    int numero;
    String[] nomes;
    List<Status> historico;
}
