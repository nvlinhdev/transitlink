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
        initializeManager();
        initializeTicketSeller();
        initializeDispatcher();
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

    private void initializeManager() {
        // Kiểm tra xem đã có người dùng nào với role MANAGER chưa
        if (accountRepository.existsByRoleName(RoleName.MANAGER)) {
            System.out.println("Manager users already exist. Skipping manager initialization.");
            return;
        }

        if (!accountRepository.existsByEmail("manager@example.com")) {
            Account manager = new Account();
            manager.setEmail("manager@example.com");
            manager.setPassword(passwordEncoder.encode("manager123"));
            manager.setFirstName("Manager");
            manager.setLastName("User");
            manager.setEmailVerified(true);
            manager.setProfileCompleted(true);

            // Khởi tạo roles để tránh NullPointerException
            manager.setRoles(new HashSet<>());

            // Gán role MANAGER
            Role managerRole = roleRepository.findByName(RoleName.MANAGER)
                    .orElseThrow(() -> new RuntimeException("Manager role not found"));
            manager.getRoles().add(managerRole);

            accountRepository.save(manager);
            System.out.println("Manager user created: manager@example.com / manager123");
        }
    }

    private void initializeTicketSeller() {
        // Kiểm tra xem đã có người dùng nào với role TICKET_SELLER chưa
        if (accountRepository.existsByRoleName(RoleName.TICKET_SELLER)) {
            System.out.println("Ticket Seller users already exist. Skipping ticket seller initialization.");
            return;
        }

        if (!accountRepository.existsByEmail("ticketseller@example.com")) {
            Account dispatcher = new Account();
            dispatcher.setEmail("ticketseller@example.com");
            dispatcher.setPassword(passwordEncoder.encode("ticketseller123"));
            dispatcher.setFirstName("Ticket");
            dispatcher.setLastName("Seller");
            dispatcher.setEmailVerified(true);
            dispatcher.setProfileCompleted(true);
            // Khởi tạo roles để tránh NullPointerException
            dispatcher.setRoles(new HashSet<>());
            // Gán role TICKET_SELLER
            Role ticketSellerRole = roleRepository.findByName(RoleName.TICKET_SELLER)
                    .orElseThrow(() -> new RuntimeException("Ticket Seller role not found"));
            dispatcher.getRoles().add(ticketSellerRole);
            accountRepository.save(dispatcher);
            System.out.println("Ticket Seller user created: ticketseller@example.com / ticketseller123");
        }
    }

    private void initializeDispatcher() {
        // Kiểm tra xem đã có người dùng nào với role DISPATCHER chưa
        if (accountRepository.existsByRoleName(RoleName.DISPATCHER)) {
            System.out.println("Dispatcher users already exist. Skipping dispatcher initialization.");
            return;
        }

        if (!accountRepository.existsByEmail("dispatcher@example.com")) {
            Account dispatcher = new Account();
            dispatcher.setEmail("dispatcher@example.com");
            dispatcher.setPassword(passwordEncoder.encode("dispatcher123"));
            dispatcher.setFirstName("Dispatcher");
            dispatcher.setLastName("User");
            dispatcher.setEmailVerified(true);
            dispatcher.setProfileCompleted(true);
            // Khởi tạo roles để tránh NullPointerException
            dispatcher.setRoles(new HashSet<>());
            // Gán role DISPATCHER
            Role dispatcherRole = roleRepository.findByName(RoleName.DISPATCHER)
                    .orElseThrow(() -> new RuntimeException("Dispatcher role not found"));
            dispatcher.getRoles().add(dispatcherRole);
            accountRepository.save(dispatcher);
            System.out.println("Dispatcher user created: dispatcher@example.com / dispatcher123");
        }
    }
}
