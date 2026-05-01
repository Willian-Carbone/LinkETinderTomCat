package br.com.williancarbone.ValidadoresEntrada.facades

import br.com.williancarbone.exceptions.DadoInvalido
import br.com.williancarbone.exceptions.DadoNaoInformado
import spock.lang.Specification


class ValidadorCandidatoFacadeSpec extends Specification {


    ValidadorUsuarioRegistroFacade facade = new ValidadorCandidatoRegistroFacade()

    def "Tetse dados corretos"() {
        given:
        def infos = [
                nome: "nome completo",
                email: "mail@teste.com",
                cep: "12345678",
                estado: "riodejaneiro",
                idade: "25",
                cpf: "123.456.789-00",
                especialidades:["css","angular"],
                descricao:"desc"
        ]

        when:
        facade.validarDadosParaRegistroCandidato(infos)

        then:
        notThrown(Exception)
    }


    def "Teste campo ausente"() {
        given:

        Map infos = [
                nome: "nome", email: "w@w.com", cep: "12345",
                estado: "SP", idade: "25", cpf: "123"
        ]

        when:
        facade.validarDadosParaRegistroCandidato(infos)

        then:
        thrown(DadoNaoInformado)


    }

    def "Teste cpf mal formatado"() {
        given:
        def infos = [
                nome: "n", email: "w@w.com", cep: "12345678",
                estado: "Saopaulo", idade: "25", cpf: "000.000",
                especialidades:["css","angular"],
                descricao:"desc"
        ]

        when:
        facade.validarDadosParaRegistroCandidato(infos)

        then:
        thrown(DadoInvalido)
    }

    def "Teste idade invalida"() {
        given:
        def infos = [
                nome: "a", email: "j@j.com", cep: "12345678",
                estado: "Saopaulo", idade: "15", cpf: "123.456.789-00",
                especialidades:["css","angular"],
                descricao:"desc"

        ]

        when:
        facade.validarDadosParaRegistroCandidato(infos)

        then:
        thrown(DadoInvalido)
    }
}