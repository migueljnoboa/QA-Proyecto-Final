package org.example.inventario.ui.view.product;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.Category;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.model.entity.security.Permit;
import org.example.inventario.service.inventory.ProductService;
import org.example.inventario.service.inventory.SupplierService;
import org.example.inventario.service.security.SecurityService;
import org.example.inventario.ui.component.*;
import org.example.inventario.ui.view.MainLayout;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;

@Route(value ="/product", layout = MainLayout.class)
@PageTitle("Product")
@RolesAllowed({Permit.PRODUCTS_MENU})
@Menu(order = 1, icon = "vaadin:package", title = "Product")
public class ProductPage extends ControlPanel<Product> {

    private ProductService productService;
    private SupplierService supplierService;
    private SecurityService securityService;
    private ApplicationContext applicationContext;


    public ProductPage(ApplicationContext applicationContext,ProductService productService, SecurityService securityService, SupplierService supplierService) {
        super();
        this.applicationContext = applicationContext;
        this.productService = productService;
        this.securityService = securityService;
        this.supplierService = supplierService;

        setId("prod-page");
        searchFilter.setId("prod-filter");
        grid.setId("prod-grid");

        setConfig();
    }

    @Override
    protected void configurateFilter(SearchFilter filter) {
        filter.addFilter(String.class, "name", "Name", null,null, "", false);
        filter.addFilter(Category.class, "category", "Category", Arrays.asList(Category.values()), null, null, false);
        filter.addFilter(BigDecimal.class, "price", "Price", null, null, BigDecimal.ZERO, false);
        filter.addFilter(Integer.class, "stock", "Stock", null, null, 0, false);
        filter.addFilter(Integer.class, "minStock", "Min Stock", null, null, 0, false);

        DataProvider<Supplier, String> supplierProvider = DataProvider.fromFilteringCallbacks(
                q -> {
                    PageRequest pr = PageRequest.of(q.getOffset() / q.getLimit(), q.getLimit());
                    ReturnList<Supplier> rl = supplierService.getAllSuppliers(pr);
                    return rl.getData().stream();
                },
                q -> {
                    PageRequest pr = PageRequest.of(0, 1);
                    return (int) supplierService.getAllSuppliers(pr).getTotalElements();
                }
        );
        filter.addFilter(Supplier.class, "supplier", "Supplier", null, supplierProvider::fetch, null, false);

        @SuppressWarnings("unchecked")
        ComboBox<Supplier> suppliersBox = (ComboBox<Supplier>) filter.getFilter("supplier");
        if (suppliersBox != null) {
            suppliersBox.setItemLabelGenerator(Supplier::getName);
            suppliersBox.setClearButtonVisible(true);
            suppliersBox.setWidth("280px");
        }
    }

    @Override
    protected void addSecurity() {
        btnNew.setVisible(securityService.getAuthenticatedUser().getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_"+Permit.PRODUCT_CREATE)));
        btnEdit.setVisible(securityService.getAuthenticatedUser().getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_"+Permit.PRODUCT_EDIT)));
        btnView.setVisible(securityService.getAuthenticatedUser().getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_"+Permit.PRODUCT_VIEW)));
        btnCancel.setVisible(securityService.getAuthenticatedUser().getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_"+Permit.PRODUCT_DELETE)));

        btnNew.setId("prod-btn-new");
        btnEdit.setId("prod-btn-edit");
        btnView.setId("prod-btn-view");
        btnCancel.setId("prod-btn-delete");
    }

    @Override
    protected void configGrid(Grid<Product> grid) {
        grid.addColumn(Product::getName).setHeader("Name").setSortable(true);
        grid.addColumn(Product::getDescription).setHeader("Description").setSortable(true);
        grid.addColumn(product -> product.getCategory().name()).setHeader("Category").setSortable(true);
        grid.addColumn(Product::getPrice).setHeader("Price").setSortable(true);
        grid.addColumn(Product::getStock).setHeader("Stock").setSortable(true);
        grid.addColumn(Product::getMinStock).setHeader("Min Stock").setSortable(true);
        grid.addColumn(product -> product.getSupplier() != null ? product.getSupplier().getName() : "No Supplier").setHeader("Supplier").setSortable(true);

    }

    @Override
    protected CallbackDataProvider.FetchCallback<Product, Void> configDataSource() {

        return query -> {
            TextField name = (TextField) searchFilter.getFilter("name");
            BigDecimalField price = (BigDecimalField) searchFilter.getFilter("price");
            IntegerField minStock = (IntegerField) searchFilter.getFilter("minStock");
            IntegerField stock = (IntegerField) searchFilter.getFilter("stock");
            ComboBox<Category> category = (ComboBox<Category>) searchFilter.getFilter("category");
            ComboBox<Supplier> supplier = (ComboBox<Supplier>) searchFilter.getFilter("supplier");

            return productService.searchProducts(name.getValue(), category.getValue(), price.getValue(), minStock.getValue(), stock.getValue(), supplier.getValue(), PageRequest.of(query.getPage(), query.getLimit())).stream();
        };

    }

    @Override
    protected void configButtons() {
        btnNew.addClickListener(event -> {
            FormProduct form = applicationContext.getBean(FormProduct.class);
            form.addDetachListener(event1 -> fillGrid());
            form.open();
        });
        btnView.addClickListener(event -> {
            FormProduct form = applicationContext.getBean(FormProduct.class, selectedItem, true);
            form.addDetachListener(event1 -> fillGrid());
            form.open();
        });
        btnEdit.addClickListener(event -> {
            FormProduct form = applicationContext.getBean(FormProduct.class, selectedItem, false);
            form.addDetachListener(event1 -> fillGrid());
            form.open();
        });
        btnCancel.addClickListener(event -> {
            deleteRow();
        });
    }

    @Override
    protected void deleteRow() {
        ConfirmWindow ventanaConfirmacion = new ConfirmWindow("Confirm Action", "Are you sure you want to delete this producta: " + selectedItem.getDescription() + "?", () -> {
            try {
                productService.deleteProduct(selectedItem.getId());
                String mensaje = "Product " + selectedItem.getName() + " deleted successfully.";
                MySuccessNotification miSuccessNotification = new MySuccessNotification(mensaje);
                miSuccessNotification.open();

                fillGrid();
            } catch (Exception e) {
                MyErrorNotification miErrorNotification = new MyErrorNotification(e.getMessage());
                miErrorNotification.open();
            }
        });
        ventanaConfirmacion.open();

    }
}
