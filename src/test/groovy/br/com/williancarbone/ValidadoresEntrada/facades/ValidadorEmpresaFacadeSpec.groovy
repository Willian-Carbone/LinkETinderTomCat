package br.com.williancarbone.ValidadoresEntrada.facades

import br.com.williancarbone.exceptions.DadoInvalido
import br.com.williancarbone.exceptions.DadoNaoInformado
import spock.lang.Specification

class ValidadorEmpresaFacadeSpec extends Specification {

    def facade = new ValidadorEmpresaRegistroFacade()

    def "Teste validação mapa correto"() {
        given:
        Map infos = [
                nome: "exemplo nome",
                email: "contato@gmail.com",
                cep: "12345-678",
                estado: "Saopaulo",
                pais: "Brasil",
                cnpj: "12.345.678/0001-00",
                especialidades:["java","html"],
                descricao:"desc"
        ]

        when:
        facade.validarDadosParaRegistroEmpresa(infos)

        then:
        notThrown(Exception)
    }

    def "Teste envio campo faltante"() {
        given:
        Map infos = [nome: "Empresa", cep: "12345", estado: "RJ", pais: "Brasil", cnpj: "123",descricao:"desc"]

        when: "validamos"
        facade.validarDadosParaRegistroEmpresa(infos)

        then:
        DadoNaoInformado e = thrown(DadoNaoInformado)
        e.message.contains("email")
    }



    def "Teste dado recusado por validador"() {
        given:
        Map infos = [
                nome: "Empresa", email: "a@a.com", cep: "12345-678",
                estado: "RJ", pais: "Brasil", cnpj: "123",especialidades:["java"],
                descricao:"desc"
        ]

        when:
        facade.validarDadosParaRegistroEmpresa(infos)

        then:
        thrown(DadoInvalido)
    }
}