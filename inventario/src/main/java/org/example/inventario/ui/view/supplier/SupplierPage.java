package org.example.inventario.ui.view.supplier;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.model.entity.security.Permit;
import org.example.inventario.service.inventory.SupplierService;
import org.example.inventario.service.security.SecurityService;
import org.example.inventario.ui.component.*;
import org.example.inventario.ui.view.MainLayout;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;

@Route(value = "/supplier", layout = MainLayout.class)
@PageTitle("Supplier")
@RolesAllowed({"SUPPLIERS_MENU"})
@Menu(order = 2, icon = "vaadin:user-card", title = "Supplier")
public class SupplierPage extends ControlPanel<Supplier> {

    private final SupplierService supplierService;
    private final SecurityService securityService;
    private final ApplicationContext applicationContext;

    public SupplierPage(SupplierService supplierService,
                        SecurityService securityService,
                        ApplicationContext applicationContext) {
        super();
        this.supplierService = supplierService;
        this.securityService = securityService;
        this.applicationContext = applicationContext;

        setId("sup-page");
        searchFilter.setId("sup-filter");
        grid.setId("sup-grid");

        setConfig();

        btnRefresh.setId("sup-btn-refresh");
        btnNew.setId("sup-btn-new");
        btnEdit.setId("sup-btn-edit");
        btnView.setId("sup-btn-view");
        btnCancel.setId("sup-btn-cancel");
        btnPrint.setId("sup-btn-print");
    }

    @Override
    protected void configurateFilter(SearchFilter filter) {
        filter.addFilter(String.class, "name", "Name", null, null, "", false);
        filter.addFilter(String.class, "contactInfo", "Contact Info", null, null, "", false);
        filter.addFilter(String.class, "address", "Address", null, null, "", false);
        filter.addFilter(String.class, "email", "Email", null, null, "", false);
        filter.addFilter(String.class, "phoneNumber", "Phone Number", null, null, "", false);

        ((TextField) filter.getFilter("name")).setId("sup-filter-name");
        ((TextField) filter.getFilter("contactInfo")).setId("sup-filter-contact");
        ((TextField) filter.getFilter("address")).setId("sup-filter-address");
        ((TextField) filter.getFilter("email")).setId("sup-filter-email");
        ((TextField) filter.getFilter("phoneNumber")).setId("sup-filter-phone");
    }

    @Override
    protected void addSecurity() {
        btnNew.setVisible(securityService.getAuthenticatedUser().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + Permit.SUPPLIER_CREATE)));
        btnEdit.setVisible(securityService.getAuthenticatedUser().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + Permit.SUPPLIER_EDIT)));
        btnView.setVisible(securityService.getAuthenticatedUser().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + Permit.SUPPLIER_VIEW)));
        btnCancel.setVisible(securityService.getAuthenticatedUser().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + Permit.SUPPLIER_DELETE)));
    }

    @Override
    protected void configGrid(Grid<Supplier> grid) {
        grid.addColumn(Supplier::getName).setHeader("Name").setKey("name").setSortable(true);
        grid.addColumn(Supplier::getContactInfo).setHeader("Contact Info").setKey("contact").setSortable(true);
        grid.addColumn(Supplier::getAddress).setHeader("Address").setKey("address").setSortable(true);
        grid.addColumn(Supplier::getEmail).setHeader("Email").setKey("email").setSortable(true);
        grid.addColumn(Supplier::getPhoneNumber).setHeader("Phone Number").setKey("phone").setSortable(true);
        grid.setSizeFull();
    }

    @Override
    protected CallbackDataProvider.FetchCallback<Supplier, Void> configDataSource() {
        return query -> {
            String name = ((TextField) searchFilter.getFilter("name")).getValue();
            String contactInfo = ((TextField) searchFilter.getFilter("contactInfo")).getValue();
            String address = ((TextField) searchFilter.getFilter("address")).getValue();
            String email = ((TextField) searchFilter.getFilter("email")).getValue();
            String phoneNumber = ((TextField) searchFilter.getFilter("phoneNumber")).getValue();

            return supplierService.searchSuppliers(
                    name, contactInfo, address, email, phoneNumber,
                    PageRequest.of(query.getPage(), query.getLimit())
            ).stream();
        };
    }

    @Override
    protected void configButtons() {
        btnNew.addClickListener(e -> {
            FormSupplier form = applicationContext.getBean(FormSupplier.class);
            form.addDetachListener(e2 -> fillGrid());
            form.open();
        });
        btnView.addClickListener(e -> {
            FormSupplier form = applicationContext.getBean(FormSupplier.class, selectedItem, true);
            form.addDetachListener(e2 -> fillGrid());
            form.open();
        });
        btnEdit.addClickListener(e -> {
            FormSupplier form = applicationContext.getBean(FormSupplier.class, selectedItem, false);
            form.addDetachListener(e2 -> fillGrid());
            form.open();
        });
        btnCancel.addClickListener(e -> deleteRow());
    }

    @Override
    protected void deleteRow() {
        ConfirmWindow cw = new ConfirmWindow(
                "Confirm Action",
                "Are you sure you want to delete this supplier: " + selectedItem.getName() + "?",
                () -> {
                    try {
                        supplierService.deleteSupplier(selectedItem.getId());
                        new MySuccessNotification(
                                "Supplier " + selectedItem.getName() + " deleted successfully."
                        ).open();
                        fillGrid();
                    } catch (Exception ex) {
                        new MyErrorNotification(ex.getMessage()).open();
                    }
                }
        );
        cw.open();
    }
}
