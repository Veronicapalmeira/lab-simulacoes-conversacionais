/**
 * Eventos de domínio trocados entre os módulos (Stack, seção 4). Não é um módulo de negócio: guarda
 * só os tipos dos eventos, sem regras nem beans.
 *
 * <p>Se cada evento ficasse no módulo que o publica, pares que publicam e escutam um ao outro, como
 * Cenários e Auditoria, formariam um ciclo. Aqui, os dois lados dependem só deste pacote.
 *
 * <p>Não depende de nenhum módulo, então os eventos carregam apenas valores simples, como
 * identificadores, e nunca tipos de outro módulo.
 */
@org.springframework.modulith.ApplicationModule(displayName = "Eventos", allowedDependencies = {})
package br.ufg.inf.labsimulacoes.eventos;
