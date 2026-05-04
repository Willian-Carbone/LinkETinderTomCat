package br.com.williancarbone.service

import br.com.williancarbone.ValidadoresEntrada.facades.ValidadorRegistroVaga
import br.com.williancarbone.dao.CandidatoDao
import br.com.williancarbone.dao.EmpresaDao
import br.com.williancarbone.dao.VagaDao
import br.com.williancarbone.exceptions.DadoNaoEncontado
import br.com.williancarbone.exceptions.DadoNaoInformado
import br.com.williancarbone.infra.conexoes.CriadorConexao
import br.com.williancarbone.model.enuns.Especialidade
import br.com.williancarbone.model.objetos.Vaga
import br.com.williancarbone.util.TextConversorUtil

class VagaService {

    VagaDao vagaDao
    CandidatoDao candidatoDao
    EmpresaDao empresaDao

    VagaService(CriadorConexao criadorConexao){
        vagaDao = new VagaDao(criadorConexao.criarConexao())
        empresaDao = new EmpresaDao(criadorConexao.criarConexao())
        candidatoDao = new CandidatoDao(criadorConexao.criarConexao())

    }

    Integer CriarVaga(Map info){
        ValidadorRegistroVaga validador=new ValidadorRegistroVaga()

        validador.validarDadosParaRegistroVaga(info)

        String nome=info.nome
        String descricao= info.descricao
        String contratante = TextConversorUtil.removerNaoDigitos(info.contratante as String)

        ArrayList<Especialidade> especialidades = info.requisitos.collect { Especialidade.localizarEnum(it as String) }

        Vaga vaga = new Vaga (nome,descricao,contratante,especialidades)

        return vagaDao.gravarVaga(vaga)



    }

    void deletarVaga(String cnpj, Integer idVaga){
        if(!cnpj){throw new DadoNaoInformado("cnpj nao informado")}
        if(!idVaga){throw new DadoNaoInformado("Id da vaga não informado")}

        boolean remocao=false


        List vagasDaEmpresa = empresaDao.buscarVagas(TextConversorUtil.removerNaoDigitos(cnpj))

        vagasDaEmpresa.each {
            if(it.id_vaga ==idVaga) {
                vagaDao.removerVaga(it.id_vaga as Integer)
                remocao = true
            }

        }

        if(!remocao){throw new DadoNaoEncontado("A empresa não possui uma vaga com id informado")}


    }

    List<Map> capturarInteressadosEmVaga(Map info){

        if (!info.cnpj) { throw new DadoNaoInformado("CNPJ não informado") }
        if (!info.idVaga) { throw new DadoNaoInformado("Id da vaga não informado") }

        List<String> cpfsDosInteressados = vagaDao.buscarCpfsInteressadosSemMatch(info.idVaga as Integer, info.cnpj as String)

        return cpfsDosInteressados.collect { cpf ->
            Map dadosSensiveis = candidatoDao.capturarInformacoesPerfil(cpf)

            if (dadosSensiveis) {
                return [
                        id          : dadosSensiveis.id,
                        competencias: dadosSensiveis.competencias
                ]
            }
            return [:]
        }.findAll { !it.isEmpty() }

    }








}
