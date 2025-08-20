package org.example.inventario.controller.externalApi;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.example.inventario.model.dto.api.ProductStockChangeApi;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.ProductStockChange;
import org.example.inventario.service.inventory.ProductStockChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("api/stockchange")
@RequiredArgsConstructor
public class StockChangeController {
    private final ProductStockChangeService stockChangeService;

    @GetMapping("{id}")
    public ProductStockChangeApi getById(@PathVariable(name = "id") @Parameter(description = "Stock Change ID.", example = "1") Long id) {
        ProductStockChange entity = stockChangeService.getById(id);
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock change not found with ID: " + id);
        }
        return ProductStockChangeApi.from(entity);
    }

    @GetMapping
    public ReturnList<ProductStockChangeApi> getAll(Pageable pageable) {
        return ProductStockChangeApi.from(stockChangeService.getAll(pageable));
    }
}
