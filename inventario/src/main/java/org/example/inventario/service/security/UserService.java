package org.example.inventario.service.security;

import lombok.RequiredArgsConstructor;
import org.example.inventario.repository.security.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;




}
