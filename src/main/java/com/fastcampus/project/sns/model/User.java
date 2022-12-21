package com.fastcampus.project.sns.model;

import com.fastcampus.project.sns.model.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
public class User implements UserDetails {

    private Integer id;
    private String username;
    private String password;
    private UserRole userRole;
    private Timestamp registerAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static User fromEntity(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getUserName(),
                entity.getPassword(),
                entity.getRole(),
                entity.getRegisterAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.getUserRole().toString()));
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return this.username;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return this.deletedAt == null;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return this.deletedAt == null;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return this.deletedAt == null;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return this.deletedAt == null;
    }
}
