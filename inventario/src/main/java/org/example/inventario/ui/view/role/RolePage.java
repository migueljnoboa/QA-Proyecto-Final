package org.example.inventario.ui.view.role;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.example.inventario.model.entity.inventory.Category;
import org.example.inventario.model.entity.security.Permit;
import org.example.inventario.model.entity.security.Role;
import org.example.inventario.service.security.PermitService;
import org.example.inventario.service.security.RoleService;
import org.example.inventario.ui.component.ControlPanel;
import org.example.inventario.ui.component.SearchFilter;
import org.example.inventario.ui.view.MainLayout;
import org.springframework.data.domain.PageRequest;

import java.util.stream.Collectors;

@Route(value ="/role", layout = MainLayout.class)
@PageTitle("Role")
@RolesAllowed({"ROLES_MENU"})
@Menu(order = 3, icon = "vaadin:users", title = "Role")
public class RolePage extends ControlPanel<Role> {

    private RoleService roleService;
    private PermitService permitService;

    public RolePage(RoleService roleService, PermitService permitService) {
        super();
        this.roleService = roleService;
        this.permitService = permitService;
        setConfig();
    }

    @Override
    protected void configurateFilter(SearchFilter filter) {
        filter.addFilter(String.class, "name", "Name", null, null, "", false);
        filter.addFilter(Permit.class, "permits", "Permits", permitService.findAll(), null, null, false);
    }

    @Override
    protected void addSecurity() {

    }

    @Override
    protected void configGrid(Grid<Role> grid) {
        grid.addColumn(Role::getName).setHeader("Name").setSortable(true);
        grid.addColumn(role -> role.getPermits().stream()
                .map(Permit::getName)
                .collect(Collectors.joining(", ")))
                .setHeader("Permits").setSortable(true);
        grid.setSizeFull();
    }

    @Override
    protected CallbackDataProvider.FetchCallback<Role, Void> configDataSource() {
        return query -> {
        TextField name = (TextField) searchFilter.getFilter("name");
        ComboBox<Permit> permit = (ComboBox<Permit>) searchFilter.getFilter("permits");
            return roleService.searchRole(name.getValue(), permit.getValue(), PageRequest.of(query.getPage(), query.getLimit())).stream();
        };
    }

    @Override
    protected void configButtons() {

    }

    @Override
    protected void deleteRow() {

    }
}
