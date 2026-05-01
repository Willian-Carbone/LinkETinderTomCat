package br.com.williancarbone.exceptions

import br.com.williancarbone.exceptions.base.ExcessaoPersonalizada

class DadoNaoInformado extends ExcessaoPersonalizada{
    DadoNaoInformado(String mensagem) {
        super(mensagem)
    }
}
