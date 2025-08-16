package org.example.inventario.model.entity.inventory;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.example.inventario.model.entity.Base;
import org.hibernate.envers.Audited;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Audited(withModifiedFlag = true)
public class Supplier extends Base implements Serializable {
    private String name;
    private String contactInfo;
    private String address;
    private String email;
    private String phoneNumber;
}
