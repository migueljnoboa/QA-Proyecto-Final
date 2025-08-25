package org.example.inventario.model.entity.security;

import jakarta.persistence.*;
import lombok.*;
import org.example.inventario.model.entity.Base;
import org.hibernate.envers.Audited;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users",
        indexes = {
                @Index(name = "idx_user_username", columnList = "username")
        })
@Audited(withModifiedFlag = true)
public class User extends Base {
    private String username;
    private String password;
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @Setter(AccessLevel.NONE)
    private Set<Role> roles = new java.util.LinkedHashSet<>();

    public void setRoles(Collection<Role> roles) {
        this.roles.clear();
        if (roles != null) this.roles.addAll(roles);
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

}
