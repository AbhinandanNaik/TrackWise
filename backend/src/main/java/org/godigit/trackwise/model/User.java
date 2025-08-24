package org.godigit.trackwise.model;

import jakarta.persistence.*;
import lombok.*;
import org.godigit.trackwise.model.Enum.UserStatus;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String role; // e.g., "ROLE_ADMIN", "ROLE_USER"

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

}

