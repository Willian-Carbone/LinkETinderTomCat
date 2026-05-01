package br.com.williancarbone.ValidadoresEntrada

import spock.lang.Specification

class ValidadorIdadeSpec extends Specification {

    def "Teste regras de idade"() {
        setup:
        ValidadorI validador= new ValidadorIdade()

        expect:
        validador.validarDado(input) == resultado

        where:
        input   | resultado
        18   | true
        25    | true
        17   | false
        0    | false

    }
}
