package org.example.inventario.model.entity.inventory;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.inventario.model.entity.Base;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product extends Base {
    String name;
    @Lob
    String description;

    @Enumerated(EnumType.STRING)
    Category category;

    BigDecimal price;

    Integer stock;

    Integer minStock;
    @Lob
    String image;

    @ManyToOne(fetch = FetchType.EAGER)
    private Supplier supplier;


}
