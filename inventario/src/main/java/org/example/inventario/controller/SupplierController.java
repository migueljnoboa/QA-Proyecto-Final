package org.example.inventario.controller;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.service.inventory.SupplierService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/supplier")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService supplierService;

    @GetMapping("")
    public ReturnList<Supplier> getAllSuppliers(@RequestParam(name = "page", defaultValue = "0") @Parameter(description = "Page Number.", example = "0") int page,
                                                @RequestParam(name = "size", defaultValue = "50") @Parameter(description = "Page Size.", example = "50") int size ){
        return supplierService.getAllSuppliers(page, size);

    }

    @GetMapping("/{id}")
    public Supplier getSupplierById(@PathVariable(name = "id") @Parameter(description = "Supplier ID.", example = "1") Long id) {
        return supplierService.getSupplierById(id);
    }

    @PostMapping("/")
    public Supplier createSupplier(@RequestBody Supplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier cannot be null");
        }
        return supplierService.createSupplier(supplier);
    }

    @PutMapping("/{id}")
    public Supplier updateSupplier(
            @PathVariable(name = "id")
            @Parameter(description = "Supplier ID.", example = "1") Long id,
            @RequestBody Supplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier cannot be null");
        }
        return supplierService.updateSupplier(id, supplier);
    }

    @DeleteMapping("/{id}")
    public Supplier deleteSupplier(
            @PathVariable(name = "id")
            @Parameter(description = "Supplier ID.", example = "1") Long id) {
       return supplierService.deleteSupplier(id);
    }



}
