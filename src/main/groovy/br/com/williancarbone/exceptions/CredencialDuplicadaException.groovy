package br.com.williancarbone.exceptions

import br.com.williancarbone.exceptions.base.ExcessaoPersonalizada

class CredencialDuplicadaException extends ExcessaoPersonalizada{
    CredencialDuplicadaException(String mensagem) {
        super(mensagem)
    }
}
