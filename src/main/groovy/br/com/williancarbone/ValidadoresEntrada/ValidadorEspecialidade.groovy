package br.com.williancarbone.ValidadoresEntrada

import br.com.williancarbone.model.enuns.Especialidade


class ValidadorEspecialidade implements ValidadorI<List<String>> {

    @Override
    boolean validarDado(List<String> especialidades) {

        if (especialidades == null || especialidades.isEmpty()) {
            return false
        }


        return especialidades.every { item ->
            Especialidade.values().any { it.valor.equalsIgnoreCase(item) }
        }


    }
}

