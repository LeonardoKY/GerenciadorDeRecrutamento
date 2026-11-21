package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration // informa ao Spring que a classe possui configurações da aplicação.
public class PasswordConfig {

    @Bean // registra o objeto retornado pelo método no contexto do Spring.
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); //implementa a codificação segura de senhas usando BCrypt.
    }
}

/*
Registra um bean.
O algoritmo utilizado é o BCrypt,
que armazena senhas em formato de hash, em vez de texto puro.
 */