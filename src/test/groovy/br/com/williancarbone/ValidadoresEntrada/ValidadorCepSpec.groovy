package br.com.williancarbone.ValidadoresEntrada

import spock.lang.Specification

class ValidadorCepSpec extends Specification {

    def "Teste validacao de CEP"() {
        setup:
        def validador = new ValidadorCep()

        expect:
        validador.validarDado(input) == resultado

        where:
        input        | resultado
        "12345-678"  | true
        "12345678"   | true
        "12345"      | false
        "abc-defg"   | false
        null         | false
    }
}
