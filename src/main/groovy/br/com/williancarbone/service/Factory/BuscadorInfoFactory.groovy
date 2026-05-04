package br.com.williancarbone.service.Factory


import br.com.williancarbone.infra.conexoes.CriadorConexao
import br.com.williancarbone.service.Base.BuscadorInfo
import br.com.williancarbone.service.Base.ServicePerfilBase
import br.com.williancarbone.service.CandidatoService
import br.com.williancarbone.service.EmpresaService
import groovy.transform.Canonical


@Canonical

class BuscadorInfoFactory {

    CriadorConexao conector


    BuscadorInfo FabricarServico(String credencial){

        credencial.size()<=11? new CandidatoService(conector) : new EmpresaService(conector)

    }








}
