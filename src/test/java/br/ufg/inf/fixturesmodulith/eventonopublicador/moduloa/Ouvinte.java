package br.ufg.inf.fixturesmodulith.eventonopublicador.moduloa;

import org.springframework.context.event.EventListener;

public class Ouvinte {

    @EventListener
    void aoReceber(br.ufg.inf.fixturesmodulith.eventonopublicador.modulob.EventoB evento) {}
}
