package org.example.inventario.model.entity.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.inventario.model.entity.Base;
import org.hibernate.envers.Audited;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
@Audited(withModifiedFlag = true)
public class Role extends Base {
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String EMPLOYEE_ROLE = "EMPLOYEE";
    public static final String USER_ROLE = "USER";
    private String name;
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Permit> permits = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private Set<User> users = new LinkedHashSet<>();
}
