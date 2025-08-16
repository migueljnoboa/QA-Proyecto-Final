package org.example.inventario.model.entity.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.inventario.model.entity.Base;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "permits")
@Audited(withModifiedFlag = true)
public class Permit extends Base implements Serializable {
    public static final String DASHBOARD_MENU = "DASHBOARD_MENU";

    public static final String SUPPLIERS_MENU = "SUPPLIERS_MENU";
    public static final String SUPPLIER_CREATE = "SUPPLIER_CREATE";
    public static final String SUPPLIER_EDIT = "SUPPLIER_EDIT";
    public static final String SUPPLIER_DELETE = "SUPPLIER_DELETE";
    public static final String SUPPLIER_VIEW = "SUPPLIER_VIEW";

    public static final String PRODUCTS_MENU = "PRODUCTS_MENU";
    public static final String PRODUCT_CREATE = "PRODUCT_CREATE";
    public static final String PRODUCT_EDIT = "PRODUCT_EDIT";
    public static final String PRODUCT_DELETE = "PRODUCT_DELETE";
    public static final String PRODUCT_VIEW = "PRODUCT_VIEW";

    public static final String USERS_MENU = "USERS_MENU";
    public static final String USER_CREATE = "USER_CREATE";
    public static final String USER_EDIT = "USER_EDIT";
    public static final String USER_DELETE = "USER_DELETE";

    public static final String ROLES_MENU = "ROLES_MENU";
    public static final String ROLE_CREATE = "ROLE_CREATE";
    public static final String ROLE_EDIT = "ROLE_EDIT";
    public static final String ROLE_DELETE = "ROLE_DELETE";

    @Column(unique = true)
    String name;

    @ManyToMany(mappedBy = "permits")
    @JsonIgnore
    List<Role> roles;

    @Override
    public String toString() {
        return name;
    }

}
