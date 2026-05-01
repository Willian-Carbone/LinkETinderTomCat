package br.com.williancarbone.ValidadoresEntrada

import spock.lang.Specification

class ValidadorCpfSpec extends Specification {

    def "Teste entrada de CPF"() {
        setup:
        def validador = new ValidadorCpf()

        expect:
        validador.validarDado(input) == resultado

        where:
        input            | resultado
        "123.456.789-00" | true
        "12345678900"    | true
        "123.456.789-0"  | false
        "123456789000"   | false
        "abc.def.ghi-jk" | false
        null             | false
    }

}
