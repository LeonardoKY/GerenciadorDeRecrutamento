package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

import java.util.NoSuchElementException;

/*
Essa classe tem o papel de apresentar uma página de erro com uma mensagem compreensível
para o usuário.
 */

@ControllerAdvice(
        basePackages = "VagaCerta.GerenciadorDeRecrutamento.gerenciamento.controller.web"
)
public class WebExceptionHandler {

    // Trata argumentos inválidos.
    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView tratarArgumentoInvalido(
            IllegalArgumentException ex,
            HttpServletRequest request,
            HttpServletResponse response) {

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request,
                response
        );
    }

    // Trata operações incompatíveis com o estado atual (regras de negócio).
    @ExceptionHandler(IllegalStateException.class)
    public ModelAndView tratarEstadoInvalido(
            IllegalStateException ex,
            HttpServletRequest request,
            HttpServletResponse response) {

        return criarResposta(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request,
                response
        );
    }

    // Trata recursos não encontrados.
    @ExceptionHandler(NoSuchElementException.class)
    public ModelAndView tratarNaoEncontrado(
            NoSuchElementException ex,
            HttpServletRequest request,
            HttpServletResponse response) {

        return criarResposta(
                HttpStatus.NOT_FOUND,
                "O recurso solicitado não foi encontrado.",
                request,
                response
        );
    }

    // Trata parâmetros com tipos inválidos, como UUID e enums.
    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            TypeMismatchException.class
    })
    public ModelAndView tratarTipoInvalido(
            Exception ex,
            HttpServletRequest request,
            HttpServletResponse response) {

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "Um dos parâmetros informados possui valor ou tipo inválido.",
                request,
                response
        );
    }

    // Trata parâmetros obrigatórios ausentes.
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ModelAndView tratarParametroAusente(
            MissingServletRequestParameterException ex,
            HttpServletRequest request,
            HttpServletResponse response) {

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "O parâmetro obrigatório '" +
                        ex.getParameterName() +
                        "' não foi informado.",
                request,
                response
        );
    }

    // Trata erros de vinculação dos formulários.
    @ExceptionHandler(BindException.class)
    public ModelAndView tratarVinculacaoInvalida(
            BindException ex,
            HttpServletRequest request,
            HttpServletResponse response) {

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "Não foi possível interpretar os dados enviados no formulário.",
                request,
                response
        );
    }

    // Trata violações de integridade do banco de dados.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView tratarIntegridade(
            DataIntegrityViolationException ex,
            HttpServletRequest request,
            HttpServletResponse response) {

        return criarResposta(
                HttpStatus.CONFLICT,
                "Não foi possível realizar a operação devido a uma restrição dos dados.",
                request,
                response
        );
    }

    // Trata erros inesperados.
    @ExceptionHandler(Exception.class)
    public ModelAndView tratarErroInterno(
            Exception ex,
            HttpServletRequest request,
            HttpServletResponse response) {

        return criarResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro inesperado. Tente novamente mais tarde.",
                request,
                response
        );
    }

    // Constrói a resposta e disponibiliza os dados para o Thymeleaf.
    private ModelAndView criarResposta(
            HttpStatus status,
            String mensagem,
            HttpServletRequest request,
            HttpServletResponse response) {

        response.setStatus(status.value());

        ModelAndView modelAndView =
                new ModelAndView("erro/erro");

        modelAndView.addObject("status", status.value());
        modelAndView.addObject("titulo", status.getReasonPhrase());
        modelAndView.addObject("mensagem", mensagem);
        modelAndView.addObject("caminho", request.getRequestURI());

        return modelAndView;
    }
}