package org.example.inventario.ui.view.supplier;

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
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.server.streams.UploadHandler;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.example.inventario.model.entity.inventory.Category;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.ui.component.ConfirmWindow;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FormSupplier extends Dialog {

    private Supplier saveSupplier;

    private TextField name;

    private TextField contactInfo;

    private TextField address;

    private EmailField email;

    private TextField phoneNumber;

    private Button btnSave, btnExit;

    private boolean view = false;


    public FormSupplier() {
        this.saveSupplier = new Supplier();
        setHeaderTitle("Nuevo Suplidor");
        setId("FORM-SUPPLIER");
        setModal(false);
        setDraggable(true);
        addThemeVariants(DialogVariant.LUMO_NO_PADDING);

        add(buildWindow());
    }

    private Component buildWindow() {
        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeUndefined();
        tabSheet.add("New Supplier", buildTabNewSupplier());
        if (saveSupplier.getId() != null && saveSupplier.getId() > 0) {
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
                ConfirmWindow confirmWindow = new ConfirmWindow("Action Confirmation", "Are you sure to continue?", this::close);
                confirmWindow.open();
            } else {
                close();
            }
        });

        getFooter().add(btnExit);
        getFooter().add(btnSave);
    }

    private Component buildTabNewSupplier() {
        name = new TextField("Nombre del Suplidor");
        name.setRequired(true);
        name.setRequiredIndicatorVisible(true);
        name.setWidthFull();

        contactInfo = new TextField("Info de Contacto");
        contactInfo.setRequired(true);
        contactInfo.setRequiredIndicatorVisible(true);
        contactInfo.setWidthFull();

        address = new TextField("Dirección del Suplidor");
        address.setRequired(true);
        address.setRequiredIndicatorVisible(true);
        address.setWidthFull();

        email = new EmailField("Correo de Suplidor");
        email.setRequired(true);
        email.setRequiredIndicatorVisible(true);
        email.setWidthFull();

        phoneNumber = new TextField("Numero de Suplidor");
        phoneNumber.setRequired(true);
        phoneNumber.setRequiredIndicatorVisible(true);
        phoneNumber.setWidthFull();

        // Crear los layouts para organizar los campos
        HorizontalLayout row1 = new HorizontalLayout();
        row1.setWidthFull();
        row1.add(name, contactInfo, address, email, phoneNumber);
        row1.setFlexGrow(1, name);
        row1.setFlexGrow(1, contactInfo);
        row1.setFlexGrow(1, email);

        HorizontalLayout row2 = new HorizontalLayout();
        row2.setWidthFull();
        row2.add(address, phoneNumber);
        row2.setFlexGrow(2, address);
        row2.setFlexGrow(1, phoneNumber);

        // Layout principal del tab
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setPadding(true);
        mainLayout.setWidthFull();
        mainLayout.add(row1, row2);

        return mainLayout;
    }


}
