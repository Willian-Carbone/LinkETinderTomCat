package br.com.williancarbone.ValidadoresEntrada.factory

import br.com.williancarbone.ValidadoresEntrada.ValidadorCep
import br.com.williancarbone.ValidadoresEntrada.ValidadorCnpj
import br.com.williancarbone.ValidadoresEntrada.ValidadorCpf
import br.com.williancarbone.ValidadoresEntrada.ValidadorEmail
import br.com.williancarbone.ValidadoresEntrada.ValidadorEspecialidade
import br.com.williancarbone.ValidadoresEntrada.ValidadorEstado
import br.com.williancarbone.ValidadoresEntrada.ValidadorI
import br.com.williancarbone.ValidadoresEntrada.ValidadorIdade
import br.com.williancarbone.ValidadoresEntrada.ValidadorNome

class ValidadorFactory {
    ValidadorI fabricarValidador(String tipo){
        switch (tipo){
            case "cep":
                return new ValidadorCep()

            case "cnpj":
                return new ValidadorCnpj()

            case "cpf":
                return new ValidadorCpf()

            case "email":
                return new ValidadorEmail()

            case "especialidade":
                return new ValidadorEspecialidade()

            case  "nome":
                return new ValidadorNome()

            case "estado":
                return new ValidadorEstado()

            case "idade":
                return new ValidadorIdade()
        }
    }
}
