package com.taru.eventmanagement;

import com.taru.eventmanagement.models.Role;
import com.taru.eventmanagement.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Vérifier si les rôles existent déjà
        if (roleRepository.findByName("ROLE_USER") == null) {
            Role roleUser = new Role();
            roleUser.setName("ROLE_USER");
            roleRepository.save(roleUser);
        }

        if (roleRepository.findByName("ROLE_ADMIN") == null) {
            Role roleAdmin = new Role() ;
            roleAdmin.setName("ROLE_ADMIN");
            roleRepository.save(roleAdmin);
        }
    }
}
