package br.pucpr.biblioteca.financeiro;

import br.pucpr.biblioteca.financeiro.politica.PoliticaDeMulta;
import br.pucpr.biblioteca.financeiro.politica.PoliticaOriginal;

import java.util.Objects;

/**
 * Mantém a política de multa vigente escolhida pela administração (R02 — Strategy).
 * Por padrão, inicia com {@link PoliticaOriginal}. Alterar a política afeta apenas multas futuras.
 */
public final class ConfiguracaoAdministrativa {

    private PoliticaDeMulta politicaVigente;

    public ConfiguracaoAdministrativa() {
        this(new PoliticaOriginal());
    }

    public ConfiguracaoAdministrativa(PoliticaDeMulta politicaInicial) {
        this.politicaVigente = Objects.requireNonNull(politicaInicial, "Política de multa é obrigatória.");
    }

    public PoliticaDeMulta getPoliticaVigente() {
        return politicaVigente;
    }

    public void definirPoliticaVigente(PoliticaDeMulta novaPolitica) {
        this.politicaVigente = Objects.requireNonNull(novaPolitica, "Política de multa não pode ser nula.");
    }
}
