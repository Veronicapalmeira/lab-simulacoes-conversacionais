package br.ufg.inf.fixturesmodulith.eventocompartilhado.moduloa;

import br.ufg.inf.fixturesmodulith.eventocompartilhado.eventos.EventoA;
import br.ufg.inf.fixturesmodulith.eventocompartilhado.eventos.EventoB;
import org.springframework.context.event.EventListener;

public class Participante {

    EventoA publicar() {
        return new EventoA();
    }

    @EventListener
    void aoReceber(EventoB evento) {}
}
