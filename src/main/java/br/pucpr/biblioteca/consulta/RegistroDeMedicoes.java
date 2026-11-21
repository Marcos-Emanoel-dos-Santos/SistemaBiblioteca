package br.pucpr.biblioteca.consulta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Armazena as medições de tempo de resposta registradas por {@link ConsultaComMedicao} (R09). */
public final class RegistroDeMedicoes {

    public record Medicao(String operacao, long duracaoNanos) {
    }

    private final List<Medicao> medicoes = new ArrayList<>();

    public void registrar(String operacao, long duracaoNanos) {
        medicoes.add(new Medicao(operacao, duracaoNanos));
    }

    public int quantidade() {
        return medicoes.size();
    }

    public List<Medicao> listar() {
        return Collections.unmodifiableList(medicoes);
    }
}
