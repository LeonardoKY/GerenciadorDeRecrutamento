package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

//Controller responsável por apresentar a página de login.

@Controller
public class AuthController {

    // Exibe a página de login.
    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String erro,
            @RequestParam(required = false) String logout,
            Model model) {

        if (erro != null) {
            model.addAttribute(
                    "erro",
                    "E-mail ou senha inválidos."
            );
        }

        if (logout != null) {
            model.addAttribute(
                    "mensagem",
                    "Você saiu do sistema com sucesso."
            );
        }

        return "login";
    }
}
