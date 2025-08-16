package org.example.inventario.model.entity.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.inventario.model.entity.Base;
import org.hibernate.envers.Audited;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
@Audited(withModifiedFlag = true)
public class Role extends Base {
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String USER_ROLE = "USER";
    private String name;
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Permit> permits;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private List<User> users;
}
