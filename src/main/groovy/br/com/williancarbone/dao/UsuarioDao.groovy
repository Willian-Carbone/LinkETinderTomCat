package br.com.williancarbone.dao

import br.com.williancarbone.dao.bases.BaseDao
import br.com.williancarbone.exceptions.CredencialDuplicadaException
import br.com.williancarbone.exceptions.DadoNaoInformado
import br.com.williancarbone.model.enuns.Estado
import br.com.williancarbone.model.objetos.EspecialidadeUsuario
import br.com.williancarbone.model.objetos.Usuario
import groovy.sql.GroovyRowResult
import groovy.sql.Sql


class UsuarioDao extends BaseDao{

    UsuarioDao(Sql sql){
        super(sql)
    }

    Integer salvarUsuario(Usuario usuario) {

            if (!usuario){
                throw new DadoNaoInformado("Dado usuario não informado")
            }

            if(buscarExistenciaEmail(usuario.email))
            {throw new CredencialDuplicadaException("O email informado ja foi registrado")}




        List<List<Object>>  insercao = sql.executeInsert "INSERT INTO usuario (nome, email, cep, estado,descricao) VALUES (${usuario.getNome()},${usuario.getEmail()},${usuario.getCep()},${usuario.estado.sigla},${usuario.getDescricao()})"
            Integer idGerado = insercao[0][0] as Integer

            return idGerado




    }

    Integer salvarEspecialidadeUsuario (EspecialidadeUsuario especialidadeusuario){

            if(!especialidadeusuario)

            {throw new DadoNaoInformado("Especialidade não informada")}

        List<List<Object>>  insercao = sql.executeInsert "INSERT INTO especialidade_usuario (especialidade,usuario) VALUES (${especialidadeusuario.especialidade.name()},${especialidadeusuario.idUsuario})"
            Integer idGerado= insercao[0][0] as Integer
            return idGerado


    }


    void editarCepUsuario (Integer idUsuario, String novoCep){
        if(!idUsuario )
        {throw new DadoNaoInformado("Dado id usuario Ausente")}

        if (!novoCep){
            throw new DadoNaoInformado("Dado da edição nao informado")
        }

        sql.execute "UPDATE usuario SET cep=? WHERE id=?", [novoCep, idUsuario]



    }

    void editarDescricaoUsuario(Integer idUsuario, String novaDescricao) {

        if(!idUsuario )
        {throw new DadoNaoInformado("Dado id usuario Ausente")}

        if (!novaDescricao){
            throw new DadoNaoInformado("Dado da edição nao informado")
        }
        sql.execute "UPDATE usuario SET descricao=${novaDescricao} WHERE id=${idUsuario}"



    }


    void editarEmailUsuario(Integer idUsuario,String novoEmail) {

        if(!idUsuario )
        {throw new DadoNaoInformado("Dado id usuario Ausente")}

        if (!novoEmail){
            throw new DadoNaoInformado("Dado da edição nao informado")
        }

        sql.execute "UPDATE usuario SET email=${novoEmail} WHERE id=${idUsuario}"


    }


    void editarEstado(Integer idUsuario, Estado novoEstado) {

        if(!idUsuario )
        {throw new DadoNaoInformado("Dado id usuario Ausente")}

        if (!novoEstado){
            throw new DadoNaoInformado("Dado da edição nao informado")
        }

        sql.execute "UPDATE usuario SET estado=${novoEstado.sigla} WHERE id=${idUsuario}"






    }


    void editarNomeUsuario(Integer idUsuario, String novoNome) {

        if(!idUsuario )
        {throw new DadoNaoInformado("Dado id usuario Ausente")}

        if (!novoNome){
            throw new DadoNaoInformado("Dado da edição nao informado")
        }


        sql.execute "UPDATE usuario SET nome=${novoNome} WHERE id=${idUsuario}"



    }

    void editarEspecialidadeDoUsuario(Integer idUsuario, List<EspecialidadeUsuario> especialidades) {

        if(!idUsuario )
        {throw new DadoNaoInformado("Dado id usuario Ausente")}

        if (!especialidades){
            throw new DadoNaoInformado("Dado da edição nao informado")
        }
            sql.withTransaction {



                sql.execute "DELETE FROM especialidade_usuario WHERE usuario = ${idUsuario}"

                especialidades.forEach { especialidade ->
                    sql.executeInsert "INSERT INTO especialidade_usuario (especialidade,usuario) VALUES (${especialidade.especialidade.name()},${especialidade.idUsuario})"

                }
            }



    }



    List<Map> buscarPorHabilidades(List<EspecialidadeUsuario> habilidadesBusca, String tipoUsuario) {

        if(!habilidadesBusca )
        {throw new DadoNaoInformado("Dado habilidaes busca Ausente")}

        if (!tipoUsuario){
            throw new DadoNaoInformado("Dado tipo usuario nao informado")
        }

        List<String> habilidadesFormatadas = habilidadesBusca.collect { it.toString() }
        String placeholders = habilidadesFormatadas.collect { '?' }.join(',')
        Integer total = habilidadesFormatadas.size()

        String tabelaFiltro = tipoUsuario.equalsIgnoreCase("candidato") ? "candidato" : "empresa"
        String fkFiltro = tipoUsuario.equalsIgnoreCase("candidato") ? "candidato_id" : "empresa_id"

        String sqlConsulta = """
        SELECT u.id,  STRING_AGG(DISTINCT eu.especialidade, ', ') AS habilidades
        FROM usuario u
        JOIN especialidade_usuario eu ON u.id = eu.usuario
        WHERE EXISTS (SELECT 1 FROM ${tabelaFiltro} WHERE ${fkFiltro} = u.id)
        AND u.id IN (
        SELECT usuario FROM especialidade_usuario 
        WHERE especialidade IN ($placeholders)
        GROUP BY usuario HAVING COUNT(DISTINCT especialidade) = ?)
       GROUP BY u.id
        """

        List<GroovyRowResult> dados =sql.rows(sqlConsulta, habilidadesFormatadas + [total])



        return dados

    }



    List<Map> buscarPorEstado(Estado estado, String tipoUsuario) {


        if(!tipoUsuario )
        {throw new DadoNaoInformado("Dado tipo usuario Ausente")}

        if (!estado){
            throw new DadoNaoInformado("Dado estado nao informado")
        }

        String tabelaFiltro =tipoUsuario.equalsIgnoreCase ("candidato") ? "candidato" : "empresa"
        String fkFiltro = tipoUsuario.equalsIgnoreCase("candidato") ? "candidato_id" : "empresa_id"

        String sqlConsulta = """
        SELECT 
        u.id,
        STRING_AGG(DISTINCT eu.especialidade, ', ') AS habilidades
        FROM usuario u
        INNER JOIN ${tabelaFiltro} perfil ON u.id = perfil.${fkFiltro}
        LEFT JOIN especialidade_usuario eu ON u.id = eu.usuario
        WHERE u.estado = ?
        GROUP BY u.id
         """

        List<GroovyRowResult> dados= sql.rows(sqlConsulta, [estado.getSigla()])



        return dados

    }

    private Boolean  buscarExistenciaEmail(String email) {


        return sql.firstRow("SELECT 1 FROM usuario WHERE email = ? LIMIT 1", [email]) ? true : false

    }




}
