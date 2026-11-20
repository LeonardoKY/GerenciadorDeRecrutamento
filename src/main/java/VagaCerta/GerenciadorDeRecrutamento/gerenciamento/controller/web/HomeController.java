package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Redireciona a rota principal para a página inicial.
    @GetMapping("/")
    public String inicio() {
        return "redirect:/home";
    }

    // Exibe a página inicial do sistema.
    @GetMapping("/home")
    public String home() {
        return "home";
    }
}
