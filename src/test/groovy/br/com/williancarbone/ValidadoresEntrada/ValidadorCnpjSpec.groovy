package br.com.williancarbone.ValidadoresEntrada

import spock.lang.Specification

class ValidadorCnpjSpec extends Specification {

    def "Teste de enntrada de CPF"() {

        setup:
        def validador = new ValidadorCnpj()

        expect:
        validador.validarDado(input) == resultado

        where:
        input                | resultado
        "12.345.678/0001-99" | true
        "12345678000199"     | true
        "12.345.678/0001-9"  | false
        "123456780001990"    | false
        "12A45678000199"     | false
        ""                   | false
        null                 | false
    }
}
