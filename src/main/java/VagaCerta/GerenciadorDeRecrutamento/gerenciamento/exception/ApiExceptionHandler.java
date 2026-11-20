package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.exception;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.dto.ApiErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.OffsetDateTime;

/* @RestControllerAdvice:
Restringe o tratamento às classes do pacote REST.
As respostas JSON não interferem nas páginas Thymeleaf.
 */

@RestControllerAdvice(
        basePackages = "VagaCerta.GerenciadorDeRecrutamento.gerenciamento.controller.rest"
)
public class ApiExceptionHandler {

    // Trata argumentos inválidos.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> tratarArgumentoInvalido(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request
        );
    }

    // Trata operações incompatíveis com o estado atual do sistema.
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> tratarEstadoInvalido(
            IllegalStateException ex,
            HttpServletRequest request) {

        return criarResposta(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request
        );
    }

    // Trata JSON inválido ou malformado.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> tratarJsonInvalido(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "O corpo da requisição está inválido ou malformado.",
                request
        );
    }

    // Trata parâmetros com tipos incompatíveis.
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> tratarTipoInvalido(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "O parâmetro '" + ex.getName() +
                        "' possui um valor ou tipo inválido.",
                request
        );
    }

    // Trata parâmetros obrigatórios ausentes.
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> tratarParametroAusente(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "O parâmetro obrigatório '" +
                        ex.getParameterName() + "' não foi informado.",
                request
        );
    }

    // Trata violações de integridade do banco de dados.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> tratarIntegridade(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        return criarResposta(
                HttpStatus.CONFLICT,
                "A operação viola uma restrição de integridade dos dados.",
                request
        );
    }

    // Trata erros inesperados.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> tratarErroInterno(
            Exception ex,
            HttpServletRequest request) {

        return criarResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno. Tente novamente mais tarde.",
                request
        );
    }

    // Constrói a resposta padronizada.
    private ResponseEntity<ApiErrorResponse> criarResposta(
            HttpStatus status,
            String mensagem,
            HttpServletRequest request) {

        ApiErrorResponse resposta = new ApiErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(resposta);
    }
}
