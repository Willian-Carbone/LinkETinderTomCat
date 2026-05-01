package br.com.williancarbone.dao

import groovy.sql.Sql
import spock.lang.Shared
import spock.lang.Specification

class BaseSpec extends  Specification{

    @Shared
    Sql sqlH2

    def setupSpec() {

        sqlH2 = Sql.newInstance("jdbc:h2:mem:linkEtinder;DATABASE_TO_UPPER=FALSE", "u", "", "org.h2.Driver")

        URL scriptCriacaoBanco = getClass().getResource('/comandosCriacaoBancoDados.sql')
        assert scriptCriacaoBanco : "Arquivo de script SQL não encontrado em src/test/resources!"

        sqlH2.execute(scriptCriacaoBanco.text)


    }


    def cleanup() {

        sqlH2.execute("SET REFERENTIAL_INTEGRITY FALSE")


        def tabelas = [
                "especialidade_usuario", "candidato", "usuario",
                "curtida", "empresa", "especialidade_vaga",
                "vaga", "matchs"
        ]

        tabelas.each { tabela ->
            sqlH2.execute("TRUNCATE TABLE ${tabela} RESTART IDENTITY".toString())
        }

        sqlH2.execute("SET REFERENTIAL_INTEGRITY TRUE")
    }

    def cleanupSpec() {
        sqlH2.close()
    }



}
