package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.config;

import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.model.Perfil;
import VagaCerta.GerenciadorDeRecrutamento.gerenciamento.service.UsuarioService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/*
executado quando a aplicação iniciar e verificará se já existem usuários cadastrados.
Se o banco estiver vazio, criará o primeiro administrador de forma automática.
 */

@Component
public class AdminInitializer implements ApplicationRunner {

    private final UsuarioService usuarioService;

    @Value("${app.admin.nome:}")
    private String nome;

    @Value("${app.admin.email:}")
    private String email;

    @Value("${app.admin.senha:}")
    private String senha;

    public AdminInitializer(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(ApplicationArguments args) {

        // Não cria outro administrador se já existem usuários.
        if (!usuarioService.listarTodos().isEmpty()) {
            return;
        }

        // Exige credenciais configuradas para a primeira inicialização.
        if (nome.isBlank() || email.isBlank() || senha.isBlank()) {
            throw new IllegalStateException(
                    "Configure app.admin.nome, app.admin.email "
                            + "e app.admin.senha para criar o primeiro administrador."
            );
        }

        usuarioService.cadastrar(
                nome,
                email,
                senha,
                Perfil.ADMIN
        );

        System.out.println("Administrador inicial cadastrado com sucesso.");
    }
}
