/**
 * Módulo Métricas: indicadores por sessão, cenário e turma, comparação com sessões de controle, painel e exportação.
 *
 * <p>Somente os tipos deste pacote raiz formam a API do módulo. Os subpacotes de camada são
 * internos e inacessíveis aos demais módulos.
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "Métricas",
        allowedDependencies = {"acesso", "eventos"})
package br.ufg.inf.labsimulacoes.metricas;
