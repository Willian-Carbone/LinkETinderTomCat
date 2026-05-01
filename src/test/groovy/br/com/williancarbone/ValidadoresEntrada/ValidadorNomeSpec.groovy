package br.com.williancarbone.ValidadoresEntrada

import spock.lang.Specification

class ValidadorNomeSpec extends Specification {

    def "Teste de diversos cenários de entrada de nome"() {
        setup:
        ValidadorI validador = new ValidadorNome()

        expect:
        validador.validarDado(nome) == resultado

        where:
        nome                        | resultado
        "mario quintana"           | true
        "João Silva"                | true
        "Ana Maria Souza"           | true
        "joão"                   | false
        "p souza"                 | false
        "maria123"                | false
        " "                         | false
        ""                          | false
        null                        | false
    }
}
