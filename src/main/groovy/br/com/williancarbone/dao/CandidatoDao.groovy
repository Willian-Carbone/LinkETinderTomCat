package br.com.williancarbone.dao

import br.com.williancarbone.dao.bases.BaseDao
import br.com.williancarbone.dao.interfaces.CurtidaDao
import br.com.williancarbone.dao.interfaces.Matchable
import br.com.williancarbone.dao.interfaces.PerfilDao
import br.com.williancarbone.exceptions.CredencialDuplicadaException
import br.com.williancarbone.exceptions.DadoNaoEncontado
import br.com.williancarbone.exceptions.DadoNaoInformado
import br.com.williancarbone.exceptions.DadoRepetido
import br.com.williancarbone.model.objetos.Candidato
import br.com.williancarbone.model.objetos.Curtida
import groovy.sql.GroovyRowResult
import groovy.sql.Sql



class CandidatoDao extends BaseDao implements Matchable, CurtidaDao, PerfilDao<Candidato, Integer> {

    CandidatoDao(Sql sql) {
        super(sql)
    }

    @Override
    Integer criarPerfil(Candidato candidato) {

        if (!candidato) {
            throw new DadoNaoInformado("Dado candidato faltante")
        }

        if(buscarExistenciaCredencial(candidato.cpf)){
            throw new CredencialDuplicadaException("O cpf informado ja esta em uso")
        }


        List<List<Object>> insercao = sql.executeInsert("INSERT INTO  candidato (cpf,idade,candidato_id) VALUES (${candidato.cpf},${candidato.idade},${candidato.identificador})")
        Integer idGerado = insercao[0][2] as Integer

        return idGerado


    }

    @Override
    void removerPerfil(String cpf) {


        if (!cpf) {
            throw new DadoNaoInformado("Dado cpf nao informado")
        }


        sql.withTransaction {

            GroovyRowResult candidato = sql.firstRow("SELECT candidato_id FROM candidato WHERE cpf = ?", [cpf])

            if (!candidato) {
                throw new DadoNaoEncontado("Candidato não encontrado")
            }

            Integer idParaDeletar = candidato.candidato_id as Integer


            sql.execute "DELETE FROM matchs WHERE candidato = ?", [cpf]
            sql.execute "DELETE FROM curtida WHERE candidato = ?", [cpf]


            sql.execute "DELETE FROM candidato WHERE candidato_id = ?", [idParaDeletar]
            sql.execute "DELETE FROM especialidade_usuario WHERE usuario = ?", [idParaDeletar]
            sql.execute "DELETE FROM usuario WHERE id = ?", [idParaDeletar]
        }


    }

    @Override
    void editarPerfil(Integer id, Integer novaIdade) {

        if (!id) {
            throw new DadoNaoInformado("Dado identificador ausente")
        }

        if(!novaIdade){
            throw new DadoNaoInformado("nova idade nao informada")
        }

        sql.execute "UPDATE candidato SET idade=${novaIdade} WHERE candidato_id=${id}"


    }

    @Override
    Map capturarInformacoesPerfil(String identificador) {

        if (!identificador) {
            throw new DadoNaoInformado("dado cpf ausente")
        }


        String busca = """
                SELECT u.id,u.nome,u.email,u.cep,u.estado,u.descricao,c.idade,c.cpf, STRING_AGG(eu.especialidade, ', ')  AS competencias
                FROM usuario as u
                JOIN candidato as c ON u.id=c.candidato_id
                LEFT JOIN especialidade_usuario as eu ON u.id=eu.usuario
                WHERE c.cpf=?
                GROUP BY u.id, u.nome, u.email, u.cep, u.estado,u.descricao, c.idade,c.cpf
        """

        GroovyRowResult infos = sql.firstRow(busca, [identificador])



        return infos


    }

    @Override
    Integer capturarId(String cpf) {

        if (!cpf) {
            throw new DadoNaoInformado("dado cpf não informado")
        }


        GroovyRowResult linhaUsuario = sql.firstRow("SELECT candidato_id AS id FROM candidato WHERE cpf=?", [cpf])

        if (!linhaUsuario) {
            throw new DadoNaoEncontado("Id não  encontrado")
        }

        Integer idUsuario = linhaUsuario.id as Integer
        return idUsuario


    }


    @Override
    List<Map> buscarVagas(String cpf) {

        if (!cpf) {
            throw new DadoNaoInformado("dado cpf não informado")
        }

        if(!sql.firstRow("SELECT 1 FROM candidato WHERE cpf = ?", [cpf])) {throw new DadoNaoEncontado("Candidato nao existe")}

        String buscaVagas = """
            SELECT v.id AS id_vaga, v.nome, v.descricao,STRING_AGG(ev.especialidade, ', ') AS competencias
            FROM vaga v JOIN  empresa e ON v.contratante = e.cnpj
            JOIN  usuario u ON e.empresa_id = u.id
            LEFT JOIN especialidade_vaga ev ON v.id = ev.vaga
            WHERE 
 
            v.id NOT IN (
                SELECT vaga FROM curtida WHERE candidato = ?
            )
           
            AND e.cnpj NOT IN (
                SELECT empresa FROM matchs WHERE candidato = ?
            )
            GROUP BY 
            v.id, v.nome, v.descricao, u.id, e.cnpj
            """


        List<GroovyRowResult> vagasEncontradas = sql.rows(buscaVagas, [cpf, cpf])


        return vagasEncontradas


    }

    @Override
    List<Map> buscarMatchs(String cpf) {

        if (!cpf) {
            throw new DadoNaoInformado("dado cpf não informado")
        }



        String buscaMatchs = """
            SELECT u.nome, u.descricao, u.email, emp.pais, u.estado, m.vaga
            FROM usuario AS u
            JOIN empresa AS emp ON u.id = emp.empresa_id
            JOIN matchs AS m ON m.empresa = emp.cnpj
            WHERE m.candidato = ?
            """

        List<GroovyRowResult> matchsEncontrados = sql.rows(buscaMatchs, [cpf])



        return matchsEncontrados
    }

    @Override
    Integer salvarCurtida(Curtida curtida) {
        if (!curtida || !curtida.cpf || !curtida.idVaga) {
            throw new DadoNaoInformado("Informações sobre a curtida estão faltando (CPF ou ID da Vaga)")
        }


        GroovyRowResult registroExistente = sql.firstRow(
                "SELECT 1 FROM curtida WHERE candidato = ? AND vaga = ?",
                [curtida.cpf, curtida.idVaga]
        )

        if (registroExistente) {
            throw new DadoRepetido("Você já curtiu essa vaga")
        }

        List<List<Object>> insercao = sql.executeInsert(
                "INSERT INTO curtida (vaga, candidato) VALUES (?, ?)",
                [curtida.idVaga, curtida.cpf]
        )


        return insercao[0][0] as Integer
    }

    @Override
    Boolean buscarExistenciaCredencial(String cpf){
        return sql.firstRow("SELECT 1 FROM candidato WHERE cpf = ?", [cpf]) ? true : false
    }

    String capturarCpfPorId(Integer id){
       String cpf = sql.firstRow("SELECT cpf FROM candidato where candidato_id=${id}")[0]

        return cpf
    }
}
