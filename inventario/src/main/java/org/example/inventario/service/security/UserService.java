package org.example.inventario.service.security;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.inventario.configuration.PasswordEncodingConfig;
import org.example.inventario.exception.MyException;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.security.Role;
import org.example.inventario.model.entity.security.User;
import org.example.inventario.model.specification.user.UserSpecification;
import org.example.inventario.repository.security.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncodingConfig passwordEncoder;
    private final RoleService roleService;

    @Transactional
    public User createUser(User user) {
        if (user == null) throw new MyException(MyException.ERROR_NOT_FOUND,"User cannot be null");
        if (StringUtils.isBlank(user.getUsername())) throw new MyException(MyException.ERROR_VALIDATION, "Username cannot be empty");
        if (StringUtils.isBlank(user.getPassword())) throw new MyException(MyException.ERROR_VALIDATION, "Password cannot be empty");
        if (userRepository.findByUsernameAndEnabledIsTrue(user.getUsername()) != null)
            throw new MyException(MyException.ERROR_VALIDATION, "User with this username already exists");

        user.setPassword(passwordEncoder.passwordEncoder().encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public ReturnList<User> findAllUsers(Pageable pageable) {
        Page<User> page = userRepository.findByEnabledIsTrue(pageable);
        page.getContent().forEach(u -> u.getRoles().size());

        ReturnList<User> result = new ReturnList<>();
        result.setPage(pageable.getPageNumber());
        result.setPageSize(pageable.getPageSize());
        result.setTotalElements((int) page.getTotalElements());
        result.setTotalPages(page.getTotalPages());
        result.setData(page.getContent());
        return result;
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        if (id == null) throw new MyException(MyException.ERROR_VALIDATION, "User ID cannot be null");
        return userRepository.findById(id)
                .orElseThrow(() -> new MyException(MyException.ERROR_NOT_FOUND, "User not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        if (StringUtils.isBlank(username)) throw new MyException(MyException.ERROR_VALIDATION, "Username cannot be empty");
        User user = userRepository.findByUsernameAndEnabledIsTrue(username);
        if (user == null) throw new MyException(MyException.ERROR_NOT_FOUND, "User not found with username: " + username);
        return user;
    }

    @Transactional
    public User updateUser(Long id, User user) {
        if (user == null) throw new MyException(MyException.ERROR_NOT_FOUND,"User cannot be null");
        if (StringUtils.isBlank(user.getUsername())) throw new MyException(MyException.ERROR_VALIDATION, "Username cannot be empty");

        User actualUser = userRepository.findById(id)
                .orElseThrow(() -> new MyException(MyException.ERROR_NOT_FOUND, "User not found with ID: " + id));

        if (!actualUser.getUsername().equals(user.getUsername())
                && userRepository.findByUsernameAndEnabledIsTrue(user.getUsername()) != null) {
            throw new MyException(MyException.ERROR_VALIDATION, "User with this username already exists");
        }

        actualUser.setUsername(user.getUsername());
        actualUser.setEnabled(user.isEnabled());
        actualUser.setEmail(user.getEmail());

        // Password is optional on update
        if (StringUtils.isNotBlank(user.getPassword())) {
            actualUser.setPassword(passwordEncoder.passwordEncoder().encode(user.getPassword()));
        }

        // Replace roles (you already have a safe setter in the entity)
        actualUser.setRoles(user.getRoles());

        return userRepository.save(actualUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (id == null) throw new MyException(MyException.ERROR_VALIDATION, "User ID cannot be null");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new MyException(MyException.ERROR_NOT_FOUND, "User not found with ID: " + id));
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Transactional
    public void enableUser(Long id) {
        if (id == null) throw new MyException(MyException.ERROR_VALIDATION, "User ID cannot be null");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new MyException(MyException.ERROR_NOT_FOUND, "User not found with ID: " + id));
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Transactional
    public void createDefaultUserIfNotExists() {
        if (userRepository.count() == 0) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.passwordEncoder().encode("admin"));
            adminUser.setEmail("admin@gmail.com");
            adminUser.setRoles(Set.of(roleService.findByName(Role.ADMIN_ROLE)));
            userRepository.save(adminUser);


            User employeeUser = new User();
            employeeUser.setUsername("employee");
            employeeUser.setPassword(passwordEncoder.passwordEncoder().encode("employee"));
            employeeUser.setEmail("employee@gmail.com");
            employeeUser.setRoles(Set.of(roleService.findByName(Role.EMPLOYEE_ROLE)));
            userRepository.save(employeeUser);

            User visitorUser = new User();
            visitorUser.setUsername("user");
            visitorUser.setPassword(passwordEncoder.passwordEncoder().encode("user"));
            visitorUser.setEmail("user@gmail.com");
            visitorUser.setRoles(Set.of(roleService.findByName(Role.USER_ROLE)));
            userRepository.save(visitorUser);
        }
    }

    @Transactional
    public void createTestUser() {
        User user = userRepository.findByUsernameAndEnabledIsTrue("miguel");
        if (user == null) {
            user = new User();
            user.setUsername("miguel");
            user.setPassword(passwordEncoder.passwordEncoder().encode("admin"));
            user.setEmail("miguel@gmail.com");
            user.setRoles(Set.of(roleService.findByName(Role.USER_ROLE)));
            userRepository.save(user);
        }

        user = userRepository.findByUsernameAndEnabledIsTrue("carlos");
        if (user == null) {
            user = new User();
            user.setUsername("carlos");
            user.setPassword(passwordEncoder.passwordEncoder().encode("admin"));
            user.setEmail("carlos@gmail.com");
            user.setRoles(Set.of(roleService.findByName(Role.USER_ROLE)));
            userRepository.save(user);
        }
    }

    @Transactional(readOnly = true)
    public Page<User> searchUsers(String username, String email, Collection<Role> roles, Pageable pageable) {
        Specification<User> spec = Specification.not(null);
        spec = spec.and(UserSpecification.hasUsernameLike(username));
        spec = spec.and(UserSpecification.hasEmailLike(email));
        spec = spec.and(UserSpecification.hasAnyRole(roles));
        spec = spec.and(UserSpecification.isEnabled());
        return userRepository.findAll(spec, pageable);
    }
}
