package org.example.bt1.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1500, nullable = false)
    private String tokenValue;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private Boolean revoked;

    private Boolean expired;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
