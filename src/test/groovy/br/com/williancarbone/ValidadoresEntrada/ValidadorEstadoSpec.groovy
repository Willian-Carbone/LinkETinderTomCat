package br.com.williancarbone.ValidadoresEntrada

import spock.lang.Specification

class ValidadorEstadoSpec extends Specification {

    def "Teste entrada de Estado"() {
        setup:
        def validador = new ValidadorEstado()

        expect:
        validador.validarDado(entrada) == resultado

        where:
        entrada            | resultado
        "São Paulo"       | true
        "rio de janeiro"  | true
        "MinasGerais"     | true
        "EstadoFicticio"  | false
        ""                | false
        null              | false
    }
}
