package org.example.inventario.model.entity.inventory;

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
public class Supplier extends Base {
    private String name;
    private String contactInfo;
    private String address;
    private String email;
    private String phoneNumber;
}
