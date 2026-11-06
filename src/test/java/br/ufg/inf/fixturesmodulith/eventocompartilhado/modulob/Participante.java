package br.ufg.inf.fixturesmodulith.eventocompartilhado.modulob;

import br.ufg.inf.fixturesmodulith.eventocompartilhado.eventos.EventoA;
import br.ufg.inf.fixturesmodulith.eventocompartilhado.eventos.EventoB;
import org.springframework.context.event.EventListener;

public class Participante {

    EventoB publicar() {
        return new EventoB();
    }

    @EventListener
    void aoReceber(EventoA evento) {}
}
