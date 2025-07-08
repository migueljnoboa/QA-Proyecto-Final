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
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.server.streams.InMemoryUploadHandler;
import com.vaadin.flow.server.streams.UploadHandler;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.example.inventario.model.entity.inventory.Category;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.ui.component.ConfirmWindow;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FormProduct extends Dialog {

    private Product saveProduct;

    private TextField name;

    private TextArea description;

    private ComboBox<Category> category;

    private BigDecimalField price;

    private IntegerField stock, minStock;

    private ComboBox<Supplier> supplier;

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
//            guardarCambios();
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
        name = new TextField("Product Name");
        name.setRequired(true);
        name.setRequiredIndicatorVisible(true);
        name.setWidthFull();

        category = new ComboBox<>("Category");
        category.setRequired(true);
        category.setRequiredIndicatorVisible(true);
        category.setWidthFull();
        category.setItems(Category.values());
        category.setItemLabelGenerator(Category::toString);

        price = new BigDecimalField("Price ($)");
        price.setRequired(true);
        price.setRequiredIndicatorVisible(true);
        price.setPrefixComponent(new Span("$"));

        stock = new IntegerField("Stock");
        stock.setRequired(true);
        stock.setRequiredIndicatorVisible(true);
        stock.setMin(0);
        stock.setStepButtonsVisible(true);

        minStock = new IntegerField("Min Stock");
        minStock.setRequired(true);
        minStock.setRequiredIndicatorVisible(true);
        minStock.setMin(0);
        minStock.setStepButtonsVisible(true);

        supplier = new ComboBox<>("Supplier");
        supplier.setRequired(true);
        supplier.setRequiredIndicatorVisible(true);
        supplier.setWidthFull();
        // Configurar el ComboBox para mostrar el nombre del proveedor
        supplier.setItemLabelGenerator(Supplier::getName);

        description = new TextArea("Description");
        description.setWidthFull();
        description.setHeight("100px");
        description.setMaxLength(500);
        description.setValueChangeMode(ValueChangeMode.EAGER);

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
        row1.add(name, category);
        row1.setFlexGrow(2, name);
        row1.setFlexGrow(1, category);

        HorizontalLayout row2 = new HorizontalLayout();
        row2.setWidthFull();
        row2.add(price, stock, minStock);
        row2.setFlexGrow(1, price);
        row2.setFlexGrow(1, stock);
        row2.setFlexGrow(1, minStock);

        HorizontalLayout row3 = new HorizontalLayout();
        row3.setWidthFull();
        row3.add(supplier);

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
        mainLayout.add(row1, row2, row3, imageSection, description);

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
        Upload upload = new Upload(inMemoryHandler);
        return upload;
    }


}
