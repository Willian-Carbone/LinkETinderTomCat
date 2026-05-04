package br.com.williancarbone.dao

import br.com.williancarbone.dao.bases.BaseDao
import br.com.williancarbone.dao.interfaces.MatchDao
import br.com.williancarbone.dao.interfaces.Matchable
import br.com.williancarbone.dao.interfaces.PerfilDao
import br.com.williancarbone.exceptions.CredencialDuplicadaException
import br.com.williancarbone.exceptions.DadoNaoEncontado
import br.com.williancarbone.exceptions.DadoNaoInformado
import br.com.williancarbone.model.objetos.Empresa
import br.com.williancarbone.model.objetos.Match
import groovy.sql.GroovyRowResult
import groovy.sql.Sql

class EmpresaDao extends BaseDao implements PerfilDao<Empresa,String>, Matchable, MatchDao {


    EmpresaDao(Sql sql) {
        super(sql)
    }

    @Override
    Integer criarPerfil(Empresa empresa) {

        if (!empresa) {
            throw new DadoNaoInformado("Dado empresa faltante")
        }

        if(buscarExistenciaCredencial(empresa.cnpj)){
            throw new CredencialDuplicadaException("O cnpj informado ja esta em uso")
        }


        List<List<Object>>  insercao = sql.executeInsert "INSERT INTO empresa (cnpj,pais,empresa_id) VALUES (?,?,?)", [empresa.getCnpj(), empresa.getPais(), empresa.getIdentificador()]
        Integer idRegistrado = insercao[0][2] as Integer

        return idRegistrado


    }

    @Override
    void removerPerfil(String cnpj) {

        if(!cnpj){
            throw new DadoNaoInformado("Dado cnpj não informado")
        }

        sql.withTransaction {

            GroovyRowResult empresa = sql.firstRow("SELECT empresa_id FROM empresa WHERE cnpj = ?", [cnpj])

            if(!empresa){
                throw  new DadoNaoEncontado("não foi encontraa uma empresa com o cnpj informado")
            }

            Integer idParaDeletar = empresa.empresa_id as Integer

            sql.execute "DELETE FROM matchs WHERE empresa = ?", [cnpj]
            sql.execute "DELETE FROM especialidade_vaga WHERE vaga IN (SELECT id FROM vaga WHERE contratante = ?) ", [cnpj]
            sql.execute "DELETE FROM curtida WHERE vaga IN (SELECT id FROM vaga WHERE contratante = ?)", [cnpj]
            sql.execute "DELETE FROM vaga WHERE contratante = ?", [cnpj]
            sql.execute "DELETE FROM empresa WHERE empresa_id = ?", [idParaDeletar]
            sql.execute "DELETE FROM especialidade_usuario WHERE usuario= ?", [idParaDeletar]
            sql.execute "DELETE FROM usuario WHERE id = ?", [idParaDeletar]
        }


    }

    @Override
    void editarPerfil(Integer id, String novoPais) {

        if (!id) {
            throw new DadoNaoInformado("Dado identificador ausente")
        }

        if(!novoPais){
            throw new DadoNaoInformado("novo pais nao informada")
        }

        sql.execute "UPDATE empresa SET pais=${novoPais} WHERE empresa_id=${id}"


    }

    @Override
    Map capturarInformacoesPerfil(String cnpj) {

        if (!cnpj) {
            throw new DadoNaoInformado("dado cnpj ausente")
        }

        String busca = """
                SELECT u.id,u.nome,u.email,u.cep,u.estado,u.descricao,emp.pais,emp.cnpj, STRING_AGG(eu.especialidade, ', ')  AS competencias
                FROM usuario as u
                JOIN empresa as emp ON u.id=emp.empresa_id
                LEFT JOIN especialidade_usuario as eu ON u.id=eu.usuario
                WHERE emp.cnpj=?
                GROUP BY u.id,u.nome, u.email, u.cep, u.estado, u.descricao, emp.pais, emp.cnpj
        """

        GroovyRowResult infos = sql.firstRow(busca, [cnpj])


        return infos
    }



    @Override
    Integer capturarId(String cnpj) {

        if (!cnpj) {
            throw new DadoNaoInformado("dado cnpj não informado")
        }


        GroovyRowResult linhaUsuario = sql.firstRow("SELECT empresa_id AS id FROM empresa WHERE cnpj=?", [cnpj])

        if (!linhaUsuario) {
            throw new DadoNaoEncontado("Nao ha empresa com tal cnpj")
        }

        Integer idUsuario = linhaUsuario.id as Integer
        return idUsuario
    }


    @Override
    List<Map> buscarVagas(String cnpj) {
        if (!cnpj) {
            throw new DadoNaoInformado("dado cnpj não informado")
        }

        String sqlBusca = """
        SELECT 
            v.id AS id_vaga, 
            v.nome AS nome_vaga, 
            v.descricao AS descricao_vaga,
            c.candidato_id AS id_candidato,
            STRING_AGG(e.nome, ', ') AS competencias_candidato
        FROM vaga v
        LEFT JOIN curtida cur ON v.id = cur.vaga
        LEFT JOIN candidato c ON cur.candidato = c.cpf
        LEFT JOIN especialidade_usuario eu ON c.candidato_id = eu.usuario
        LEFT JOIN especialidade e ON eu.especialidade = e.sigla
        WHERE v.contratante = ?
        GROUP BY v.id, v.nome, v.descricao, c.candidato_id, c.cpf
        ORDER BY v.id
        """
        List<Map> infos = sql.rows(sqlBusca, [cnpj])

        List<LinkedHashMap> vagasAgrupadas = infos.groupBy { it.id_vaga }.collect { idVaga, registros ->
            Map primeiraLinha = registros[0]
            return [
                    id_vaga       : idVaga,
                    nome_vaga     : primeiraLinha.nome_vaga,
                    descricao_vaga: primeiraLinha.descricao_vaga,

                    interessados  : registros.collect {
                        it.id_candidato ? [id: it.id_candidato, habilidades: it.competencias_candidato] : null
                    }.findAll { it != null }
            ]
        }

        return vagasAgrupadas
    }


    @Override
    List<Map> buscarMatchs(String cnpj) {
        if (!cnpj) {
            throw new DadoNaoInformado("dado cnpj não informado")
        }

        String buscaMatchs = """
                SELECT u.nome, u.descricao, u.email, c.idade, u.estado, m.vaga
                FROM usuario AS u
                JOIN candidato AS c ON u.id = c.candidato_id
                JOIN matchs AS m ON m.candidato = c.cpf
                WHERE m.empresa = ?
        """

        List<Map> matchsEncontrados = sql.rows(buscaMatchs, [cnpj])


        return matchsEncontrados
    }



    @Override
    Integer criarMatch(Match match) {
       if(!match){
           throw new DadoNaoInformado("Dados sobre o match ao informados")
       }

        List<List<Object>>  insercao = sql.executeInsert "INSERT INTO matchs (empresa,candidato,vaga) VALUES (?,?,?)", [match.cnpj, match.cpf, match.idVaga]

        Integer idMatch = insercao[0][0] as Integer


        return idMatch

    }

    @Override
    Boolean buscarExistenciaCredencial (String cnpj){
        return sql.firstRow("SELECT 1 FROM empresa WHERE cnpj = ?", [cnpj]) ? true : false
    }
}
