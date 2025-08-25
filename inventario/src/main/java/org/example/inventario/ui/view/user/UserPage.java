package org.example.inventario.ui.view.user;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.apache.commons.lang3.StringUtils;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.security.Permit;
import org.example.inventario.model.entity.security.Role;
import org.example.inventario.model.entity.security.User;
import org.example.inventario.service.security.RoleService;
import org.example.inventario.service.security.SecurityService;
import org.example.inventario.service.security.UserService;
import org.example.inventario.ui.component.*;
import org.example.inventario.ui.view.MainLayout;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Set;
import java.util.stream.Collectors;

@Route(value = "/user", layout = MainLayout.class)
@PageTitle("Users")
@RolesAllowed({Permit.USERS_MENU})
@Menu(order = 4, icon = "vaadin:user", title = "Users")
public class UserPage extends ControlPanel<User> {

    private final UserService userService;
    private final RoleService roleService;
    private final SecurityService securityService;
    private final ApplicationContext applicationContext;

    public UserPage(ApplicationContext applicationContext,
                    UserService userService,
                    RoleService roleService,
                    SecurityService securityService) {
        super();
        this.applicationContext = applicationContext;
        this.userService = userService;
        this.roleService = roleService;
        this.securityService = securityService;
        setConfig();
    }

    @Override
    protected void configurateFilter(SearchFilter filter) {
        filter.addFilter(String.class, "username", "Username", null, null, "", false);
        filter.addFilter(String.class, "email", "Email", null, null, "", false);

        DataProvider<Role, String> rolesProvider = DataProvider.fromFilteringCallbacks(
                q -> {
                    PageRequest pr = PageRequest.of(q.getOffset() / q.getLimit(), q.getLimit());
                    ReturnList<Role> rl = roleService.listAllRole(pr);
                    return rl.getData().stream();
                },
                q -> {
                    PageRequest pr = PageRequest.of(0, 1);
                    return (int) roleService.listAllRole(pr).getTotalElements();
                }
        );
        filter.addFilter(Role.class, "roles", "Roles", null, rolesProvider::fetch, null, true);

        @SuppressWarnings("unchecked")
        MultiSelectComboBox<Role> rolesBox = (MultiSelectComboBox<Role>) filter.getFilter("roles");
        if (rolesBox != null) {
            rolesBox.setItemLabelGenerator(Role::getName);
            rolesBox.setId("user-filter-roles");
            rolesBox.setClearButtonVisible(true);
            rolesBox.setWidth("280px");
        }

        ((TextField) filter.getFilter("username")).setId("user-filter-username");
        ((TextField) filter.getFilter("email")).setId("user-filter-email");
    }

    @Override
    protected void addSecurity() {
        btnRefresh.setId("user-btn-refresh");
        btnNew.setId("user-btn-new");
        btnEdit.setId("user-btn-edit");
        btnView.setId("user-btn-view");
        btnCancel.setId("user-btn-delete");
        btnPrint.setId("user-btn-print");

        btnNew.setVisible(securityService.getAuthenticatedUser().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + Permit.USER_CREATE)));
        btnEdit.setVisible(securityService.getAuthenticatedUser().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + Permit.USER_EDIT)));
        btnView.setVisible(securityService.getAuthenticatedUser().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + Permit.USER_CREATE) // or a dedicated USER_VIEW
                        || a.getAuthority().equals("ROLE_" + Permit.USER_EDIT)));
        btnCancel.setVisible(securityService.getAuthenticatedUser().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + Permit.USER_DELETE)));
    }

    @Override
    protected void configGrid(Grid<User> grid) {
        grid.setId("user-grid");
        grid.addColumn(User::getUsername).setHeader("Username").setSortable(true);
        grid.addColumn(User::getEmail).setHeader("Email").setSortable(true);
        grid.addColumn(u -> u.getRoles().stream().map(Role::getName).collect(Collectors.joining(", ")))
                .setHeader("Roles").setSortable(false);
        grid.addColumn(User::isEnabled).setHeader("Enabled").setSortable(true);
        grid.setSizeFull();
    }

    @Override
    protected CallbackDataProvider.FetchCallback<User, Void> configDataSource() {
        return query -> {
            TextField tfUsername = (TextField) searchFilter.getFilter("username");
            TextField tfEmail = (TextField) searchFilter.getFilter("email");
            @SuppressWarnings("unchecked")
            MultiSelectComboBox<Role> rolesBox = (MultiSelectComboBox<Role>) searchFilter.getFilter("roles");

            String username = tfUsername != null ? tfUsername.getValue() : null;
            String email = tfEmail != null ? tfEmail.getValue() : null;
            var roles = rolesBox != null ? rolesBox.getSelectedItems() : Set.<Role>of();

            Page<User> page = userService.searchUsers(
                    StringUtils.isBlank(username) ? null : username,
                    StringUtils.isBlank(email) ? null : email,
                    roles,
                    PageRequest.of(query.getPage(), query.getLimit())
            );
            return page.stream();
        };
    }

    @Override
    protected void configButtons() {
        btnNew.addClickListener(e -> {
            FormUser form = applicationContext.getBean(FormUser.class); // NEW
            form.addDetachListener(ev -> fillGrid());
            form.open();
        });
        btnEdit.addClickListener(e -> {
            FormUser form = applicationContext.getBean(FormUser.class, selectedItem, false);
            form.addDetachListener(ev -> fillGrid());
            form.open();
        });
        btnView.addClickListener(e -> {
            FormUser form = applicationContext.getBean(FormUser.class, selectedItem, true);
            form.addDetachListener(ev -> fillGrid());
            form.open();
        });
        btnCancel.addClickListener(e -> deleteRow());
    }

    @Override
    protected void deleteRow() {
        ConfirmWindow cw = new ConfirmWindow(
                "Confirm Action",
                "Are you sure you want to delete user: " + (selectedItem != null ? selectedItem.getUsername() : "") + "?",
                () -> {
                    try {
                        userService.deleteUser(selectedItem.getId());
                        new MySuccessNotification("User deleted successfully.").open();
                        fillGrid();
                    } catch (Exception ex) {
                        new MyErrorNotification(ex.getMessage()).open();
                    }
                }
        );
        cw.setId("user-delete-confirm");
        cw.open();
    }
}
