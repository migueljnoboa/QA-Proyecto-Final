package org.example.inventario.controller;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.example.inventario.model.dto.api.SupplierApi;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.service.inventory.SupplierService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/supplier")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService supplierService;

    @GetMapping("")
    public ReturnList<SupplierApi> getAllSuppliers(Pageable pageable) {
        return SupplierApi.from(supplierService.getAllSuppliers(pageable));

    }

    @GetMapping("/{id}")
    public SupplierApi getSupplierById(@PathVariable(name = "id") @Parameter(description = "Supplier ID.", example = "1") Long id) {
        return SupplierApi.from(supplierService.getSupplierById(id));
    }

    @PostMapping("/")
    public SupplierApi createSupplier(@RequestBody Supplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier cannot be null");
        }
        return SupplierApi.from(supplierService.createSupplier(supplier));
    }

    @PutMapping("/{id}")
    public SupplierApi updateSupplier(
            @PathVariable(name = "id")
            @Parameter(description = "Supplier ID.", example = "1") Long id,
            @RequestBody Supplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier cannot be null");
        }
        return SupplierApi.from(supplierService.updateSupplier(id, supplier));
    }

    @DeleteMapping("/{id}")
    public SupplierApi deleteSupplier(
            @PathVariable(name = "id")
            @Parameter(description = "Supplier ID.", example = "1") Long id) {
       return SupplierApi.from(supplierService.deleteSupplier(id));
    }



}
