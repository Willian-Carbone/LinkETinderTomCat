package br.com.williancarbone.service

import br.com.williancarbone.ValidadoresEntrada.facades.ValidadorCandidatoRegistroFacade
import br.com.williancarbone.dao.CandidatoDao
import br.com.williancarbone.exceptions.DadoNaoEncontado
import br.com.williancarbone.exceptions.DadoNaoInformado
import br.com.williancarbone.infra.conexoes.CriadorConexao
import br.com.williancarbone.model.enuns.Especialidade
import br.com.williancarbone.model.enuns.Estado
import br.com.williancarbone.model.objetos.Candidato
import br.com.williancarbone.model.objetos.Curtida
import br.com.williancarbone.model.objetos.EspecialidadeUsuario
import br.com.williancarbone.service.Base.ServicePerfilBase


import br.com.williancarbone.util.TextConversorUtil


class CandidatoService extends ServicePerfilBase {

    CandidatoDao candidatoDao

    ValidadorCandidatoRegistroFacade validadorDados = new ValidadorCandidatoRegistroFacade()

    CandidatoService(CriadorConexao criadorConexao){
        super(criadorConexao)
        candidatoDao= new CandidatoDao(criadorConexao.criarConexao())


    }


    Integer criarPerfil(Map info) {

        validadorDados.validarDadosParaRegistroCandidato(info)

        String nome=info.nome
        String email=info.email
        String cep = TextConversorUtil.removerNaoDigitos(info.cep as String)
        Estado estado = Estado.values().find {it.name()==TextConversorUtil.converterParaNomeEnum(info.estado as String)}
        String descricao = info.descricao
        Integer idade = info.idade as Integer
        String cpf = TextConversorUtil.removerNaoDigitos(info.cpf as String)

        ArrayList<Especialidade> especialidades = info.especialidades.collect { Especialidade.localizarEnum(it as String) }

        Candidato candidato = new Candidato(nome, cpf ,idade,email,cep,estado,descricao,especialidades)

        Integer idGerado= usuarioDao.salvarUsuario(candidato)

        candidato.identificador=idGerado

        especialidades.each {
            EspecialidadeUsuario especialidadeUsuario = new EspecialidadeUsuario(idGerado,it)
            usuarioDao.salvarEspecialidadeUsuario(especialidadeUsuario)

        }

        candidatoDao.criarPerfil(candidato)

    }



    void removerPerfil(String cpf) {

        candidatoDao.removerPerfil(TextConversorUtil.removerNaoDigitos(cpf))

    }



    Map capturarInfosDoPerfil(String cpf) {

        if(!cpf){throw new DadoNaoInformado("Cpf para busca não informado")}

        Map infos= candidatoDao.capturarInformacoesPerfil(TextConversorUtil.removerNaoDigitos(cpf))

        return [id:infos.id,competencias:infos.competencias]

    }


    Integer capturarIdPerfil(String cpf) {

        if(!cpf){throw new DadoNaoInformado("Cpf para busca não informado")}

        candidatoDao.capturarId(TextConversorUtil.removerNaoDigitos(cpf))

    }


    List<Map> buscarVagasParaOPerfil(String cpf) {
        if(!cpf){throw new DadoNaoInformado("Cpf para busca não informado")}

        candidatoDao.buscarVagas(TextConversorUtil.removerNaoDigitos(cpf))
    }


    List<Map> buscarMatchsParaOPerfil(String cpf) {
        if(!cpf){throw new DadoNaoInformado("Cpf para busca não informado")}

        candidatoDao.buscarMatchs(TextConversorUtil.removerNaoDigitos(cpf ))
    }


    Integer salvarCurtida(Map curtidaInfo) {
        if(!curtidaInfo.idVaga|| !curtidaInfo.cpf){throw new DadoNaoInformado("Dados Ausentes , insira um cpf e o iD da vaga")}

        String cpfFormatado= TextConversorUtil.removerNaoDigitos(curtidaInfo.cpf as String)

        if(!candidatoDao.buscarExistenciaCredencial(cpfFormatado)){throw new DadoNaoEncontado("Cpf informado não possui registro")}

         Curtida curtida=new Curtida(curtidaInfo)

         candidatoDao.salvarCurtida(curtida)



    }
}
