package br.com.williancarbone.service

import br.com.williancarbone.dao.BaseSpec
import br.com.williancarbone.exceptions.DadoNaoInformado
import br.com.williancarbone.infra.conexoes.CriadorConexao
import groovy.sql.GroovyRowResult


class CandidatoServiceSpec extends BaseSpec {

    CandidatoService service

    def setup(){
        CriadorConexao criadorConexaoFake=Mock(CriadorConexao)
        criadorConexaoFake.criarConexao() >> sqlH2

        service = new CandidatoService(criadorConexaoFake)
    }



    def "Teste criação perfil"() {

        given:
        Map info = [
                nome: "candidato exemplo",
                email: "email@teste.com",
                cep: "12345678",
                estado: "Sao Paulo",
                descricao: "Desenvolvedor",
                idade: 25,
                cpf: "123.456.789-00",
                especialidades: ["java", "html"]
        ]

        when:

        service.criarPerfil(info)

        then:
        GroovyRowResult usuarios = sqlH2.firstRow("SELECT * FROM usuario")
        GroovyRowResult candidatos =  sqlH2.firstRow("SELECT * FROM candidato")
        GroovyRowResult habilidades = sqlH2.firstRow("SELECT * FROM especialidade_usuario")

        usuarios
        candidatos
        habilidades





    }

    def "Deve remover perfil quando o CPF for informado"() {
        given:



        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('empresaTeste','emailemp@gm','12345678','RJ','desc')")
        sqlH2.execute("INSERT INTO usuario (nome, email, cep, estado, descricao) VALUES ('Candidato', 'y@x.com', '12345678', 'RJ', '...')")
        sqlH2.executeInsert("INSERT INTO empresa (cnpj,pais,empresa_id) VALUES ('12345678901234', 'brasil', 1)")
        sqlH2.executeInsert("INSERT INTO vaga (nome,descricao,contratante) VALUES ('vaganome', 'vagaDesc','12345678901234')")
        sqlH2.executeInsert("INSERT INTO candidato (cpf,idade,candidato_id) VALUES ('12345678901', 20,2)")
        sqlH2.executeInsert("INSERT INTO matchs (candidato,empresa,vaga) VALUES('12345678901','12345678901234',1)")
        sqlH2.executeInsert("INSERT INTO curtida (vaga,candidato) VALUES (1,'12345678901')")


        when:

        service.removerPerfil("12345678901")

        List<GroovyRowResult> tabelaMatch= sqlH2.rows("SELECT * FROM matchs")
        List<GroovyRowResult> tabelaCandidato= sqlH2.rows("SELECT * FROM candidato")
        List<GroovyRowResult> tabelaCurtida =sqlH2.rows("SELECT * FROM curtida")

        then:

        !tabelaCandidato
        !tabelaCurtida
        !tabelaMatch





    }



    def "Teste informações faltantes"() {
        given:

        when:
        service.removerPerfil(null)

        then:
        thrown(DadoNaoInformado)

    }



    def "Teste CapturarInfosDoPerfil"() {
        given:

        sqlH2.execute("INSERT INTO usuario ( nome, email, cep, estado, descricao) VALUES ( 'nome', 'w@test.com', '12345678', 'SP', 'Dev')")
        sqlH2.execute("INSERT INTO candidato (cpf, idade, candidato_id) VALUES ('12345678901', 25, 1)",)


        when:
        Map resultado = service.capturarInfosDoPerfil("12345678901")

        then:
        resultado != null
        resultado.id == 1
    }



    def "Teste CapturarIdPerfil"() {
        given:


        sqlH2.execute("INSERT INTO usuario (id, nome, email, cep, estado, descricao) VALUES (1, 'nome', 'id@test.com', '12345678', 'RJ', '...')")
        sqlH2.execute("INSERT INTO candidato (cpf, idade, candidato_id) VALUES ('12345678901', 30, 1)", )

        when:
        Integer idEncontrado = service.capturarIdPerfil("12345678901")

        then:
        idEncontrado == 1
    }


    def "Teste BuscarVagasParaOPerfil"() {

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
        List<Map> resultado = service.buscarVagasParaOPerfil("12345678901")

        then:
        resultado.size() == 1
        resultado[0].nome == "V2"
        resultado[0].cnpj_empresa == "12345678901235"


    }


    def "Teste BuscarMatchsParaOPerfil"() {


        given:
        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('empresaTeste','emailemp@gm','12345678','RJ','desc')")
        sqlH2.execute("INSERT INTO usuario (nome, email, cep, estado, descricao) VALUES ('candidatoTeste', 'y@x.com', '12345678', 'RJ', '...')")
        sqlH2.executeInsert("INSERT INTO empresa (cnpj,pais,empresa_id) VALUES (?,?,?)", ["12345678901234", "brasil", 1])
        sqlH2.executeInsert("INSERT INTO vaga (nome,descricao,contratante) VALUES (?,?,?)", ["vaganome", "vagaDesc", "12345678901234"])
        sqlH2.executeInsert("INSERT INTO candidato (cpf,idade,candidato_id) VALUES (?,?,?)", ["12345678901", 20, 2])
        sqlH2.executeInsert("INSERT INTO matchs (candidato,empresa,vaga) VALUES(?,?,?)", ["12345678901", "12345678901234", 1])



        when:

        List<Map> linhasCapturadas = service.buscarMatchsParaOPerfil("12345678901")

        then:
        linhasCapturadas.size() == 1
        linhasCapturadas[0].nome == "empresaTeste"

    }

    def "Teste SalvarCurtida"() {
        given:

        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('empresa','emailemp@gm','12345678','RJ','desc')")
        sqlH2.executeInsert("INSERT INTO empresa (cnpj,pais,empresa_id) VALUES (?,?,?)", ["12345678901234", "brasil", 1])
        sqlH2.executeInsert("INSERT INTO vaga (nome,descricao,contratante) VALUES (?,?,?)", ["vaganome", "vagaDesc","12345678901234"])
        sqlH2.executeInsert("INSERT INTO candidato (cpf,idade,candidato_id) VALUES (?,?,?)", ["12345678901", 20,1])







        when:
        Integer idGerado= service.salvarCurtida([cpf:"12345678901",idVaga:1])


        then:

        GroovyRowResult linhaCapturada = sqlH2.firstRow ("Select * FROM curtida WHERE id=?",[idGerado])

        linhaCapturada.vaga==1
        linhaCapturada.candidato=='12345678901'





    }
}
