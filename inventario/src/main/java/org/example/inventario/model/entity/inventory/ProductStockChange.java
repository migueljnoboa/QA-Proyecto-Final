package org.example.inventario.model.entity.inventory;

import jakarta.persistence.*;
import lombok.*;
import org.example.inventario.model.entity.Base;
import org.hibernate.envers.Audited;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockChange implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L; // fixed version
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private boolean increased;
    private int amount;
    private LocalDateTime date;
    private String createdBy;
}
