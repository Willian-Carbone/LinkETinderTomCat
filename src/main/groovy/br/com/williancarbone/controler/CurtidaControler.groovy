package br.com.williancarbone.controler

import br.com.williancarbone.exceptions.DadoNaoEncontado
import br.com.williancarbone.exceptions.DadoNaoInformado
import br.com.williancarbone.exceptions.DadoRepetido
import br.com.williancarbone.exceptions.FalhaCriacaoConexao
import br.com.williancarbone.infra.FeedbackBuilder
import br.com.williancarbone.infra.conexoes.ConexaoPostGresBase
import br.com.williancarbone.service.CandidatoService
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

import java.sql.SQLException


@WebServlet("/curtida")

class CurtidaControler extends HttpServlet implements FeedbackBuilder{


    private final ObjectMapper mapper = new ObjectMapper()
    private final CandidatoService candidatoService = new CandidatoService( new ConexaoPostGresBase())

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json")
        resp.setCharacterEncoding("UTF-8")

        try {

            Map<String, Object> mapaDosDados = mapper.readValue(req.getInputStream(), Map.class)

            candidatoService.salvarCurtida(mapaDosDados)

            resp.setStatus(HttpServletResponse.SC_CREATED)
            Map<String, Object> okBody = [mensagem: "Curtida registrada"]
            mapper.writeValue(resp.getWriter(), okBody)

        } catch (DadoNaoInformado e) {

            enviarErro(resp, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST)

        }

        catch (DadoNaoEncontado e){
            enviarErro(resp,e.getMessage(),HttpServletResponse.SC_NOT_FOUND)
        }

        catch (FalhaCriacaoConexao e){

            enviarErro(resp, e.getMessage(), HttpServletResponse.SC_SERVICE_UNAVAILABLE)

        }

        catch (DadoRepetido e){
            enviarErro(resp, e.getMessage(),HttpServletResponse.SC_CONFLICT)
        }


        catch (SQLException ignored) {
            enviarErro(resp, "Erro interno no servidor", HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
        }
    }



}
