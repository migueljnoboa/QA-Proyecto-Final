package org.example.inventario.ui.view.role;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.example.inventario.model.entity.security.Permit;
import org.example.inventario.model.entity.security.Role;
import org.example.inventario.service.security.PermitService;
import org.example.inventario.service.security.RoleService;
import org.example.inventario.service.security.SecurityService;
import org.example.inventario.ui.component.ConfirmWindow;
import org.example.inventario.ui.component.ControlPanel;
import org.example.inventario.ui.component.MyErrorNotification;
import org.example.inventario.ui.component.MySuccessNotification;
import org.example.inventario.ui.component.SearchFilter;
import org.example.inventario.ui.view.MainLayout;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;

import java.util.stream.Collectors;

@Route(value = "/role", layout = MainLayout.class)
@PageTitle("Role")
@RolesAllowed({Permit.ROLES_MENU})
@Menu(order = 3, icon = "vaadin:users", title = "Role")
public class RolePage extends ControlPanel<Role> {

    private final RoleService roleService;
    private final PermitService permitService;
    private final SecurityService securityService;
    private final ApplicationContext applicationContext;

    public RolePage(RoleService roleService,
                    PermitService permitService,
                    SecurityService securityService,
                    ApplicationContext applicationContext) {
        super();
        this.roleService = roleService;
        this.permitService = permitService;
        this.securityService = securityService;
        this.applicationContext = applicationContext;

        setId("role-page");
        searchFilter.setId("role-filter");
        grid.setId("role-grid");

        setConfig();

        btnRefresh.setId("role-btn-refresh");
        btnNew.setId("role-btn-new");
        btnEdit.setId("role-btn-edit");
        btnView.setId("role-btn-view");
        btnCancel.setId("role-btn-cancel");
        btnPrint.setId("role-btn-print");
    }

    @Override
    protected void configurateFilter(SearchFilter filter) {
        filter.addFilter(String.class, "name", "Name", null, null, "", false);
        filter.addFilter(Permit.class, "permits", "Permits", permitService.findAll(), null, null, false);

        ((TextField) filter.getFilter("name")).setId("role-filter-name");
        @SuppressWarnings("unchecked")
        ComboBox<Permit> permitCb = (ComboBox<Permit>) filter.getFilter("permits");
        if (permitCb != null) {
            permitCb.setItemLabelGenerator(Permit::getName);
            permitCb.setId("role-filter-permit");
            permitCb.setClearButtonVisible(true);
        }
    }

    @Override
    protected void addSecurity() {
        var user = securityService.getAuthenticatedUser();
        boolean canCreate = user != null && user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + Permit.ROLE_CREATE));
        boolean canEdit = user != null && user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + Permit.ROLE_EDIT));
        boolean canDelete = user != null && user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + Permit.ROLE_DELETE));

        btnNew.setVisible(canCreate);
        btnEdit.setVisible(canEdit);
        btnView.setVisible(true);   // Anyone with ROLES_MENU can view
        btnCancel.setVisible(canDelete);
        btnPrint.setVisible(false);
    }

    @Override
    protected void configGrid(Grid<Role> grid) {
        grid.addColumn(Role::getName).setHeader("Name").setKey("name").setSortable(true);
        grid.addColumn(Role::getDescription).setHeader("Description").setKey("description").setSortable(true);
        grid.addColumn(role -> role.getPermits().stream().map(Permit::getName).collect(Collectors.joining(", ")))
                .setHeader("Permits").setKey("permits").setSortable(false);
        grid.setSizeFull();
    }

    @Override
    protected CallbackDataProvider.FetchCallback<Role, Void> configDataSource() {
        return query -> {
            String name = ((TextField) searchFilter.getFilter("name")).getValue();
            @SuppressWarnings("unchecked")
            ComboBox<Permit> permitCb = (ComboBox<Permit>) searchFilter.getFilter("permits");
            Permit permit = permitCb != null ? permitCb.getValue() : null;

            return roleService.searchRole(
                    name, permit,
                    PageRequest.of(query.getPage(), query.getLimit())
            ).stream();
        };
    }

    @Override
    protected void configButtons() {
        btnNew.addClickListener(e -> {
            FormRole form = applicationContext.getBean(FormRole.class);
            form.addDetachListener(ev -> fillGrid());
            form.open();
        });
        btnView.addClickListener(e -> {
            if (selectedItem == null) return;
            FormRole form = applicationContext.getBean(FormRole.class, selectedItem, true);
            form.addDetachListener(ev -> fillGrid());
            form.open();
        });
        btnEdit.addClickListener(e -> {
            if (selectedItem == null) return;
            FormRole form = applicationContext.getBean(FormRole.class, selectedItem, false);
            form.addDetachListener(ev -> fillGrid());
            form.open();
        });
        btnCancel.addClickListener(e -> deleteRow());
    }

    @Override
    protected void deleteRow() {
        if (selectedItem == null) return;
        ConfirmWindow cw = new ConfirmWindow(
                "Confirm Action",
                "Are you sure you want to delete this role: " + selectedItem.getName() + "?",
                () -> {
                    try {
                        roleService.deleteRole(selectedItem.getId());
                        new MySuccessNotification("Role " + selectedItem.getName() + " deleted successfully.").open();
                        fillGrid();
                    } catch (Exception ex) {
                        new MyErrorNotification(ex.getMessage()).open();
                    }
                }
        );
        cw.open();
    }
}
