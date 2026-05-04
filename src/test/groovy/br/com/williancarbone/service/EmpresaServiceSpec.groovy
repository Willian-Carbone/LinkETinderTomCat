package br.com.williancarbone.service

import br.com.williancarbone.dao.BaseSpec
import br.com.williancarbone.exceptions.DadoNaoEncontado
import br.com.williancarbone.exceptions.DadoNaoInformado
import br.com.williancarbone.infra.conexoes.CriadorConexao
import groovy.sql.GroovyRowResult


class EmpresaServiceSpec extends BaseSpec {

    EmpresaService service

    def setup() {
        CriadorConexao criadorConexaoFake = Mock(CriadorConexao)
        criadorConexaoFake.criarConexao() >> sqlH2

        service = new EmpresaService(criadorConexaoFake)
    }

    def "Teste criação perfil empresa"() {
        given:
        Map info = [
                nome          : "empresa teste",
                email         : "contato@gmail.com",
                cep           : "12345678",
                estado        : "Sao Paulo",
                descricao     : "desc",
                pais          : "Brasil",
                cnpj          : "12345678901234",
                especialidades: ["java","html"]
        ]

        when:
        service.criarPerfil(info)

        then:
        GroovyRowResult usuario = sqlH2.firstRow("SELECT * FROM usuario WHERE descricao = 'desc'")
        GroovyRowResult empresa = sqlH2.firstRow("SELECT * FROM empresa WHERE cnpj = '12345678901234'")
        List<GroovyRowResult> habilidades = sqlH2.rows("SELECT * FROM especialidade_usuario WHERE usuario= ?", [usuario.id])

        usuario != null
        empresa != null
        empresa.empresa_id == usuario.id
        habilidades.size() == 2
    }

    def "Teste remocao perfil empresa"() {
        given:
        String cnpj = "12345678901234"
        sqlH2.execute("INSERT INTO usuario (nome, email, cep, estado, descricao) VALUES ('empresa teste', 'y@x.com', '12345678', 'RJ', '...')")
        sqlH2.executeInsert("INSERT INTO empresa (cnpj,pais,empresa_id) VALUES (?,?,?)", ["12345678901234", "brasil", 1])

        when:
        service.removerPerfil(cnpj)

        then:
        sqlH2.firstRow("SELECT * FROM empresa WHERE cnpj = ?", [cnpj]) == null

        when:
        service.removerPerfil("00")

        then:
        thrown(DadoNaoEncontado)
    }


    def "Teste CapturarInfosDoPerfil Empresa"() {
        given:
        String cnpj = "12345678901234"

        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('empresaTeste','emailemp@gm','12345678','RJ','desc')")
        sqlH2.executeInsert("INSERT INTO empresa (cnpj,pais,empresa_id) VALUES (?,?,?)", [cnpj, "brasil", 1])



        when:
        Map resultado = service.capturarInfosDoPerfil(cnpj)

        then:
        resultado != null
        resultado.id==1
    }

    def "Teste Match realizado pela empresa"() {
        given:
        String cnpj = "12345678901234"
        String cpf = "12345678901"


        sqlH2.execute("INSERT INTO usuario (nome, email, cep, estado, descricao) VALUES ('empresa teste', 'z@x.com', '12345678', 'RJ', '...')")
        sqlH2.execute("INSERT INTO usuario (nome, email, cep, estado, descricao) VALUES ('candidato teste', 'y@x.com', '12345678', 'RJ', '...')")
        sqlH2.execute("INSERT INTO candidato (cpf, idade, candidato_id) VALUES (?, 25, 2)", [cpf])
        sqlH2.execute("INSERT INTO empresa (cnpj, pais, empresa_id) VALUES (?, 'Brasil', 1)", [cnpj])
        sqlH2.execute("INSERT INTO vaga ( nome, descricao ,contratante) VALUES ('vaga', 'Vaga Teste', ?)", [cnpj])


        Map infoMatch = [
                idVaga     : 1,
                idCandidato: 2,
                cnpj       : cnpj
        ]

        when:
        service.realizarMatch(infoMatch)

        then:
        GroovyRowResult matchGravado = sqlH2.firstRow("SELECT * FROM matchs WHERE vaga = ?", [1])
        matchGravado != null
        matchGravado.candidato == cpf
        matchGravado.empresa == cnpj
    }

    def "Teste criacao match dados ausentes"() {
        when:
        service.realizarMatch([idVaga: 1])

        then:
        thrown(DadoNaoInformado)
    }



    def"Teste de busca de ID da empresa"()
    {


        given:

        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('nome','emial@gm','12345678','RJ','desc')")
        sqlH2.executeInsert("INSERT INTO empresa (cnpj,pais,empresa_id) VALUES (?,?,?)", ["12345678901234", "brasil", 1])


        when:


        Integer idObtido = service.capturarIdPerfil("12345678901234")

        then:
        idObtido==1


    }

    def "Teste buscar matchs da empresa"(){


        given:
        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('empresaTeste','emailemp@gm','12345678','RJ','desc')")
        sqlH2.execute("INSERT INTO usuario (nome, email, cep, estado, descricao) VALUES ('candidatoTeste', 'y@x.com', '12345678', 'RJ', '...')")
        sqlH2.executeInsert("INSERT INTO empresa (cnpj,pais,empresa_id) VALUES (?,?,?)", ["12345678901234", "brasil", 1])
        sqlH2.executeInsert("INSERT INTO vaga (nome,descricao,contratante) VALUES (?,?,?)", ["vaganome", "vagaDesc", "12345678901234"])
        sqlH2.executeInsert("INSERT INTO candidato (cpf,idade,candidato_id) VALUES (?,?,?)", ["12345678901", 20, 2])
        sqlH2.executeInsert("INSERT INTO matchs (candidato,empresa,vaga) VALUES(?,?,?)", ["12345678901", "12345678901234", 1])



        when:

        List<Map> linhasCapturadas = service.buscarMatchsParaOPerfil("12345678901234")

        then:
        linhasCapturadas.size() == 1
        linhasCapturadas[0].nome == "candidatoTeste"

    }


    def "Teste busca vagas empresa"() {

        given:
        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('empresaTeste','emailemp@gm','12345678','RJ','desc')")
        sqlH2.executeInsert("INSERT INTO empresa (cnpj,pais,empresa_id) VALUES (?,?,?)", ["12345678901234", "brasil", 1])
        sqlH2.executeInsert("INSERT INTO vaga (nome,descricao,contratante) VALUES (?,?,?)", ["vaganome", "vagaDesc", "12345678901234"])

        when:

        List<Map> resultadosObtidos= service.buscarVagasParaOPerfil("12345678901234")

        then:

        resultadosObtidos.size()==1
        resultadosObtidos[0].id_vaga==1

    }


}

