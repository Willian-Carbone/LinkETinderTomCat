package br.com.williancarbone.infra

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletResponse

trait FeedbackBuilder {


    void enviarErro(HttpServletResponse resp, String mensagem, int status) {
        ObjectMapper mapper = new ObjectMapper()
        resp.setStatus(status)
        Map<String, Object> erroBody = [
                mensagem : mensagem,
                status   : status,
                timestamp: System.currentTimeMillis()
        ]
        mapper.writeValue(resp.getWriter(), erroBody)
    }


}