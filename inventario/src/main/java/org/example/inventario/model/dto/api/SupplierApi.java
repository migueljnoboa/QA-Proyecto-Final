package org.example.inventario.model.dto.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.Supplier;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SupplierApi {
    private Long id;
    private String name;
    private String contactInfo;
    private String address;
    private String email;
    private String phoneNumber;
    private boolean enabled;

    public static SupplierApi from(Supplier supplier){
        if (supplier == null) {
            return null;
        }
        SupplierApi supplierApi = new SupplierApi();
        supplierApi.setId(supplier.getId());
        supplierApi.setName(supplier.getName());
        supplierApi.setContactInfo(supplier.getContactInfo());
        supplierApi.setAddress(supplier.getAddress());
        supplierApi.setEmail(supplier.getEmail());
        supplierApi.setPhoneNumber(supplier.getPhoneNumber());
        supplierApi.setEnabled(supplierApi.isEnabled());
        return supplierApi;
    }

    public static List<SupplierApi> from(List<Supplier> supplierList){
        List<SupplierApi> supplierApis = new ArrayList<>();
        for (Supplier supplier : supplierList) {
            supplierApis.add(from(supplier));
        }
        return supplierApis;
    }

    public static ReturnList<SupplierApi> from(ReturnList<Supplier> returnList){
        ReturnList<SupplierApi> result = new ReturnList<>();
        result.setPage(returnList.getPage());
        result.setPageSize(returnList.getPageSize());
        result.setTotalElements(returnList.getTotalElements());
        result.setTotalPages(returnList.getTotalPages());
        result.setData(from(returnList.getData()));
        return result;
    }
}
