/**
 * Módulo Acesso: usuários, perfis acumuláveis, turmas, parâmetros e autenticação.
 *
 * <p>Somente os tipos deste pacote raiz formam a API do módulo. Os subpacotes de camada são
 * internos e inacessíveis aos demais módulos.
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "Acesso",
        allowedDependencies = {"eventos"})
package br.ufg.inf.labsimulacoes.acesso;
