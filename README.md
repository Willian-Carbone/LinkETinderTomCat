
##  Autor

*   **Willian Carbone** - [GitHub Profile](https://github.com/Willian-Carbone)


# Link Tinder API

![Status](https://img.shields.io/badge/Status-Em_Desenvolvimento-blue)
![Java](https://img.shields.io/badge/Java-17%2B-orange)
![Groovy](https://img.shields.io/badge/Groovy-4.0-green)
![Tomcat](https://img.shields.io/badge/Server-Tomcat_11-red)

 O projeto simula o ecossistema de recrutamento, permitindo que candidatos e empresas gerenciem seus perfis e vagas 

A aplicação foi estruturada seguindo padrões  de desenvolvimento :

*   **Camada de Controle (Servlets):** Gerencia as rotas HTTP, valida parâmetros de entrada e coordena as respostas JSON.
*   **Camada de Serviço (Business Logic):** Centraliza as regras de negócio e orquestra as operações.
*   **Camada de Infraestrutura (Persistência):** Gerencia conexões JDBC com PostgreSQL via `ConexaoPostGresBase`.
*   **Feedback & Erros:** Uso de **Traits** (`FeedbackBuilder`) para padronizar as respostas de erro em toda a aplicação.

---

## Referência da API

###  Candidatos
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/candidatos` | Lista candidatos . |
| `POST` | `/candidatos` | Cria um novo perfil de candidato. |

### Empresas
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `POST` | `/empresas` | Cadastra uma nova empresa no sistema. |
| `DELETE` | `/empresas?cnpj=...` | Remove uma empresa permanentemente via CNPJ. |

### Vagas
| Método | Endpoint                             | Descrição |
| :--- |:-------------------------------------| :--- |
| `POST` | `/vagas`                             | Publica uma nova oportunidade de emprego. |
| `DELETE` | `/vagas?cnpj=...&vaga=...`           | Remove uma vaga específica de uma empresa. |
|`GET`|  `/vagas?credenciais= ...cpf/cnpj...`| lista vagas para o perfil selecionado|


### Curtida
| Método | Endpoint                             | Descrição                                  |
| :--- |:-------------------------------------|:-------------------------------------------|
| `POST` | `/curtida`                           | Publica uma nova curtida em vaga.          |


---

## Tratamento de Erros Padronizado

A API utiliza códigos de status HTTP semânticos

| Status | Código HTTP | Descrição |
| :--- | :--- | :--- |
| `400` | **Bad Request** | Dados ausentes ou formato de número inválido. |
| `404` | **Not Found** | Recurso (Empresa/Vaga) não localizado. |
| `409` | **Conflict** | Tentativa de duplicar um registro . |
| `422` | **Unprocessable Content** | Violação de regra de negócio (ex: Credencial duplicada). |
| `503` | **Service Unavailable** | Banco de dados offline ou erro de conexão. |


**Exemplo de Resposta de Erro:**
```json
{
  "mensagem": "Para remover uma empresa, informe seu cnpj na url",
  "status": 400,
  "timestamp": 1714598400000
}
