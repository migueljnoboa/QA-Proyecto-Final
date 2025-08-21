package org.example.inventario.controller.externalApi;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.example.inventario.model.dto.api.ProductStockChangeApi;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.ProductStockChange;
import org.example.inventario.service.inventory.ProductStockChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

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

    @GetMapping("/search")
    public ReturnList<ProductStockChangeApi> search(
            Pageable pageable,
            @RequestParam(required = false)
            @Parameter(description = "Filter by product id", example = "42")
            Long productId,
            @RequestParam(required = false)
            @Parameter(description = "Filter by increased flag", example = "true")
            Boolean increased,
            @RequestParam(required = false)
            @Parameter(description = "Minimum amount (inclusive)", example = "5")
            Integer minAmount,
            @RequestParam(required = false)
            @Parameter(description = "Maximum amount (inclusive)", example = "100")
            Integer maxAmount,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "From date-time (inclusive, ISO-8601)", example = "2025-08-01T00:00:00")
            LocalDateTime fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "To date-time (inclusive, ISO-8601)", example = "2025-08-21T23:59:59")
            LocalDateTime toDate,
            @RequestParam(required = false)
            @Parameter(description = "Exact createdBy match", example = "alice")
            String createdBy
    ) {
        Page<ProductStockChange> page = stockChangeService.searchProductStockChanges(
                pageable, productId, increased, minAmount, maxAmount, fromDate, toDate, createdBy
        );

        ReturnList<ProductStockChangeApi> result = new ReturnList<>();
        result.setPage(pageable.getPageNumber());
        result.setPageSize(pageable.getPageSize());
        result.setTotalElements((int) page.getTotalElements());
        result.setTotalPages(page.getTotalPages());
        result.setData(page.getContent().stream()
                .map(ProductStockChangeApi::from)
                .collect(Collectors.toList()));

        return result;
    }
}
