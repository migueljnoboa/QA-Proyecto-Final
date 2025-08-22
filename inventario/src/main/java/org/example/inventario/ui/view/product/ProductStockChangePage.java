package org.example.inventario.ui.view.product;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.inventory.ProductStockChange;
import org.example.inventario.service.inventory.ProductService;
import org.example.inventario.service.inventory.ProductStockChangeService;
import org.example.inventario.ui.component.ControlPanel;
import org.example.inventario.ui.component.SearchFilter;
import org.example.inventario.ui.view.MainLayout;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

@Route(value = "/productStockChange", layout = MainLayout.class)
@PageTitle("Product Stock Changes")
@RolesAllowed({"PRODUCTS_MENU"})
@Menu(order = 1, icon = "vaadin:history", title = "Stock Changes")
public class ProductStockChangePage extends ControlPanel<ProductStockChange> {

    private final ProductStockChangeService productStockChangeService;
    private final ProductService productService;

    ApplicationContext applicationContext;

    public ProductStockChangePage(
            ApplicationContext applicationContext,
            ProductStockChangeService productStockChangeService,
            ProductService productService
    ) {
        super();
        this.applicationContext = applicationContext;
        this.productStockChangeService = productStockChangeService;
        this.productService = productService;
        setConfig();
    }

    @Override
    protected void configurateFilter(SearchFilter filter) {
        // Product (ComboBox with paging; label generator set in the field itself below)
        filter.addFilter(
                Product.class, "product", "Product",
                null,
                q -> productService.searchProducts(
                        q.getFilter().orElse(null),
                        null,
                        null,
                        null,
                        null,
                        PageRequest.of(q.getPage(), q.getPageSize())
                ).stream(),
                null,
                false
        );
        // optional: set item label generator on the created ComboBox
        @SuppressWarnings("unchecked")
        ComboBox<Product> productCb = (ComboBox<Product>) filter.getFilter("product");
        if (productCb != null) {
            productCb.setItemLabelGenerator(Product::getName);
            productCb.setClearButtonVisible(true);
            productCb.setWidth("280px");
        }

        // Increased? (tri-state; null = ANY)
        filter.addFilter(Boolean.class, "increased", "Increased?",
                java.util.List.of(Boolean.TRUE, Boolean.FALSE), null, null, false);

        // Amount range
        filter.addIntegerRange("amount", "Min amount", "Max amount");

        // Date range (LocalDate) => creates "datestart" and "dateend"
        filter.addFilter(java.time.LocalDate.class, "date", "From date", null, null, null, false);

        // Created by
        filter.addFilter(String.class, "createdBy", "Created by", null, null, "", false);
    }

    @Override
    protected void addSecurity() {
        btnNew.setVisible(false);
        btnEdit.setVisible(false);
        btnView.setVisible(false);
        btnCancel.setVisible(false);
        btnPrint.setVisible(false);
    }

    @Override
    protected void configGrid(Grid<ProductStockChange> grid) {
        grid.addColumn(sc -> sc.getProduct() != null ? sc.getProduct().getName() : "No Product")
                .setHeader("Product");
        grid.addColumn(ProductStockChange::isIncreased)
                .setHeader("Increased");
        grid.addColumn(ProductStockChange::getAmount)
                .setHeader("Amount");
        grid.addColumn(ProductStockChange::getDate)
                .setHeader("Date");
        grid.addColumn(ProductStockChange::getCreatedBy)
                .setHeader("User");
    }

    @Override
    protected CallbackDataProvider.FetchCallback<ProductStockChange, Void> configDataSource() {
        return query -> {
            // Product
            @SuppressWarnings("unchecked")
            ComboBox<Product> productCb = (ComboBox<Product>) searchFilter.getFilter("product");
            Long productId = productCb != null && productCb.getValue() != null ? productCb.getValue().getId() : null;

            // Increased
            @SuppressWarnings("unchecked")
            ComboBox<Boolean> increasedCb = (ComboBox<Boolean>) searchFilter.getFilter("increased");
            Boolean increased = increasedCb != null ? increasedCb.getValue() : null;

            // Amount range
            IntegerField minAmountField = (IntegerField) searchFilter.getFilter("amountMin");
            IntegerField maxAmountField = (IntegerField) searchFilter.getFilter("amountMax");
            Integer minAmount = minAmountField != null ? minAmountField.getValue() : null;
            Integer maxAmount = maxAmountField != null ? maxAmountField.getValue() : null;

            if (minAmount != null && maxAmount != null && minAmount > maxAmount) {
                int tmp = minAmount; minAmount = maxAmount; maxAmount = tmp; // be nice to users
            }

            // Dates (inclusive end-of-day)
            DatePicker fromDp = (DatePicker) searchFilter.getFilter("datestart");
            DatePicker toDp   = (DatePicker) searchFilter.getFilter("dateend");
            LocalDateTime fromDate = (fromDp != null && fromDp.getValue() != null)
                    ? fromDp.getValue().atStartOfDay() : null;
            LocalDateTime toDate = (toDp != null && toDp.getValue() != null)
                    ? toDp.getValue().atTime(23, 59, 59, 999_999_999) : null;

            // Created by
            TextField createdByTf = (TextField) searchFilter.getFilter("createdBy");
            String createdBy = (createdByTf != null && createdByTf.getValue() != null && !createdByTf.getValue().isBlank())
                    ? createdByTf.getValue().trim() : null;

            Page<ProductStockChange> page = productStockChangeService.searchProductStockChanges(
                    PageRequest.of(query.getPage(), query.getLimit()),
                    productId,
                    increased,
                    minAmount,
                    maxAmount,
                    fromDate,
                    toDate,
                    createdBy
            );

            return page.stream();
        };
    }

    @Override
    protected void configButtons() {
        // No CRUD actions on this page.
    }

    @Override
    protected void deleteRow() {
        // Not applicable
    }
}
