package br.com.williancarbone.util

import spock.lang.Specification

class TextConversorUtilSpec extends Specification {


    def "Teste de removedor não digitos"(){

        expect:

        valorEsperado == TextConversorUtil.removerNaoDigitos(exemploEntrada)


        where:

        exemploEntrada| valorEsperado
        "asdhs2asdha" | "2"
        "%¨&*()#@!23%#@!&*><" | "23"
        ""|""
        null | ""



    }

    def "Teste conversor String comun para nome enum valido"(){

        expect:



        where:

        ExemploDePossivelEstado | ValorEsperado
        "rio de janeiro" | "RIODEJANEIRO"
        "SaÕpÃuLo" | "SAOPAULO"
        "" | ""
        null| ""
    }
}
