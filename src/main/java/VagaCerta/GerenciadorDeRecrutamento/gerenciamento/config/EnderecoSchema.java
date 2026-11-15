package VagaCerta.GerenciadorDeRecrutamento.gerenciamento.config;

/*
Migração não destrutiva da tabela APP. ENDERECO, executada depois da inicialização JPA.

Permite que um endereço pertença exclusivamente a um candidato ou a uma empresa.
 */

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class EnderecoSchema {
    @Bean
    ApplicationRunner runner(JdbcTemplate jdbc) {
        return args -> {
            //Cria as colunas, caso ainda não exista.
            jdbc.execute("ALTER TABLE APP.ENDERECO ADD COLUMN IF NOT EXISTS CANDIDATO_ID UUID");
            jdbc.execute("ALTER TABLE APP.ENDERECO ADD COLUMN IF NOT EXISTS EMPRESA_ID UUID");
            // Permite que apenas um dos proprietários seja informado.
            jdbc.execute("ALTER TABLE APP.ENDERECO ALTER COLUMN CANDIDATO_ID DROP NOT NULL");
            jdbc.execute("ALTER TABLE APP.ENDERECO ALTER COLUMN EMPRESA_ID DROP NOT NULL");

            // Índice único -> cada candidato pode possuir apenas um endereço.
            jdbc.execute("CREATE UNIQUE INDEX IF NOT EXISTS IDX_ENDERECO_CANDIDATO_UNICO " +
                    "ON APP.ENDERECO(CANDIDATO_ID)");
            // Índice comum -> uma empresa pode possuir vários endereços.
            jdbc.execute("CREATE INDEX IF NOT EXISTS IDX_ENDERECO_EMPRESA " +
                    "ON APP.ENDERECO(EMPRESA_ID)");


            /* Restrição Check

            Garante que um endereço não seja compartilhado entre uma empresa e um candidato,
            nem fique sem proprietário.
             */

            // Verifica se a restrição já existe
            Integer restricao = jdbc.queryForObject("""
                    SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
                    WHERE TABLE_SCHEMA='APP' AND TABLE_NAME='ENDERECO'
                      AND CONSTRAINT_NAME='CK_ENDERECO_DONO_EXCLUSIVO'
                    """, Integer.class);

            // Impede endereços sem proprietário ou com dois proprietários.
            if (restricao != null && restricao == 0) {
                jdbc.execute("""
                        ALTER TABLE APP.ENDERECO ADD CONSTRAINT CK_ENDERECO_DONO_EXCLUSIVO
                        CHECK (
                            (CANDIDATO_ID IS NOT NULL AND EMPRESA_ID IS NULL)
                            OR
                            (CANDIDATO_ID IS NULL AND EMPRESA_ID IS NOT NULL)
                        )
                        """);
            }
        };
    }
}
