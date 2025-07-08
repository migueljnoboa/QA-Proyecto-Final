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

    private byte[] image;
    
    private Button btnSave, btnExit;

    private boolean view = false;


    public FormProduct() {
        this.saveProduct = new Product();
        setHeaderTitle("Nueva Producto");
        setId("FORM-PRODUCTO");
        setModal(false);
        setDraggable(true);
        addThemeVariants(DialogVariant.LUMO_NO_PADDING);

        add(buildWindow());
    }

    @Autowired
    public void setServices(ProductService productService, SupplierService supplierService) {
        this.productService = productService;
        this.supplierService = supplierService;
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
            saveChanges();

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

                    Page<Supplier> page = supplierService.getAllSuppliers(pageable);

                    return page.getContent().stream();
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

        Upload upload = getUpload();
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

//        // Configurar el comportamiento del upload
//        uploadImage.addSucceededListener(event -> {
//            try {
//                InputStream inputStream = memoryBuffer.getInputStream();
//                byte[] imageBytes = inputStream.readAllBytes();
//
//                // Crear URL para preview de la imagen
//                StreamResource resource = new StreamResource(event.getFileName(),
//                        () -> new ByteArrayInputStream(imageBytes));
//                imagePreview.setSrc(resource);
//                imagePreview.setVisible(true);
//
//                // Guardar la imagen en el producto (esto depende de tu implementación)
//                // saveProduct.setImage(imageBytes);
//                // saveProduct.setImageName(event.getFileName());
//
//            } catch (IOException e) {
//                Notification.show("Error al cargar la imagen: " + e.getMessage(),
//                                3000, Notification.Position.MIDDLE)
//                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
//            }
//        });

//        uploadImage.addFileRejectedListener(event -> {
//            Notification.show("Archivo rechazado: " + event.getErrorMessage(),
//                            3000, Notification.Position.MIDDLE)
//                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
//        });

        // Crear los layouts para organizar los campos
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

        // Layout para la sección de imagen
        VerticalLayout imageSection = new VerticalLayout();
        imageSection.setSpacing(false);
        imageSection.setPadding(false);
        NativeLabel imageLabel = new NativeLabel("Product Image");
        imageLabel.getStyle().set("font-weight", "bold");
        imageSection.add(imageLabel, upload, imagePreviewLayout);

        // Layout principal del tab
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setPadding(true);
        mainLayout.setWidthFull();
        mainLayout.add(row1, row2, row3, imageSection, taDescription);

        return mainLayout;
    }

    @NotNull
    private Upload getUpload() {
        InMemoryUploadHandler inMemoryHandler = UploadHandler.inMemory(
                (metadata, data) -> {
                    String fileName = metadata.fileName();
                    String mimeType = metadata.contentType();
                    long contentLength = metadata.contentLength();
                    image = data;
                });
        return new Upload(inMemoryHandler);
    }
    private void saveChanges() {
        if (!validate()) {
            return;
        }
        try {
            loadComponents();

            productService.createProduct(saveProduct);


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

        if (image != null && image.length > 0) {
            saveProduct.setImage(Arrays.toString(image));
        } else {
            saveProduct.setImage(null);
        }
        
    }


}
