package br.com.williancarbone.ValidadoresEntrada

import spock.lang.Specification

class ValidadorEmailSpec extends Specification {

    def "Teste validacao de Email"() {
        setup:
        def validador = new ValidadorEmail()

        expect:
        validador.validarDado(input) == resultado

        where:
        input             | resultado
        "teste@gmail.com" | true
        "zg@link.com.br"  | true
        "email"           | false
        "email@"          | false
        null              | false
    }
}
