package br.com.williancarbone.exceptions

import br.com.williancarbone.exceptions.base.ExcessaoPersonalizada

class DadoInvalido extends ExcessaoPersonalizada{
    DadoInvalido(String mensagem) {
        super(mensagem)
    }
}
