package br.com.williancarbone.controler


import br.com.williancarbone.exceptions.DadoInvalido
import br.com.williancarbone.exceptions.DadoNaoEncontado
import br.com.williancarbone.exceptions.DadoNaoInformado
import br.com.williancarbone.exceptions.FalhaCriacaoConexao
import br.com.williancarbone.infra.FeedbackBuilder
import br.com.williancarbone.infra.conexoes.ConexaoPostGresBase
import br.com.williancarbone.service.VagaService
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

import java.sql.SQLException

@WebServlet("/vagas")

class VagaControler extends HttpServlet implements FeedbackBuilder{

    private final ObjectMapper mapper = new ObjectMapper()
    private final VagaService vagaService = new VagaService( new ConexaoPostGresBase())


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json")
        resp.setCharacterEncoding("UTF-8")

        try {

            Map<String, Object> mapaDosDados = mapper.readValue(req.getInputStream(), Map.class)

            vagaService.CriarVaga(mapaDosDados)
            resp.setStatus(HttpServletResponse.SC_CREATED)
            Map<String, Object> okBody = [mensagem: "Vaga criada com sucesso"]
            mapper.writeValue(resp.getWriter(), okBody)

        } catch (DadoNaoInformado | DadoInvalido e) {

            enviarErro(resp, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST)

        }

        catch (FalhaCriacaoConexao e){

            enviarErro(resp, e.getMessage(), HttpServletResponse.SC_SERVICE_UNAVAILABLE)

        }


        catch (SQLException ignored) {
            enviarErro(resp, "Erro interno no servidor", HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
        }
    }



    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json")
        resp.setCharacterEncoding("UTF-8")

        try{

            String cnpj=req.getParameter("cnpj")

            String vaga= req.getParameter("vaga")

            if(!cnpj  || !vaga ){
                throw new DadoNaoInformado("Para remover uma vaga  , informe  o cnpj da empresa e o identificador da vaga na url ")
            }

            vagaService.deletarVaga(cnpj, vaga.toInteger())

            resp.setStatus(HttpServletResponse.SC_OK)
            Map<String, Object> okBody = [mensagem: "Vaga excluida com sucesso"]
            mapper.writeValue(resp.getWriter(), okBody)


        }
        catch (DadoNaoInformado e){

            enviarErro(resp, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST)

        }

        catch (DadoNaoEncontado e){
            enviarErro(resp, e.getMessage(), HttpServletResponse.SC_NOT_FOUND)

        }

        catch (NumberFormatException ignored){
            enviarErro(resp, "Insira um valor numerico ao id da vaga", HttpServletResponse.SC_BAD_REQUEST)
        }


    }



}
