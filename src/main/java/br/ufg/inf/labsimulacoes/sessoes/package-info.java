/**
 * Módulo Sessões: sessões em sala, mensagens, controles, tempo, bloqueio de dados pessoais, aplicação do quiz e engajamento.
 *
 * <p>Somente os tipos deste pacote raiz formam a API do módulo. Os subpacotes de camada são
 * internos e inacessíveis aos demais módulos.
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "Sessões",
        allowedDependencies = {"acesso", "cenarios", "ia", "eventos"})
package br.ufg.inf.labsimulacoes.sessoes;
