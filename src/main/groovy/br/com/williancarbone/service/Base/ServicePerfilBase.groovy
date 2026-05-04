package br.com.williancarbone.service.Base

import br.com.williancarbone.dao.UsuarioDao
import br.com.williancarbone.infra.conexoes.CriadorConexao


class ServicePerfilBase {


    CriadorConexao criadorConexao
    UsuarioDao usuarioDao


    ServicePerfilBase(CriadorConexao criadorConexao){
        usuarioDao = new UsuarioDao(criadorConexao.criarConexao())
        this.criadorConexao = criadorConexao


    }

}
