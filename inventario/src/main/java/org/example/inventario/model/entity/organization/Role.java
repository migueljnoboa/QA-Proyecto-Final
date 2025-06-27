package org.example.inventario.model.entity.organization;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.inventario.model.entity.Base;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role extends Base {
    private String name;
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Permit> permits;
}
