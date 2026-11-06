package br.ufg.inf.fixturesmodulith.eventonopublicador.modulob;

import org.springframework.context.event.EventListener;

public class Ouvinte {

    @EventListener
    void aoReceber(br.ufg.inf.fixturesmodulith.eventonopublicador.moduloa.EventoA evento) {}
}
