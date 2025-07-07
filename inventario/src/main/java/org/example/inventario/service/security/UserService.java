package org.example.inventario.service.security;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.example.inventario.configuration.PasswordEncodingConfig;
import org.example.inventario.exception.MyException;
import org.example.inventario.model.entity.security.Role;
import org.example.inventario.model.entity.security.User;
import org.example.inventario.repository.security.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncodingConfig passwordEncoder;
    private final RoleService roleService;


    public User createUser(User user) {
        if(user == null) {
            throw new MyException(MyException.ERROR_NOT_FOUND,"User cannot be null");
        }
        if(StringUtils.isBlank(user.getUsername())) {
            throw new MyException(MyException.ERROR_VALIDATION, "Username cannot be empty");
        }
        if(StringUtils.isBlank(user.getPassword())) {
            throw new MyException(MyException.ERROR_VALIDATION, "Password cannot be empty");
        }
        if(userRepository.findByUsernameAndEnabledIsTrue(user.getUsername()) != null) {
            throw new MyException(MyException.ERROR_VALIDATION, "User with this username already exists");
        }
        user.setPassword(passwordEncoder.passwordEncoder().encode(user.getPassword()));

        return userRepository.save(user);
    }
    public User findById(Long id) {
        if(id == null) {
            throw new MyException(MyException.ERROR_VALIDATION, "User ID cannot be null");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new MyException(MyException.ERROR_NOT_FOUND, "User not found with ID: " + id));
    }
    public User findByUsername(String username) {
        if(StringUtils.isBlank(username)) {
            throw new MyException(MyException.ERROR_VALIDATION, "Username cannot be empty");
        }
        User user = userRepository.findByUsernameAndEnabledIsTrue(username);
        if(user == null) {
            throw new MyException(MyException.ERROR_NOT_FOUND, "User not found with username: " + username);
        }
        return user;
    }

    public User updateUser(Long id, User user) {
        if(user == null) {
            throw new MyException(MyException.ERROR_NOT_FOUND,"User cannot be null");
        }
        if(StringUtils.isBlank(user.getUsername())) {
            throw new MyException(MyException.ERROR_VALIDATION, "Username cannot be empty");
        }
        if(StringUtils.isBlank(user.getPassword())) {
            throw new MyException(MyException.ERROR_VALIDATION, "Password cannot be empty");
        }
        User actualUser = userRepository.findById(id)
                .orElseThrow(() -> new MyException(MyException.ERROR_NOT_FOUND, "User not found with ID: " + id));

        if(!actualUser.getUsername().equals(user.getUsername()) && userRepository.findByUsernameAndEnabledIsTrue(user.getUsername()) != null) {
            throw new MyException(MyException.ERROR_VALIDATION, "User with this username already exists");
        }
        actualUser.setUsername(user.getUsername());
        actualUser.setPassword(passwordEncoder.passwordEncoder().encode(user.getPassword()));
        actualUser.setEnabled(user.isEnabled());
        actualUser.setRoles(user.getRoles());
        return userRepository.save(actualUser);
    }
    public void deleteUser(Long id) {
        if(id == null) {
            throw new MyException(MyException.ERROR_VALIDATION, "User ID cannot be null");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new MyException(MyException.ERROR_NOT_FOUND, "User not found with ID: " + id));
        user.setEnabled(false);
        userRepository.save(user);
    }

    public void enableUser(Long id) {
        if(id == null) {
            throw new MyException(MyException.ERROR_VALIDATION, "User ID cannot be null");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new MyException(MyException.ERROR_NOT_FOUND, "User not found with ID: " + id));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public void createDefaultUserIfNotExists() {
        User user = userRepository.findByUsernameAndEnabledIsTrue("admin");
        if(user == null) {
            user = new User();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.passwordEncoder().encode("admin"));
            user.setEmail("admin@gmail.com");
            user.setRoles(List.of( roleService.findByName(Role.ADMIN_ROLE)));
            userRepository.save(user);
        }
    }


    public void createTestUser(){
        User user = userRepository.findByUsernameAndEnabledIsTrue("miguel");
        if(user == null) {
            user = new User();
            user.setUsername("miguel");
            user.setPassword(passwordEncoder.passwordEncoder().encode("admin"));
            user.setEmail("miguel@gmail.com");
            user.setRoles(List.of(roleService.findByName(Role.USER_ROLE)));
            userRepository.save(user);
        }

        user = userRepository.findByUsernameAndEnabledIsTrue("carlos");
        if(user == null) {
            user = new User();
            user.setUsername("carlos");
            user.setPassword(passwordEncoder.passwordEncoder().encode("admin"));
            user.setEmail("carlos@gmail.com");
            user.setRoles(List.of(roleService.findByName(Role.USER_ROLE)));
            userRepository.save(user);
        }
    }

}
