package org.example.inventario.model.dto.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.inventario.model.entity.inventory.Supplier;

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

    public static SupplierApi from(Supplier supplier) {
        if (supplier == null) {
            return null;
        }
        return new SupplierApi(
                supplier.getId(),
                supplier.getName(),
                supplier.getContactInfo(),
                supplier.getAddress(),
                supplier.getEmail(),
                supplier.getPhoneNumber()
        );
    }
}
