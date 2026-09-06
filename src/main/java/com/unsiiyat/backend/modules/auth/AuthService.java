package com.unsiiyat.backend.modules.auth;

import com.unsiiyat.backend.common.exceptions.BadRequestException;
import com.unsiiyat.backend.common.exceptions.UnauthorizedException;
import com.unsiiyat.backend.common.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResponseDto register(RegisterRequestDto requestDto) {
        String email = requestDto.getEmail();

        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email is already registered: " + email);
        }

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setName(requestDto.getName());
        user.setRole(UserRole.ADMIN); // Strictly ADMIN by default
        user.setIsActive(true);

        UserEntity savedUser = userRepository.save(user);
        String token = jwtTokenProvider.generateToken(savedUser);

        return new LoginResponseDto(token, savedUser.getRole(), savedUser.getEmail(), savedUser.getName());
    }

    public LoginResponseDto login(LoginRequestDto requestDto) {
        String email = requestDto.getEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new UnauthorizedException("User account is inactive. Please contact administrator.");
        }

        boolean matches = passwordEncoder.matches(requestDto.getPassword(), user.getPassword());
        if (!matches) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user);

        return new LoginResponseDto(token, user.getRole(), user.getEmail(), user.getName());
    }
}
