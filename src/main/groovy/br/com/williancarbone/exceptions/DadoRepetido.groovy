package br.com.williancarbone.exceptions

import br.com.williancarbone.exceptions.base.ExcessaoPersonalizada

class DadoRepetido extends ExcessaoPersonalizada{

    DadoRepetido(String mensagem) {
        super(mensagem)
    }
}
