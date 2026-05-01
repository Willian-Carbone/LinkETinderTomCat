package br.com.williancarbone.service

import br.com.williancarbone.dao.BaseSpec
import br.com.williancarbone.exceptions.DadoNaoEncontado
import br.com.williancarbone.exceptions.DadoNaoInformado
import br.com.williancarbone.infra.conexoes.CriadorConexao
import groovy.sql.GroovyRowResult




class VagaServiceSpec extends BaseSpec {

    VagaService service


    def setup() {
        CriadorConexao criadorConexaoFake = Mock(CriadorConexao)
        criadorConexaoFake.criarConexao() >> sqlH2

        service = new VagaService(criadorConexaoFake)
    }



    def "Teste inserção vaga sucesso"(){

        given:

        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('nome','emial@gm','12345678','RJ','desc')")

        sqlH2.executeInsert("INSERT INTO empresa (cnpj,pais,empresa_id) VALUES (?,?,?)", ["12345678901234" ,"brasil", 1])



        when:

        Integer idGerado= service.CriarVaga([nome:"teste", descricao: "Desc", contratante: "12345678901234",requisitos: ['Css','java']])


        then:

        GroovyRowResult vagaCapturada = sqlH2.firstRow ("Select * FROM vaga WHERE id=?",[idGerado])


        vagaCapturada.contratante== "12345678901234"
        vagaCapturada.nome == "teste"
        vagaCapturada.descricao=="Desc"

        and:

        List<GroovyRowResult> especialidadeGravada = sqlH2.rows("SELECT * FROM especialidade_vaga WHERE vaga = ?", [idGerado])
        especialidadeGravada.size()==2





    }

    def "Teste inserção vaga Falha"(){

        when:

        service.CriarVaga([:])

        then:
        thrown(DadoNaoInformado)
    }


    def "Teste remocao vaga sucesso"() {
        given:

        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('empresaTeste','emailemp@gm','12345678','RJ','desc')")
        sqlH2.executeInsert("INSERT INTO empresa (cnpj,pais,empresa_id) VALUES (?,?,?)", ["12345678901234", "brasil", 1])
        sqlH2.executeInsert("INSERT INTO vaga (nome,descricao,contratante) VALUES ('nome','desc','12345678901234')")



        when:
        service.deletarVaga("12345678901234", 1)

        then:
        sqlH2.rows("SELECT * FROM vaga").size() ==0

        and:
        sqlH2.rows("SELECT * FROM especialidade_vaga").size()==0
    }

    def "Teste lançamento excecao ao  remover uma vaga de outra empresa diferente da do cnpj passado"() {

        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('empresaTeste','emailemp@gm','12345678','RJ','desc')")
        sqlH2.executeInsert("INSERT INTO empresa (cnpj,pais,empresa_id) VALUES (?,?,?)", ["12345678901234", "brasil", 1])
        sqlH2.executeInsert("INSERT INTO vaga (nome,descricao,contratante) VALUES ('nome','desc','12345678901234')")



        when:
        service.deletarVaga("12345678901234", 3)


        then:
        thrown(DadoNaoEncontado)
    }




    def "Teste capturar candidatos interessados em uma vaga com sucesso"() {
        given:
        String cnpjEmpresa = "12345678901234"
        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado, descricao) VALUES ('teste emp', 'c@tech.com', '12345678', 'RJ', 'Desc')")
        sqlH2.executeInsert("INSERT INTO empresa (cnpj, pais, empresa_id) VALUES (?, 'Brasil', 1)", [cnpjEmpresa])
        sqlH2.executeInsert("INSERT INTO vaga (id, nome, descricao, contratante) VALUES (1, 'vaga', 'Vaga teste', ?)", [cnpjEmpresa])


        String cpfCandidato = "11122233344"
        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado, descricao) VALUES ('Candidato Teste', 'candi@teste.com', '12345678', 'SP', 'desc')")
        sqlH2.executeInsert("INSERT INTO candidato (cpf, idade, candidato_id) VALUES (?, 25, 2)", [cpfCandidato])


        sqlH2.executeInsert("INSERT INTO especialidade_usuario (usuario, especialidade) VALUES (2, 'JAV')")
        sqlH2.executeInsert("INSERT INTO especialidade_usuario (usuario, especialidade) VALUES (2, 'HT')")

        and:
        sqlH2.executeInsert("INSERT INTO curtida (candidato, vaga) VALUES (?, 1)", [cpfCandidato])

        when:
        List<Map> interessados = service.capturarInteressadosEmVaga([cnpj: cnpjEmpresa, idVaga: 1])

        then:
        interessados.size() == 1
        interessados[0].id == 2
        interessados[0].competencias.contains("JAV")
        interessados[0].competencias.contains("HT")

        and:
        !interessados[0].containsKey("nome")
        !interessados[0].containsKey("cpf")
    }

    def "Teste falha ao capturar interessados com dados ausentes"() {
        when:
        service.capturarInteressadosEmVaga([cnpj: null, idVaga: 1])

        then:
        thrown(DadoNaoInformado)
    }

}
