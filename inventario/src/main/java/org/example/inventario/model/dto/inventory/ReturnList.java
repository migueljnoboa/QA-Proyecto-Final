package org.example.inventario.model.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.inventario.model.entity.inventory.Supplier;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReturnList<T> {
    private int page;
    private int pageSize;
    private int totalElements;
    private int totalPages;
    List<T> data;
}
