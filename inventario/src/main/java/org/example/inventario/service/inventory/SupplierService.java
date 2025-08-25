package org.example.inventario.service.inventory;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.inventario.exception.MyException;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.model.specification.supplier.SupplierSpecification;
import org.example.inventario.repository.inventory.SupplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Transactional
    public Supplier createSupplier(Supplier supplier){

        checkVariables(supplier);

        return supplierRepository.save(supplier);
    }

    @Transactional(readOnly = true)
    public Supplier getSupplierById(Long id) {
        if (id == null) {
            throw new MyException(400,"Supplier ID cannot be null");
        }
        return supplierRepository.findById(id)
                .orElseThrow(() -> new MyException(MyException.ERROR_NOT_FOUND,"Supplier not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public ReturnList<Supplier> getAllSuppliers(Pageable pageable) {
        Page<Supplier> list =  supplierRepository.findAllByEnabledIsTrue(pageable);
        ReturnList<Supplier> result = new ReturnList<>();
        result.setPage(pageable.getPageNumber());
        result.setPageSize(pageable.getPageSize());
        result.setTotalElements((int) list.getTotalElements());
        result.setTotalPages(list.getTotalPages());
        result.setData(list.getContent());
        return result;

    }

    @Transactional
    public Supplier updateSupplier(Long id,  Supplier supplier) {

        if(id == null) {
            throw  new MyException(400, "Id cannot be null");
        }

        Supplier oldSupplier = supplierRepository.findById(id).orElse(null);

        if(supplier == null || oldSupplier == null) {
            throw  new MyException(400, "Supplier not found or null");
        }

        checkVariables(supplier);

        oldSupplier.setName(supplier.getName());
        oldSupplier.setContactInfo(supplier.getContactInfo());
        oldSupplier.setAddress(supplier.getAddress());
        oldSupplier.setEmail(supplier.getEmail());
        oldSupplier.setPhoneNumber(supplier.getPhoneNumber());

        return supplierRepository.save(oldSupplier);
    }

    @Transactional
    public Supplier deleteSupplier(Long id) {
        if (id == null) {
            throw new MyException(400,"Supplier ID cannot be null");
        }
        Supplier supplier = supplierRepository.findById(id).orElse(null);

        if (supplier == null) {
            throw  new MyException(400, "Supplier not found or null");
        }
        supplier.setEnabled(false);
        return supplierRepository.save(supplier);
    }

    private void checkVariables(Supplier supplier) {
        if(supplier == null) {
            throw new MyException(400,"Supplier cannot be null");
        }
        if(StringUtils.isBlank(supplier.getName())) {
            throw  new MyException(400,"Supplier name cannot be null or empty");
        }
        if(StringUtils.isBlank(supplier.getContactInfo())) {
            throw new MyException(400,"Supplier contact info cannot be null or empty");
        }
        if(StringUtils.isBlank(supplier.getAddress())) {
            throw new MyException(400,"Supplier address cannot be null or empty");
        }
        if(StringUtils.isBlank(supplier.getEmail())) {
            throw new MyException(400,"Supplier email cannot be null or empty");
        }
        if(StringUtils.isBlank(supplier.getPhoneNumber())) {
            throw new MyException(400, "Supplier phone number cannot be null or empty");
        }
    }

    @Transactional(readOnly = true)
    public Page<Supplier>searchSuppliers(String name, String contactInfo, String address, String mail, String phone, PageRequest pageable) {
        Specification<Supplier> spec = Specification.not(null);
        spec = spec.and(SupplierSpecification.hasName(name));
        spec = spec.and(SupplierSpecification.hasContactInfo(contactInfo));
        spec = spec.and(SupplierSpecification.hasAddress(address));
        spec = spec.and(SupplierSpecification.hasEmail(mail));
        spec = spec.and(SupplierSpecification.hasPhoneNumber(phone));
        spec = spec.and(SupplierSpecification.isEnabled());
        return supplierRepository.findAll(spec, pageable);

    }
}
