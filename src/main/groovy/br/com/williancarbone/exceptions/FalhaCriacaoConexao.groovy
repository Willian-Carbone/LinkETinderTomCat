package br.com.williancarbone.exceptions

import br.com.williancarbone.exceptions.base.ExcessaoPersonalizada

class FalhaCriacaoConexao extends ExcessaoPersonalizada{
    FalhaCriacaoConexao(String mensagem) {
        super(mensagem)
    }
}
