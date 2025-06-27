package org.example.inventario.model.entity.organization;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.inventario.model.entity.Base;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Permit extends Base {
    String name;
}
