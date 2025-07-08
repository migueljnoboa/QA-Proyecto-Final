package org.example.inventario.ui.view.product;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
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
import java.util.stream.Collectors;

@Route(value ="/product", layout = MainLayout.class)
@PageTitle("Product")
@RolesAllowed({"PRODUCTS_MENU"})
@Menu(order = 1, icon = "vaadin:package", title = "Product")
public class ProductPage extends ControlPanel<Product> {

    private ProductService productService;
    private SupplierService supplierService;


    public ProductPage(ProductService productService, SupplierService supplierService) {
        super();
        this.productService = productService;
        this.supplierService = supplierService;
        setConfig();
    }

    @Override
    protected void configurateFilter(SearchFilter filter) {
        filter.addFilter(String.class, "name", "Name", null,null, "", false);
        filter.addFilter(Category.class, "category", "Category", Arrays.asList(Category.values()), null, null, false);
        filter.addFilter(BigDecimal.class, "price", "Price", null, null, BigDecimal.ZERO, false);
        filter.addFilter(Integer.class, "stock", "Stock", null, null, 0, false);
        filter.addFilter(Integer.class, "minStock", "Min Stock", null, null, 0, false);
//        filter.addFilter(Supplier.class, "supplier", "Supplier", supplierService., null, null, false);

    }

    @Override
    protected void addSecurity() {

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

            return productService.searchProducts(name.getValue(), category.getValue(), price.getValue(), minStock.getValue(), stock.getValue(), PageRequest.of(query.getPage(), query.getLimit())).stream();
        };

    }

    @Override
    protected void configButtons() {

    }

    @Override
    protected void deleteRow() {

    }
}
