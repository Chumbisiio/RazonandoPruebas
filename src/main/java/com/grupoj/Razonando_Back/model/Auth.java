package com.grupoj.Razonando_Back.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "auth")
@Getter
@Setter 
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(name = "supplier" , nullable = false, length = 50)
    @Size(max = 50)
    private String supplier;

    @Column(name = "supplier_id", nullable = true, length = 50)
    @Size(max = 50)
    private String supplierId;

    @Column(name = "pass_hash", nullable = true, length = 255)
    @Size(max = 255)
    private String passHash;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Auth that = (Auth) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
