package org.example.inventario.model.entity.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.inventario.model.entity.Base;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "permits")
public class Permit extends Base {
    public static final String DASHBOARD_MENU = "DASHBOARD_MENU";
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
    List<Role> roles;

}
