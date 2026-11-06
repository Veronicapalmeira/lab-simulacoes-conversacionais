/**
 * Módulo Adaptador de IA: porta única para o provedor: falas do personagem, sugestão de cenário, moderação, prompts versionados e metadados de cada chamada.
 *
 * <p>Somente os tipos deste pacote raiz formam a API do módulo. Os subpacotes de camada são
 * internos e inacessíveis aos demais módulos.
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "Adaptador de IA",
        allowedDependencies = {})
package br.ufg.inf.labsimulacoes.ia;
