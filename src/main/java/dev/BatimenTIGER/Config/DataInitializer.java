package dev.BatimenTIGER.Config;

import dev.BatimenTIGER.Model.Admin;
import dev.BatimenTIGER.Repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initAdmin(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            try {
                String username = "bazoutiger";

                // On vérifie si l'utilisateur "bazoutiger" existe déjà
                if (adminRepository.findByUsername(username).isEmpty()) {
                    Admin admin = new Admin();
                    admin.setUsername(username);
                    admin.setPassword(passwordEncoder.encode("06062001"));
                    adminRepository.save(admin);
                    System.out.println(">>> Administrateur par défaut créé : " + username);
                } else {
                    System.out.println(">>> L'administrateur '" + username + "' existe déjà. Démarrage normal.");
                }
            } catch (Exception e) {
                // On capture l'erreur pour ne pas bloquer le démarrage de l'application
                System.err.println(">>> Erreur lors de l'initialisation de l'admin : " + e.getMessage());
            }
        };
    }
}