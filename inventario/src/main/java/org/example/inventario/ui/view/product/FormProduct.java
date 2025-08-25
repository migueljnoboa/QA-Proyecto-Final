package org.example.inventario.ui.view.product;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.server.streams.InMemoryUploadHandler;
import com.vaadin.flow.server.streams.UploadHandler;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.apache.commons.lang3.StringUtils;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.Category;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.service.inventory.ProductService;
import org.example.inventario.service.inventory.SupplierService;
import org.example.inventario.ui.component.ConfirmWindow;
import org.example.inventario.ui.component.MyErrorNotification;
import org.example.inventario.ui.component.MySuccessNotification;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Base64;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FormProduct extends Dialog {

    private ProductService productService;
    private SupplierService supplierService;

    private Product saveProduct;

    private TextField tfName;

    private TextArea taDescription;

    private ComboBox<Category> cbCategory;

    private BigDecimalField bdFPrice;

    private IntegerField ifStock, ifminStock;

    private ComboBox<Supplier> cbSupplier;

    private String image;

    private Upload upload;
    
    private Button btnSave, btnExit;

    private boolean view = false;


    public FormProduct() {

        setId("prod-form");

        this.saveProduct = new Product();
        setHeaderTitle("New Product");
        setId("FORM-PRODUCT");
        setModal(false);
        setDraggable(true);
        addThemeVariants(DialogVariant.LUMO_NO_PADDING);

        add(buildWindow());
    }
    public FormProduct(Product product, boolean view) {
        this.saveProduct = product;
        setHeaderTitle("Edit Product");
        setId("FORM-PRODUCT");
        setModal(false);
        setDraggable(true);
        addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        add(buildWindow());
        if (view) {
            this.setHeaderTitle("View Product");
            this.setId("FORM-PRODUCT-VIEW");
            disableFields();
        } else {
            this.setHeaderTitle("Edit Product");
            this.setId("FORM-PRODUCT-EDIT");
        }
    }

    @Autowired
    public void setServices(ProductService productService, SupplierService supplierService) {
        this.productService = productService;
        this.supplierService = supplierService;
        if (saveProduct.getId() != null && saveProduct.getId() > 0L) {
            fillFields();
        }
    }

    private Component buildWindow() {
        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeUndefined();
        tabSheet.add("New Product", buildTabNewProduct());
        if (saveProduct.getId() != null && saveProduct.getId() > 0) {
//            tabSheet.add("Security", construirTabSeguridad());
        }

        VerticalLayout layoutVentana = new VerticalLayout();
        layoutVentana.setMargin(false);
        layoutVentana.setPadding(true);
        layoutVentana.setSpacing(false);
        layoutVentana.setSizeUndefined();
        layoutVentana.add(tabSheet);

        construirLayoutBotones();

        return layoutVentana;
    }

    private void construirLayoutBotones() {

        btnSave = new Button("Save (F10)");
        btnSave.addClickShortcut(Key.F10);
        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
        btnSave.addClickListener(event -> {
            saveChanges(saveProduct.getId() != null && saveProduct.getId() > 0L);
        });

        btnExit = new Button("Exit (ESC)");
        btnExit.addClickShortcut(Key.ESCAPE);
        btnExit.addThemeVariants(ButtonVariant.LUMO_SMALL);
        btnExit.addClickListener(event -> {
            if (!view) {
                ConfirmWindow confirmWindow = new ConfirmWindow("Action Confirmation", "Are you sure you want to continue?", this::close);
                confirmWindow.open();
            } else {
                close();
            }
        });

        btnSave.setId("prod-form-btn-save");
        btnExit.setId("prod-form-btn-exit");

        getFooter().add(btnExit);
        getFooter().add(btnSave);
    }

    private Component buildTabNewProduct() {

        tfName = new TextField("Product Name");
        tfName.setRequired(true);
        tfName.setRequiredIndicatorVisible(true);
        tfName.setWidthFull();

        cbCategory = new ComboBox<>("Category");
        cbCategory.setRequired(true);
        cbCategory.setRequiredIndicatorVisible(true);
        cbCategory.setWidthFull();
        cbCategory.setItems(Category.values());
        cbCategory.setItemLabelGenerator(Category::toString);

        bdFPrice = new BigDecimalField("Price ($)");
        bdFPrice.setRequired(true);
        bdFPrice.setRequiredIndicatorVisible(true);
        bdFPrice.setPrefixComponent(new Span("$"));

        ifStock = new IntegerField("Stock");
        ifStock.setRequired(true);
        ifStock.setRequiredIndicatorVisible(true);
        ifStock.setMin(0);
        ifStock.setStepButtonsVisible(true);

        ifminStock = new IntegerField("Min Stock");
        ifminStock.setRequired(true);
        ifminStock.setRequiredIndicatorVisible(true);
        ifminStock.setMin(0);
        ifminStock.setStepButtonsVisible(true);

        cbSupplier = new ComboBox<>("Supplier");
        cbSupplier.setRequired(true);
        cbSupplier.setRequiredIndicatorVisible(true);
        cbSupplier.setWidthFull();
        cbSupplier.setItemLabelGenerator(Supplier::getName);
        DataProvider<Supplier, String> dataProvider = DataProvider.fromFilteringCallbacks(
                query -> {
                    Pageable pageable = PageRequest.of(
                            query.getOffset() / query.getLimit(),
                            query.getLimit()
                    );

                    ReturnList<Supplier> page = supplierService.getAllSuppliers(pageable);
                    return page.getData().stream();
                },
                query -> {
                    Pageable pageable = PageRequest.of(0, 1);
                    return (int) supplierService.getAllSuppliers(pageable).getTotalElements();
                }
        );
        cbSupplier.setItems(dataProvider);

        taDescription = new TextArea("Description");
        taDescription.setWidthFull();
        taDescription.setHeight("100px");
        taDescription.setMaxLength(500);
        taDescription.setValueChangeMode(ValueChangeMode.EAGER);

        upload = getUpload();
        upload.setDropAllowed(true);

        // Crear layout para mostrar la imagen preview
        VerticalLayout imagePreviewLayout = new VerticalLayout();
        imagePreviewLayout.setSpacing(false);
        imagePreviewLayout.setPadding(false);
        imagePreviewLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        Image imagePreview = new Image();
        imagePreview.setVisible(false);
        imagePreview.setWidth("200px");
        imagePreview.setHeight("150px");
        imagePreview.getStyle().set("border", "1px solid #ccc");
        imagePreview.getStyle().set("border-radius", "4px");

        NativeLabel imageInfo = new NativeLabel("Accepted formats: JPG, PNG, GIF. Max size: 5MB.");
        imageInfo.getStyle().set("font-size", "12px");
        imageInfo.getStyle().set("color", "var(--lumo-secondary-text-color)");

        imagePreviewLayout.add(imagePreview, imageInfo);

        HorizontalLayout row1 = new HorizontalLayout();
        row1.setWidthFull();
        row1.add(tfName, cbCategory);
        row1.setFlexGrow(2, tfName);
        row1.setFlexGrow(1, cbCategory);

        HorizontalLayout row2 = new HorizontalLayout();
        row2.setWidthFull();
        row2.add(bdFPrice, ifStock, ifminStock);
        row2.setFlexGrow(1, bdFPrice);
        row2.setFlexGrow(1, ifStock);
        row2.setFlexGrow(1, ifminStock);

        HorizontalLayout row3 = new HorizontalLayout();
        row3.setWidthFull();
        row3.add(cbSupplier);

        VerticalLayout imageSection = new VerticalLayout();
        imageSection.setSpacing(false);
        imageSection.setPadding(false);
        NativeLabel imageLabel = new NativeLabel("Product Image");
        imageLabel.getStyle().set("font-weight", "bold");
        imageSection.add(imageLabel, upload, imagePreviewLayout);

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setPadding(true);
        mainLayout.setWidthFull();
        if(saveProduct.getId() != null && saveProduct.getId() > 0L) {
            String src = "data:image/png;base64," + saveProduct.getImage();
            Image image = new Image(src, "imagen");
            image.setWidth("200px");
            image.setHeight("150px");
            mainLayout.add(image);
        }
        if(!view ) {
            mainLayout.add(imageSection);
        }

        tfName.setId("prod-form-name");
        cbCategory.setId("prod-form-category");
        bdFPrice.setId("prod-form-price");
        ifStock.setId("prod-form-stock");
        ifminStock.setId("prod-form-minstock");
        cbSupplier.setId("prod-form-supplier");
        taDescription.setId("prod-form-description");
        upload.setId("prod-form-upload");

        mainLayout.add(row1, row2, row3, taDescription);

        return mainLayout;
    }

    @NotNull
    private Upload getUpload() {
        InMemoryUploadHandler inMemoryHandler = UploadHandler.inMemory(
                (metadata, data) -> {
                    String fileName = metadata.fileName();
                    String mimeType = metadata.contentType();
                    long contentLength = metadata.contentLength();

                    image = Base64.getEncoder().encodeToString(data);
                });
        return new Upload(inMemoryHandler);
    }
    private void saveChanges(boolean update) {
        if (!validate()) {
            return;
        }
        try {
            loadComponents();

            if(update) {
                productService.updateProduct(saveProduct);
            }else {
                productService.createProduct(saveProduct);
            }


            MySuccessNotification mySuccessNotification = new MySuccessNotification("Product saved successfully: " + saveProduct.getName());
            mySuccessNotification.open();

            close();
        } catch (Exception e) {
            MyErrorNotification miErrorNotification = new MyErrorNotification(e.getMessage());
            miErrorNotification.open();
        }
    }


    private boolean validate() {
        boolean ok = true;

        if (tfName.isRequired() && StringUtils.isBlank(tfName.getValue())) {
            ok = false;
            tfName.setInvalid(true);
        } else {
            tfName.setInvalid(false);
        }
        if (cbCategory.isRequired() && cbCategory.getValue() == null) {
            ok = false;
            cbCategory.setInvalid(true);
        } else {
            cbCategory.setInvalid(false);
        }
        if (bdFPrice.isRequired() && bdFPrice.getValue() == null) {
            ok = false;
            bdFPrice.setInvalid(true);
        } else {
            bdFPrice.setInvalid(false);
        }
        if (ifStock.isRequired() && ifStock.getValue() == null) {
            ok = false;
            ifStock.setInvalid(true);
        } else {
            ifStock.setInvalid(false);
        }
        if (ifminStock.isRequired() && ifminStock.getValue() == null) {
            ok = false;
            ifminStock.setInvalid(true);
        } else {
            ifminStock.setInvalid(false);
        }

        if (taDescription.isRequired() && StringUtils.isBlank(taDescription.getValue())) {
            ok = false;
            taDescription.setInvalid(true);
        } else {
            taDescription.setInvalid(false);
        }
        return ok;
    }
    private void loadComponents() {
        saveProduct.setName(tfName.getValue());
        saveProduct.setDescription(taDescription.getValue());
        saveProduct.setCategory(cbCategory.getValue());
        saveProduct.setPrice(bdFPrice.getValue());
        saveProduct.setStock(ifStock.getValue());
        saveProduct.setMinStock(ifminStock.getValue());
        saveProduct.setSupplier(cbSupplier.getValue());
        if (StringUtils.isNotBlank(image)) {
            saveProduct.setImage(image);
        } else {
            saveProduct.setImage(null);
        }
        
    }

    private void fillFields(){
        tfName.setValue(saveProduct.getName());
        taDescription.setValue(saveProduct.getDescription());
        cbCategory.setValue(saveProduct.getCategory());
        bdFPrice.setValue(saveProduct.getPrice());
        ifStock.setValue(saveProduct.getStock());
        ifminStock.setValue(saveProduct.getMinStock());
        cbSupplier.setValue(saveProduct.getSupplier());

        if (saveProduct.getImage() != null) {
            try {
                image = saveProduct.getImage();
            } catch (Exception e) {
                image = null;
            }
        } else {
            image = null;
        }
    }

    private void disableFields(){
        tfName.setReadOnly(true);
        taDescription.setReadOnly(true);
        cbCategory.setReadOnly(true);
        bdFPrice.setReadOnly(true);
        ifStock.setReadOnly(true);
        ifminStock.setReadOnly(true);
        cbSupplier.setReadOnly(true);
        upload.setVisible(false);
        btnSave.setVisible(false);
    }


}
