package br.com.williancarbone.exceptions

import br.com.williancarbone.exceptions.base.ExcessaoPersonalizada

class DadoNaoEncontado extends ExcessaoPersonalizada{
    DadoNaoEncontado(String mensagem) {
        super(mensagem)
    }
}
