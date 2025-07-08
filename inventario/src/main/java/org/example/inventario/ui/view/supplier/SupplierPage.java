package org.example.inventario.ui.view.supplier;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.example.inventario.model.entity.inventory.Category;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.service.inventory.ProductService;
import org.example.inventario.service.inventory.SupplierService;
import org.example.inventario.ui.component.ControlPanel;
import org.example.inventario.ui.component.SearchFilter;
import org.example.inventario.ui.view.MainLayout;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Arrays;

@Route(value ="/supplier", layout = MainLayout.class)
@PageTitle("Supplier")
@RolesAllowed({"PRODUCTS_MENU"})
@Menu(order = 1, icon = "vaadin:user-card", title = "Supplier")
public class SupplierPage extends ControlPanel<Supplier> {
    private SupplierService supplierService;

    public SupplierPage(SupplierService supplierService) {
        super();
        this.supplierService = supplierService;
        setConfig();
    }
    @Override
    protected void configurateFilter(SearchFilter filter) {
        filter.addFilter(String.class, "name", "Name", null,null, "", false);
        filter.addFilter(String.class, "contactInfo", "Contact Info", null,null, "", false);
        filter.addFilter(String.class, "address", "Address", null,null, "", false);
        filter.addFilter(String.class, "email", "Email", null,null, "", false);
        filter.addFilter(String.class, "phoneNumber", "Phone Number", null,null, "", false);
    }

    @Override
    protected void addSecurity() {

    }

    @Override
    protected void configGrid(Grid<Supplier> grid) {
        grid.addColumn(Supplier::getName).setHeader("Name").setSortable(true);
        grid.addColumn(Supplier::getContactInfo).setHeader("Contact Info").setSortable(true);
        grid.addColumn(Supplier::getAddress).setHeader("Address").setSortable(true);
        grid.addColumn(Supplier::getEmail).setHeader("Email").setSortable(true);
        grid.addColumn(Supplier::getPhoneNumber).setHeader("Phone Number").setSortable(true);
        grid.setSizeFull();

    }

    @Override
    protected CallbackDataProvider.FetchCallback<Supplier, Void> configDataSource() {
        return query -> {
            TextField name = (TextField) searchFilter.getFilter("name");
            TextField contactInfo = (TextField) searchFilter.getFilter("contactInfo");
            TextField address = (TextField) searchFilter.getFilter("address");
            TextField email = (TextField) searchFilter.getFilter("email");
            TextField phoneNumber = (TextField) searchFilter.getFilter("phoneNumber");

            return supplierService.searchSuppliers(name.getValue(), contactInfo.getValue(), address.getValue(), email.getValue(), phoneNumber.getValue(), PageRequest.of(query.getPage(), query.getLimit())).stream();

        };
    }

    @Override
    protected void configButtons() {

    }

    @Override
    protected void deleteRow() {

    }
}
