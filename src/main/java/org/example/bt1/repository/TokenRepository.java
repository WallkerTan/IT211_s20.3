package org.example.bt1.repository;

import org.example.bt1.entity.Employee;
import org.example.bt1.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByTokenValue(String tokenValue);

    List<Token> findByEmployeeAndRevokedFalseAndExpiredFalse(Employee employee);
}
