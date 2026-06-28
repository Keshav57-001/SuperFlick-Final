package com.superflick.modules.user.entity;

import com.superflick.modules.hr.entity.HRProfile;
import com.superflick.shared.enums.AccountStatus;
import com.superflick.shared.enums.AuthProvider;
import com.superflick.shared.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class User implements UserDetails {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @JdbcTypeCode(Types.VARCHAR)   // ← generates UUID as VARCHAR, not binary
    @Column(name = "id", columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private UUID id;


    @Column(unique = true) private String email;
    @Column(unique = true) private String phone;
    @Column(name = "password_hash") private String passwordHash;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING) @Column(name = "auth_provider", nullable = false)
    private AuthProvider authProvider;

    @Enumerated(EnumType.STRING) @Builder.Default
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(name = "profile_complete") private boolean profileComplete;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private HRProfile hrProfile;

    @PrePersist  protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate   protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name())); }
    @Override public String getPassword()              { return passwordHash; }
    @Override public String getUsername()              { return id.toString(); }
    @Override public boolean isAccountNonLocked()      { return status != AccountStatus.BLOCKED; }
    @Override public boolean isEnabled()               { return status == AccountStatus.ACTIVE; }
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
}