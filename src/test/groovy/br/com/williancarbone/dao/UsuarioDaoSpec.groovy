package br.com.williancarbone.dao

import br.com.williancarbone.exceptions.DadoNaoEncontado
import br.com.williancarbone.exceptions.DadoNaoInformado
import br.com.williancarbone.model.enuns.Especialidade
import br.com.williancarbone.model.enuns.Estado
import br.com.williancarbone.model.objetos.Candidato
import br.com.williancarbone.model.objetos.EspecialidadeUsuario
import br.com.williancarbone.model.objetos.Usuario
import groovy.sql.GroovyRowResult
import spock.lang.Shared


class UsuarioDaoSpec extends BaseSpec {

    @Shared
    UsuarioDao gerenciador

    def setupSpec() {
        gerenciador = new UsuarioDao(sqlH2)
    }


    void "Teste inserção usuário sucesso"() {

        given:

        Usuario usuarioExemplo = new Candidato("nome", "12345678901", 20, "email@email.com", "12345678", Estado.GOIAS, "Descricao", [Especialidade.ANG, Especialidade.JAV])


        when:

        Integer idDoUsuarioGravado = gerenciador.salvarUsuario(usuarioExemplo)

        then:

        GroovyRowResult linhaCapturada = sqlH2.firstRow("Select * FROM usuario WHERE id=?", [idDoUsuarioGravado])


        with(linhaCapturada) {
            nome == usuarioExemplo.nome
            email == usuarioExemplo.email
            cep == usuarioExemplo.cep
            estado == usuarioExemplo.estado.sigla
            descricao == usuarioExemplo.descricao
        }

    }


    void "Teste inserção usuário Falha"(){

        when:

        gerenciador.salvarUsuario(null)

        then:
        thrown(DadoNaoInformado)
    }


    def "Teste inserção especialidade Usuario sucesso"(){
        given:


        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('nome','email','12345678','SP','desc')")


        EspecialidadeUsuario especialidadeUsuario = new EspecialidadeUsuario(1, Especialidade.JAV)

        when:

        Integer idDaEspecialidadeGravada = gerenciador.salvarEspecialidadeUsuario(especialidadeUsuario)

        then:

        GroovyRowResult linhaCapturada = sqlH2.firstRow ("Select * FROM especialidade_usuario WHERE id=${idDaEspecialidadeGravada}")

        linhaCapturada.especialidade==especialidadeUsuario.especialidade.name()
        linhaCapturada.usuario == especialidadeUsuario.idUsuario


    }

    def "Teste inserção especialidade Usuario Falha"(){


        when:

        gerenciador.salvarEspecialidadeUsuario(null)

        then:
        thrown(DadoNaoInformado)
    }

    def "Teste edição de CEP de usuario"(){
        given:
        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('nome1','email1@gm','12345678','RJ','desc')")


        when:
        gerenciador.editarCepUsuario(1,"87654321")

        then:

        GroovyRowResult linhaCapturada = sqlH2.firstRow ("Select * FROM usuario WHERE id=1")

        linhaCapturada.cep=="87654321"




    }

    def "Teste Edicao de descrição Usuario"() {
        given:
        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('nome1','email1@gm','12345678','RJ','desc')")

        when:
        gerenciador.editarDescricaoUsuario(1,"novaDesc")


        then:

        GroovyRowResult linhaCapturada = sqlH2.firstRow("Select * FROM usuario WHERE id=1")

        linhaCapturada.descricao == "novaDesc"
    }


    def "Teste edição de email de usuario"(){
        given:
        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('nome1','email1@gm','12345678','RJ','desc')")


        when:
        gerenciador.editarEmailUsuario(1,"novoEmail@gmail")



        then:
        GroovyRowResult linhaCapturada = sqlH2.firstRow ("Select * FROM usuario WHERE id=1")
        linhaCapturada.email=="novoEmail@gmail"


    }


    def "Teste edição de Estado de usuario"() {
        given:
        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('nome1','email1@gm','12345678','RJ','desc')")


        when:
        gerenciador.editarEstado(1, Estado.BAHIA)



        then:

        GroovyRowResult linhaCapturada = sqlH2.firstRow("Select * FROM usuario WHERE id=1")

        linhaCapturada.estado == "BH"


    }


    def "Teste edição de nome de usuario"(){
        given:
        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('nome1','emial@gm','12345678','RJ','desc')")


        when:
        gerenciador.editarNomeUsuario(1,"nomeNovo")



        then:

        GroovyRowResult linhaCapturada = sqlH2.firstRow ("Select * FROM usuario WHERE id=1")

        linhaCapturada.nome=="nomeNovo"


    }

    def "Teste edicao especialidade do usuario"(){
        given:
        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('nome','email','12345678','SP','desc')")
        sqlH2.executeInsert "INSERT INTO especialidade_usuario (especialidade,usuario) VALUES ('JAV',1)"


        EspecialidadeUsuario especialidade1 = new EspecialidadeUsuario(1,Especialidade.CS)
        EspecialidadeUsuario especialidade2 = new EspecialidadeUsuario(1,Especialidade.HT)

        when:

        gerenciador.editarEspecialidadeDoUsuario(1,[especialidade1,especialidade2])



        then:

        List<Map> linhasCapturadas = sqlH2.rows("SELECT * FROM especialidade_usuario")

        linhasCapturadas.size()==2
        linhasCapturadas[0].especialidade==especialidade1.especialidade.name()
        linhasCapturadas[1].especialidade==especialidade2.especialidade.name()

    }

    def "Teste de busca de usuario por especialidade"(){
        given:
        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('user1','email','12345678','SP','desc')")

        sqlH2.executeInsert("INSERT INTO candidato (cpf,idade,candidato_id) VALUES (?,?,?)", ["12345678901", 20,1])

        sqlH2.executeInsert "INSERT INTO especialidade_usuario (especialidade,usuario) VALUES ('JAV',1)"

        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('user2','email2','12345678','SP','desc')")
        sqlH2.executeInsert "INSERT INTO especialidade_usuario (especialidade,usuario) VALUES ('ANG',2)"


        when:

        List<Map> usuariosEncontrados =gerenciador.buscarPorHabilidades([Especialidade.JAV] as List<EspecialidadeUsuario>,"candidato")

        then:
        usuariosEncontrados.size()==1
        usuariosEncontrados[0].id==1

    }



    def "Teste quando não encontrar empresas no estado"() {
        given:

        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('user1','email2','12345678','RJ','desc')")
        sqlH2.executeInsert("INSERT INTO empresa (cnpj,pais,empresa_id) VALUES ('12345678901234','Brasil',1)")


        when:
        List<Map> empresas =gerenciador.buscarPorEstado(Estado.BAHIA, "empresa")

        then:
        !empresas
    }

    def "Deve encontrar candidatos em SP com sucesso"() {
        given:
        sqlH2.executeInsert("INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES ('user1','email1','12345678','SP','desc')")
        sqlH2.executeInsert("INSERT INTO candidato (cpf,idade,candidato_id) VALUES ('12345678901',20,1)")
        when:
        List<Map> lista = gerenciador.buscarPorEstado(Estado.SAOPAULO, "candidato")

        then:
        lista.size() == 1
        lista[0].id == 1
    }












}
