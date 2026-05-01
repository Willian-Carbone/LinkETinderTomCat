package br.com.williancarbone.dao

import br.com.williancarbone.exceptions.DadoNaoEncontado
import br.com.williancarbone.exceptions.DadoNaoInformado
import br.com.williancarbone.model.enuns.Especialidade
import br.com.williancarbone.model.objetos.Vaga
import groovy.sql.GroovyRowResult
import spock.lang.Shared

import java.sql.SQLException


class VagaDaoSpec extends BaseSpec{

    @Shared
    VagaDao gerenciador

    def setupSpec() {
        gerenciador = new VagaDao(sqlH2)
    }


    def "Teste inserção vaga sucesso"(){
        given:

        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('nome','emial@gm','12345678','RJ','desc')")

        sqlH2.executeInsert("INSERT INTO empresa (cnpj,pais,empresa_id) VALUES (?,?,?)", ["12345678901234" ,"brasil", 1])


        Vaga vaga = new Vaga("vagaNome","vagadesc","12345678901234",[Especialidade.HT])


        when:

        Integer idGerado=gerenciador.gravarVaga(vaga)


        then:

        GroovyRowResult vagaCapturada = sqlH2.firstRow ("Select * FROM vaga WHERE id=?",[idGerado])

        vagaCapturada.contratante == vaga.contratante
        vagaCapturada.nome == vaga.nome
        vagaCapturada.descricao==vaga.descricao

        and:

        GroovyRowResult especialidadeGravada = sqlH2.firstRow("SELECT * FROM especialidade_vaga WHERE vaga = ?", [idGerado])
        especialidadeGravada.especialidade == Especialidade.HT.name()




    }

    def "Teste inserção vaga Falha"(){
        given:
        Vaga vagaFalha = new Vaga(null,null,null)

        when:

        gerenciador.gravarVaga(vagaFalha)

        then:
        thrown(SQLException)
    }


    def "Teste edição nome e descrição vaga"(){

        given:

        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('nome','emial@gm','12345678','RJ','desc')")

        sqlH2.executeInsert("INSERT INTO empresa (cnpj,pais,empresa_id) VALUES (?,?,?)", ["12345678901234" ,"brasil", 1])

        sqlH2.executeInsert("INSERT INTO vaga (nome,descricao,contratante) VALUES ('nomeAntigo','descAntiga','12345678901234')")



        when:

        gerenciador.trocarNomeDaVaga(1,"nomeNovo")
        gerenciador.trocarDescricaoDaVaga(1,"novaDesc")

        GroovyRowResult vagaRegistradaNoBanco= sqlH2.firstRow("SELECT * FROM vaga WHERE id=1")

        then:
        vagaRegistradaNoBanco.nome=="nomeNovo"
        vagaRegistradaNoBanco.descricao=="novaDesc"

        when:

        gerenciador.trocarNomeDaVaga(2,"novoNOme")

        then:

        thrown(DadoNaoEncontado)

        when:

        gerenciador.trocarDescricaoDaVaga(2,"novaDesc")

        then:

        thrown(DadoNaoEncontado)


    }



    def "Teste remocao vaga sucesso"() {
        given:

        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('empresaTeste','emailemp@gm','12345678','RJ','desc')")
        sqlH2.executeInsert("INSERT INTO empresa (cnpj,pais,empresa_id) VALUES (?,?,?)", ["12345678901234", "brasil", 1])

        Vaga vaga = new Vaga("vagateste", "Desc", "12345678901234", [Especialidade.HT, Especialidade.JAV])
        Integer idCriado = gerenciador.gravarVaga(vaga)

        when:
        gerenciador.removerVaga(idCriado)

        then:
        sqlH2.rows("SELECT * FROM vaga").size() ==0

        and:
        sqlH2.rows("SELECT * FROM especialidade_vaga").size()==0
    }

    def "Teste lançamento excecao ao  remover uma vaga com ID inexistente"() {


        when:
        gerenciador.removerVaga(99999)

        then:
        thrown(DadoNaoEncontado)
    }

    def "Teste excecao vaga id nao informado"() {
        when:
        gerenciador.removerVaga(null)

        then:
        thrown(DadoNaoInformado)
    }



    def "Teste capturar interessados vaga"() {
        given:

        sqlH2.execute("INSERT INTO usuario (id, nome, email, cep, estado) VALUES (1, 'Empresa', 'e@t.com', '1', 'RJ')")
        sqlH2.execute("INSERT INTO empresa (cnpj, pais, empresa_id) VALUES ('12345678901234', 'Brasil', 1)")


        Vaga vaga = new Vaga("nome", "Desc", "12345678901234", [])
        Integer idVaga = gerenciador.gravarVaga(vaga)

        expect:
        try {
            gerenciador.buscarCpfsInteressadosSemMatch(999, "12345678901234")

        } catch (Exception e) {
            e.class==DadoNaoInformado
        }

        when:
      List<String> resultadoVazio = gerenciador.buscarCpfsInteressadosSemMatch(idVaga, "12345678901234")

        then:
        resultadoVazio == []

        when:
        sqlH2.execute("INSERT INTO usuario (id, nome, email, cep, estado) VALUES (2, 'Candi', 'c@t.com', '1', 'RJ')")
        sqlH2.execute("INSERT INTO candidato (cpf, idade, candidato_id) VALUES ('111', 20, 2)")
        sqlH2.execute("INSERT INTO curtida (vaga, candidato) VALUES (?, '111')", [idVaga])

       List<String> resultadoComDados = gerenciador.buscarCpfsInteressadosSemMatch(idVaga, "12345678901234")

        then:
        resultadoComDados == ["111"]
    }
}


