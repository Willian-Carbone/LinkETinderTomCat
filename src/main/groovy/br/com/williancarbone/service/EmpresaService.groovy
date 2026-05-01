package br.com.williancarbone.service


import br.com.williancarbone.ValidadoresEntrada.facades.ValidadorEmpresaRegistroFacade
import br.com.williancarbone.dao.CandidatoDao
import br.com.williancarbone.dao.EmpresaDao
import br.com.williancarbone.exceptions.DadoNaoEncontado
import br.com.williancarbone.exceptions.DadoNaoInformado
import br.com.williancarbone.infra.conexoes.CriadorConexao
import br.com.williancarbone.model.enuns.Especialidade
import br.com.williancarbone.model.enuns.Estado
import br.com.williancarbone.model.objetos.Empresa
import br.com.williancarbone.model.objetos.EspecialidadeUsuario
import br.com.williancarbone.model.objetos.Match
import br.com.williancarbone.service.Base.ServicePerfilBase
import br.com.williancarbone.util.TextConversorUtil


class EmpresaService extends ServicePerfilBase{

    EmpresaDao empresaDao
    CandidatoDao candidatoDao

    ValidadorEmpresaRegistroFacade validadorDados = new ValidadorEmpresaRegistroFacade()

    EmpresaService(CriadorConexao criadorConexao){
        super(criadorConexao)
        empresaDao= new EmpresaDao(criadorConexao.criarConexao())
        candidatoDao = new CandidatoDao(criadorConexao.criarConexao())


    }



    Integer criarPerfil(Map info) {

        validadorDados.validarDadosParaRegistroEmpresa(info)

        String nome=info.nome
        String email=info.email
        String cep = TextConversorUtil.removerNaoDigitos(info.cep as String)
        Estado estado = Estado.values().find {it.name()==TextConversorUtil.converterParaNomeEnum(info.estado as String)}
        String descricao = info.descricao
        String pais = info.pais
        String cnpj= TextConversorUtil.removerNaoDigitos(info.cnpj as String)

        ArrayList<Especialidade> especialidades = info.especialidades.collect { Especialidade.localizarEnum(it as String) }

        Empresa empresa = new Empresa(nome, cnpj ,pais,cep,email,estado,descricao,especialidades)

        Integer idGerado= usuarioDao.salvarUsuario(empresa)

        empresa.identificador=idGerado

        especialidades.each {
            EspecialidadeUsuario especialidadeUsuario = new EspecialidadeUsuario(idGerado,it)
            usuarioDao.salvarEspecialidadeUsuario(especialidadeUsuario)

        }

        empresaDao.criarPerfil(empresa)

    }



    void removerPerfil(String cnpj) {

        if(!cnpj){throw new DadoNaoInformado("Não foi informado um cnpj para remoção")}
        if(!empresaDao.buscarExistenciaCredencial(cnpj as String)){throw new DadoNaoEncontado("O cnpj informado não esta cadastrado ")}

        empresaDao.removerPerfil(TextConversorUtil.removerNaoDigitos(cnpj))

    }




    Map capturarInfosDoPerfil(String cnpj) {

        if(!cnpj){throw new DadoNaoInformado("cnpj para busca não informado")}

        Map iformacoes = empresaDao.capturarInformacoesPerfil(TextConversorUtil.removerNaoDigitos(cnpj))

        return [id:iformacoes.id,competencias:iformacoes.competencias]



    }


    Integer capturarIdPerfil(String cnpj) {

        if(!cnpj){throw new DadoNaoInformado("Cnpj para busca não informado")}

        empresaDao.capturarId(TextConversorUtil.removerNaoDigitos(cnpj))

    }


    List<Map> buscarVagasParaOPerfil(String cnpj) {
        if(!cnpj){throw new DadoNaoInformado("cnpj para busca não informado")}

        empresaDao.buscarVagas(TextConversorUtil.removerNaoDigitos(cnpj))
    }


    List<Map> buscarMatchsParaOPerfil(String cnpj) {
        if(!cnpj){throw new DadoNaoInformado("cnpj para busca não informado")}

        empresaDao.buscarMatchs(TextConversorUtil.removerNaoDigitos(cnpj))
    }


    Integer realizarMatch(Map matchInfo) {
        if(!matchInfo.idVaga|| !matchInfo.idCandidato || !matchInfo.cnpj){throw new DadoNaoInformado("Dados Ausentes , insira um cnpj , id vaga e id do candidato")}


        String cnpjFormatado = TextConversorUtil.removerNaoDigitos(matchInfo.cnpj as String)

        if(!empresaDao.buscarExistenciaCredencial(cnpjFormatado))
        {throw new DadoNaoEncontado("Cnpj informado não possui registro")}



            String cpf = candidatoDao.capturarCpfPorId(matchInfo.idCandidato as Integer)

            if(!cpf){throw  new DadoNaoEncontado("Não a candidatos com o id informado ")}

            Match match = new Match(cpf,cnpjFormatado,matchInfo.idVaga as Integer)
            empresaDao.criarMatch(match)




    }
}
