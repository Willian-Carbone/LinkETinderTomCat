package br.com.williancarbone.dao

import br.com.williancarbone.exceptions.DadoNaoEncontado
import br.com.williancarbone.exceptions.DadoNaoInformado
import br.com.williancarbone.model.enuns.Especialidade
import br.com.williancarbone.model.enuns.Estado
import br.com.williancarbone.model.objetos.Candidato
import br.com.williancarbone.model.objetos.Curtida
import groovy.sql.GroovyRowResult
import spock.lang.Shared

import java.sql.SQLException


class CandidatoDaoSpec extends BaseSpec {

    @Shared
    CandidatoDao gerenciador

    def setupSpec() {
        gerenciador = new CandidatoDao(sqlH2)
    }


    def "Teste inserção candidato sucesso"(){
        given:

        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('nome','emial@gm','12345678','RJ','desc')")


        Candidato candidato=new Candidato("candidato","12345678901",20,"em@gm.com","12345678901", Estado.BAHIA,"desc",[Especialidade.JAV])
        candidato.setIdentificador(1)


        when:

        gerenciador.criarPerfil(candidato)


        then:

        GroovyRowResult linhaCapturada = sqlH2.firstRow ("Select * FROM candidato WHERE candidato_id=?",[candidato.identificador])

        linhaCapturada.cpf == candidato.cpf
        linhaCapturada.candidato_id == candidato.identificador
        linhaCapturada.idade==candidato.idade


    }

    def "Teste inserção candadidato Falha"(){
        given:
        Candidato candidatoInvalida = new Candidato(null,null,null,null,null,null,null,null)


        when:

        gerenciador.criarPerfil(candidatoInvalida)
        then:
        thrown(SQLException)
    }

    def "Teste Remocao de perfil candidato"(){
        given:

        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('empresaTeste','emailemp@gm','12345678','RJ','desc')")
        sqlH2.execute("INSERT INTO usuario (nome, email, cep, estado, descricao) VALUES ('Candidato', 'y@x.com', '12345678', 'RJ', '...')")
        sqlH2.executeInsert("INSERT INTO empresa (cnpj,pais,empresa_id) VALUES ('12345678901234', 'brasil', 1)")
        sqlH2.executeInsert("INSERT INTO vaga (nome,descricao,contratante) VALUES ('vaganome', 'vagaDesc','12345678901234')")
        sqlH2.executeInsert("INSERT INTO candidato (cpf,idade,candidato_id) VALUES ('12345678901', 20,2)")
        sqlH2.executeInsert("INSERT INTO matchs (candidato,empresa,vaga) VALUES('12345678901','12345678901234',1)")
        sqlH2.executeInsert("INSERT INTO curtida (vaga,candidato) VALUES (1,'12345678901')")


        when:

        gerenciador.removerPerfil("12345678901")

        List<GroovyRowResult> tabelaMatch= sqlH2.rows("SELECT * FROM matchs")
        List<GroovyRowResult> tabelaCandidato= sqlH2.rows("SELECT * FROM candidato")
        List<GroovyRowResult> tabelaCurtida =sqlH2.rows("SELECT * FROM curtida")

        then:

        !tabelaCandidato
        !tabelaCurtida
        !tabelaMatch

    }



    def "Teste CPF nulo"() {

        when:
        gerenciador.removerPerfil(null)

        then:
        thrown(DadoNaoInformado)

    }

    def "Teste candidato não existir"() {
        when:
        gerenciador.removerPerfil("00000000000")

        then:
        thrown(DadoNaoEncontado)
    }


    def "Teste troca idade candidato"(){
        given:

        sqlH2.execute("INSERT INTO usuario (nome, email, cep, estado, descricao) VALUES ('nomeCandidato', 'y@x.com', '12345678', 'RJ', '...')")
        sqlH2.executeInsert("INSERT INTO candidato (cpf,idade,candidato_id) VALUES (?,?,?)", ["12345678901", 20,1])



        when:

        gerenciador.editarPerfil(1,30)



        then:
        GroovyRowResult linhaCapturada= sqlH2.firstRow("Select * FROM candidato WHERE cpf='12345678901'")

        linhaCapturada.idade==30
    }



    def "Teste de busca de informações de um candidato"(){
        given:

        sqlH2.execute("INSERT INTO usuario (nome, email, cep, estado, descricao) VALUES ('nomeCandidato', 'y@x.com', '12345678', 'RJ', '...')")
        sqlH2.executeInsert("INSERT INTO candidato (cpf,idade,candidato_id) VALUES (?,?,?)", ["12345678901", 20,1])
        sqlH2.executeInsert("INSERT INTO especialidade_usuario (especialidade,usuario) VALUES (?,?)",["JAV",1])

        when:


        GroovyRowResult resultadosObtidos = gerenciador.capturarInformacoesPerfil("12345678901")

        then:
        resultadosObtidos.nome=="nomeCandidato"
        resultadosObtidos.email=="y@x.com"
        resultadosObtidos.competencias =="JAV"

    }





    def "Teste busca vagas que o candidato ainda não interagiu"() {
        given:

        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado, descricao) VALUES (?,?,?,?,?)", ['emp1', 'emp1@gmail', '12345678', 'SP', 'des'])
        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado, descricao) VALUES (?,?,?,?,?)", ['emp2', 'emp2@gmail', '12345678', 'RJ', 'desc'])
        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado, descricao) VALUES (?,?,?,?,?)", ['candidato', 'cand@meta.com', '12345678', 'RJ', 'cand'])

        sqlH2.execute("INSERT INTO empresa (cnpj, pais, empresa_id) VALUES (?,?,?)", ["12345678901234", "X", 1])
        sqlH2.execute("INSERT INTO empresa (cnpj, pais, empresa_id) VALUES (?,?,?)", ["12345678901235", "X", 2])


        sqlH2.execute("INSERT INTO vaga (nome, descricao, contratante) VALUES (?,?,?)", ["V1", "Vaga 1", "12345678901234"])
        sqlH2.execute("INSERT INTO vaga (nome, descricao, contratante) VALUES (?,?,?)", ["V2", "Vaga 2", "12345678901235"])

        sqlH2.executeInsert("INSERT INTO candidato (cpf,idade,candidato_id) VALUES (?,?,?)", ["12345678901", 20,3])



        sqlH2.execute("INSERT INTO curtida (candidato, vaga) VALUES (?, ?)", ["12345678901", 1])


        when:
        List<Map> resultado = gerenciador.buscarVagas("12345678901")

        then:
        resultado.size() == 1
        resultado[0].nome == "V2"


    }


    def "Teste se o candidato já deu match com todas as empresas"() {


        sqlH2.execute("INSERT INTO usuario (nome, email, cep, estado, descricao) VALUES ('Empresa', 'x@x.com', '12345678', 'RJ', '...')")
        sqlH2.execute("INSERT INTO usuario (nome, email, cep, estado, descricao) VALUES ('Candidato', 'y@x.com', '12345678', 'RJ', '...')")
        sqlH2.execute("INSERT INTO empresa (cnpj, pais, empresa_id) VALUES ('12345678901234', 'BR', 1)")
        sqlH2.execute("INSERT INTO vaga (nome, descricao, contratante) VALUES ('Vaga X', '...', '12345678901234')")

        sqlH2.executeInsert("INSERT INTO candidato (cpf,idade,candidato_id) VALUES (?,?,?)", ["12345678901", 20,2])


        sqlH2.execute("INSERT INTO matchs (candidato, empresa,vaga) VALUES (?, ?,?)", ["12345678901", "12345678901234",1])


        when:
       List<Map> listaDeVagas =gerenciador.buscarVagas("12345678901")

        then:
        listaDeVagas.size()==0

    }


    def "teste inserção curtida sucesso"(){
        given:

        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('empresa','emailemp@gm','12345678','RJ','desc')")
        sqlH2.executeInsert("INSERT INTO empresa (cnpj,pais,empresa_id) VALUES (?,?,?)", ["12345678901234", "brasil", 1])
        sqlH2.executeInsert("INSERT INTO vaga (nome,descricao,contratante) VALUES (?,?,?)", ["vaganome", "vagaDesc","12345678901234"])
        sqlH2.executeInsert("INSERT INTO candidato (cpf,idade,candidato_id) VALUES (?,?,?)", ["12345678901", 20,1])


        Curtida curtida = new Curtida("12345678901",1)




        when:
        Integer idGerado= gerenciador.salvarCurtida(curtida)


        then:

        GroovyRowResult linhaCapturada = sqlH2.firstRow ("Select * FROM curtida WHERE id=?",[idGerado])

        linhaCapturada.vaga==curtida.idVaga
        linhaCapturada.candidato==curtida.cpf




    }

    def "teste inserção curtida falha"(){


        when:

        gerenciador.salvarCurtida(null)

        then:

        thrown(DadoNaoInformado)




    }


    def"Teste de busca de ID de Candidato"()
    {

        given:

        sqlH2.execute("INSERT INTO usuario (nome, email, cep, estado, descricao) VALUES ('candidatoTeste', 'y@x.com', '12345678', 'RJ', '...')")
        sqlH2.executeInsert("INSERT INTO candidato (cpf,idade,candidato_id) VALUES (?,?,?)", ["12345678901", 20,1])



        when:

        Integer idObtido= gerenciador.capturarId("12345678901")

        then:
        idObtido==1


    }


    def"Teste de busca cpf existente"()
    {

        given:

        sqlH2.execute("INSERT INTO usuario (nome, email, cep, estado, descricao) VALUES ('candidatoTeste', 'y@x.com', '12345678', 'RJ', '...')")
        sqlH2.executeInsert("INSERT INTO candidato (cpf,idade,candidato_id) VALUES (?,?,?)", ["12345678901", 20,1])



        when:

        Boolean cpfexistente= gerenciador.buscarExistenciaCredencial("12345678901")
        Boolean cpfInexistente = gerenciador.buscarExistenciaCredencial("123")
        then:
        cpfexistente
        !cpfInexistente


    }


    def "Teste buscar matchs do candidato"(){


        given:
        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('empresaTeste','emailemp@gm','12345678','RJ','desc')")
        sqlH2.execute("INSERT INTO usuario (nome, email, cep, estado, descricao) VALUES ('candidatoTeste', 'y@x.com', '12345678', 'RJ', '...')")
        sqlH2.executeInsert("INSERT INTO empresa (cnpj,pais,empresa_id) VALUES (?,?,?)", ["12345678901234", "brasil", 1])
        sqlH2.executeInsert("INSERT INTO vaga (nome,descricao,contratante) VALUES (?,?,?)", ["vaganome", "vagaDesc", "12345678901234"])
        sqlH2.executeInsert("INSERT INTO candidato (cpf,idade,candidato_id) VALUES (?,?,?)", ["12345678901", 20, 2])
        sqlH2.executeInsert("INSERT INTO matchs (candidato,empresa,vaga) VALUES(?,?,?)", ["12345678901", "12345678901234", 1])



        when:

        List<Map> linhasCapturadas = gerenciador.buscarMatchs("12345678901")

        then:
        linhasCapturadas.size() == 1
        linhasCapturadas[0].nome == "empresaTeste"

    }



}
