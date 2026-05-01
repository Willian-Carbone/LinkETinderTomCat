package br.com.williancarbone.dao

import br.com.williancarbone.dao.bases.BaseDao
import br.com.williancarbone.exceptions.DadoNaoEncontado
import br.com.williancarbone.exceptions.DadoNaoInformado
import br.com.williancarbone.model.objetos.Vaga
import groovy.sql.Sql

class VagaDao extends BaseDao{

    VagaDao(Sql sql) {
        super(sql)
    }

    Integer gravarVaga(Vaga vaga) {

        if(!vaga){
            throw new DadoNaoInformado("Dado sobre a vaga não informado")
        }

        Integer idVaga = null


        sql.withTransaction {

            List<List<Object>> insercao = sql.executeInsert "INSERT INTO vaga (nome,descricao,contratante) VALUES (?,?,?)", [vaga.getNome(), vaga.getDescricao(), vaga.getContratante()]
              idVaga = insercao[0][0] as Integer

            vaga.requisitos.forEach {
                sql.executeInsert("INSERT INTO especialidade_vaga (vaga,especialidade) VALUES (?,?)", [idVaga, it.name()])}


        }

        return idVaga





    }

    void trocarNomeDaVaga(Integer idVaga, String novoNome) {

        if(!idVaga){
            throw new DadoNaoInformado("ID da vaga não informado")
        }

        if(!novoNome){
            throw new DadoNaoInformado("Novo nome da vaga não fornecido")
        }

       Integer linhasAfetadas = sql.executeUpdate( "UPDATE vaga SET nome=? WHERE id=?", [novoNome, idVaga])
        if(linhasAfetadas==0){
            throw new DadoNaoEncontado("Não foi encontrada uma vaga para o id informado")
        }
    }

    void trocarDescricaoDaVaga(Integer idVaga, String novaDescricao) {

        if(!idVaga){
            throw new DadoNaoInformado("ID da vaga não informado")
        }

        if(!novaDescricao){
            throw new DadoNaoInformado("Nova descricao  da vaga não fornecida")
        }

        Integer linhasAfetadas = sql.executeUpdate( "UPDATE vaga SET descricao=? WHERE id=?", [novaDescricao, idVaga])

        if(linhasAfetadas==0){
            throw new DadoNaoEncontado("Não foi encontrada uma vaga para o id informado")
        }
    }

    void removerVaga(Integer idVaga){



        if(!idVaga){
            throw new DadoNaoInformado("Dado idVaga não informado")
        }

        sql.withTransaction {

            sql.execute("DELETE FROM especialidade_vaga  WHERE vaga=?", [idVaga])
            Integer vagasAfetadas = sql.executeUpdate("DELETE FROM vaga WHERE id=?", [idVaga])

            if(vagasAfetadas==0){
                throw  new DadoNaoEncontado("Não foram encontradas vagas com o id especificado")

            }
        }



    }


    List<String> buscarCpfsInteressadosSemMatch(Integer vagaId, String cnpj) {

        if(!vagaId){
            throw new DadoNaoInformado("Id da vaga não informado")

        }

        if(!cnpj){
            throw new DadoNaoInformado("Dado referente ao cnpj não informado")
        }


        if (!sql.firstRow("SELECT 1 FROM vaga WHERE id = ?", [vagaId])) {
            throw new DadoNaoEncontado("Não existe vaga com o ID ${vagaId}")
        }


        List<String> cpfCandidatosInteressados= sql.rows("""
        SELECT c.candidato 
        FROM curtida AS c 
        WHERE c.vaga = ? 
        AND NOT EXISTS (
            SELECT 1 FROM matchs AS m 
            WHERE m.candidato = c.candidato AND m.empresa = ?
        )
    """, [vagaId, cnpj]).collect { it.candidato as String }


        return cpfCandidatosInteressados
    }








}
