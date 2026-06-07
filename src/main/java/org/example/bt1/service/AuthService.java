package org.example.bt1.service;

import lombok.RequiredArgsConstructor;
import org.example.bt1.dto.AuthRequest;
import org.example.bt1.dto.AuthResponse;
import org.example.bt1.dto.RefreshRequest;
import org.example.bt1.entity.Employee;
import org.example.bt1.entity.Token;
import org.example.bt1.entity.TokenType;
import org.example.bt1.jwt.JwtService;
import org.example.bt1.repository.EmployeeRepository;
import org.example.bt1.repository.TokenRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final EmployeeRepository employeeRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        Employee employee = employeeRepository.findByUsername(request.getUsername()).orElseThrow();

        String accessToken = jwtService.generateAccessToken(employee);
        String refreshToken = jwtService.generateRefreshToken(employee);

        saveToken(employee, accessToken, TokenType.ACCESS);
        saveToken(employee, refreshToken, TokenType.REFRESH);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(RefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        Token dbToken = tokenRepository.findByTokenValue(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token không tồn tại"));

        if (dbToken.getRevoked() || dbToken.getExpired()) {
            throw new RuntimeException("Refresh token không hợp lệ");
        }

        Employee employee = dbToken.getEmployee();

        if (!jwtService.isTokenValid(refreshToken, employee)) {
            dbToken.setExpired(true);
            tokenRepository.save(dbToken);
            throw new RuntimeException("Refresh token đã hết hạn");
        }

        String newAccessToken = jwtService.generateAccessToken(employee);
        saveToken(employee, newAccessToken, TokenType.ACCESS);

        return new AuthResponse(newAccessToken, refreshToken);
    }

    public void logout(String username) {
        Employee employee = employeeRepository.findByUsername(username).orElseThrow();

        tokenRepository.findByEmployeeAndRevokedFalseAndExpiredFalse(employee)
                .stream()
                .peek(token -> token.setRevoked(true))
                .peek(token -> token.setExpired(true))
                .collect(Collectors.toList())
                .forEach(tokenRepository::save);

        SecurityContextHolder.clearContext();
    }

    private void saveToken(Employee employee, String tokenValue, TokenType tokenType) {
        Token token = Token.builder()
                .employee(employee)
                .tokenValue(tokenValue)
                .tokenType(tokenType)
                .revoked(false)
                .expired(false)
                .build();

        tokenRepository.save(token);
    }
}
