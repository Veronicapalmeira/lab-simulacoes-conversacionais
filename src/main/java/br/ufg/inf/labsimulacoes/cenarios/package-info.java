/**
 * Módulo Cenários: cenários, versões, vínculo com a BNCC, quiz, curadoria, biblioteca e arquivamento.
 *
 * <p>Somente os tipos deste pacote raiz formam a API do módulo. Os subpacotes de camada são
 * internos e inacessíveis aos demais módulos.
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "Cenários",
        allowedDependencies = {"acesso", "ia", "eventos"})
package br.ufg.inf.labsimulacoes.cenarios;
