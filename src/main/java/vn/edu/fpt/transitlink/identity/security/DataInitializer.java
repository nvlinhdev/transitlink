package vn.edu.fpt.transitlink.identity.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.edu.fpt.transitlink.identity.entity.Account;
import vn.edu.fpt.transitlink.identity.entity.Role;
import vn.edu.fpt.transitlink.identity.enumeration.RoleName;
import vn.edu.fpt.transitlink.identity.repository.AccountRepository;
import vn.edu.fpt.transitlink.identity.repository.RoleRepository;

import java.util.HashSet;

@Component
public class DataInitializer {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeData() {
        initializeRoles();
        initializeAdmin();
    }

    private void initializeRoles() {
        if (!roleRepository.existsByName(RoleName.MANAGER)) {
            Role userRole = new Role();
            userRole.setName(RoleName.MANAGER);
            userRole.setDisplayName("Manager role");
            roleRepository.save(userRole);
        }

        if (!roleRepository.existsByName(RoleName.DISPATCHER)) {
            Role dispatcherRole = new Role();
            dispatcherRole.setName(RoleName.DISPATCHER);
            dispatcherRole.setDisplayName("Dispatcher role");
            roleRepository.save(dispatcherRole);
        }

        if (!roleRepository.existsByName(RoleName.TICKET_SELLER)) {
            Role ticketSellerRole = new Role();
            ticketSellerRole.setName(RoleName.TICKET_SELLER);
            ticketSellerRole.setDisplayName("Ticket Seller role");
            roleRepository.save(ticketSellerRole);
        }

        if (!roleRepository.existsByName(RoleName.DRIVER)) {
            Role driverRole = new Role();
            driverRole.setName(RoleName.DRIVER);
            driverRole.setDisplayName("Driver role");
            roleRepository.save(driverRole);
        }

        if (!roleRepository.existsByName(RoleName.PASSENGER)) {
            Role passengerRole = new Role();
            passengerRole.setName(RoleName.PASSENGER);
            passengerRole.setDisplayName("Passenger role");
            roleRepository.save(passengerRole);
        }

        System.out.println("Roles initialized successfully!");
    }

    private void initializeAdmin() {
        if (!accountRepository.existsByEmail("manager@example.com")) {
            Account manager = new Account();
            manager.setEmail("manager@example.com");
            manager.setPassword(passwordEncoder.encode("manager123"));
            manager.setFirstName("Admin");
            manager.setLastName("User");
            manager.setPhoneNumber("1234567890");

            // Khởi tạo roles để tránh NullPointerException
            manager.setRoles(new HashSet<>());

            // Gán role MANAGER
            Role managerRole = roleRepository.findByName(RoleName.MANAGER)
                    .orElseThrow(() -> new RuntimeException("Manager role not found"));
            manager.getRoles().add(managerRole);

            accountRepository.save(manager);
            System.out.println("Admin user created: manager@example.com / manager123");
        }
    }
}
