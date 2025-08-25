package org.example.inventario.ui.view.supplier;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.apache.commons.lang3.StringUtils;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.service.inventory.SupplierService;
import org.example.inventario.ui.component.ConfirmWindow;
import org.example.inventario.ui.component.MyErrorNotification;
import org.example.inventario.ui.component.MySuccessNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FormSupplier extends Dialog {

    private SupplierService supplierService;

    private Supplier saveSupplier;
    private boolean view;
    private boolean isEdit;

    private TextField name;
    private TextField contactInfo;
    private TextField address;
    private EmailField email;
    private TextField phoneNumber;

    private Button btnSave, btnExit;

    public FormSupplier() {
        this(new Supplier(), false);
    }

    // Constructor used by SupplierPage via applicationContext.getBean(FormSupplier.class, selectedItem, view)
    public FormSupplier(Supplier supplier, boolean view) {
        this.saveSupplier = supplier != null ? supplier : new Supplier();
        this.view = view;
        this.isEdit = this.saveSupplier.getId() != null;

        setCommonProps(view ? "View Supplier" : (isEdit ? "Edit Supplier" : "New Supplier"),
                view ? "sup-form-view" : (isEdit ? "sup-form-edit" : "sup-form-new"));

        add(buildWindow());
        if (view) disableFields();
    }

    private void setCommonProps(String title, String id) {
        setHeaderTitle(title);
        setId(id);
        setModal(false);
        setDraggable(true);
        addThemeVariants(DialogVariant.LUMO_NO_PADDING);
    }

    @Autowired
    public void setServices(SupplierService supplierService) {
        this.supplierService = supplierService;
        if (isEdit) fillFields();
    }

    private Component buildWindow() {
        TabSheet tabs = new TabSheet();
        tabs.setSizeUndefined();
        tabs.add(isEdit ? "Supplier" : "New Supplier", buildForm());

        VerticalLayout root = new VerticalLayout();
        root.setMargin(false);
        root.setPadding(true);
        root.setSpacing(false);
        root.setSizeUndefined();
        root.add(tabs);

        buildFooterButtons();
        return root;
    }

    private void buildFooterButtons() {
        btnSave = new Button("Save (F10)");
        btnSave.setId("sup-form-btn-save");
        btnSave.addClickShortcut(Key.F10);
        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
        btnSave.addClickListener(e -> saveChanges());

        btnExit = new Button("Exit (ESC)");
        btnExit.setId("sup-form-btn-exit");
        btnExit.addClickShortcut(Key.ESCAPE);
        btnExit.addThemeVariants(ButtonVariant.LUMO_SMALL);
        btnExit.addClickListener(e -> {
            if (!view) {
                ConfirmWindow cw = new ConfirmWindow("Action Confirmation", "Are you sure to continue?", this::close);
                cw.open();
            } else {
                close();
            }
        });

        if (view) btnSave.setVisible(false);

        getFooter().add(btnExit, btnSave);
    }

    private Component buildForm() {
        name = new TextField("Supplier Name");
        name.setId("sup-form-name");
        name.setRequired(true);
        name.setRequiredIndicatorVisible(true);
        name.setWidthFull();

        contactInfo = new TextField("Contact Info");
        contactInfo.setId("sup-form-contact");
        contactInfo.setRequired(true);
        contactInfo.setRequiredIndicatorVisible(true);
        contactInfo.setWidthFull();

        email = new EmailField("Supplier Email");
        email.setId("sup-form-email");
        email.setRequired(true);
        email.setRequiredIndicatorVisible(true);
        email.setWidthFull();

        address = new TextField("Supplier Address");
        address.setId("sup-form-address");
        address.setRequired(true);
        address.setRequiredIndicatorVisible(true);
        address.setWidthFull();

        phoneNumber = new TextField("Supplier Phone");
        phoneNumber.setId("sup-form-phone");
        phoneNumber.setRequired(true);
        phoneNumber.setRequiredIndicatorVisible(true);
        phoneNumber.setWidthFull();

        // Row 1: name, contact, email
        HorizontalLayout row1 = new HorizontalLayout(name, contactInfo, email);
        row1.setWidthFull();
        row1.setFlexGrow(1, name);
        row1.setFlexGrow(1, contactInfo);
        row1.setFlexGrow(1, email);

        // Row 2: address, phone
        HorizontalLayout row2 = new HorizontalLayout(address, phoneNumber);
        row2.setWidthFull();
        row2.setFlexGrow(2, address);
        row2.setFlexGrow(1, phoneNumber);

        VerticalLayout main = new VerticalLayout(row1, row2);
        main.setSpacing(true);
        main.setPadding(true);
        main.setWidthFull();
        main.setId("sup-form-body");

        return main;
    }

    private void saveChanges() {
        if (!validate()) return;

        try {
            loadComponents();

            boolean update = (saveSupplier.getId() != null);
            if (update) {
                supplierService.updateSupplier(saveSupplier.getId(), saveSupplier);
            } else {
                supplierService.createSupplier(saveSupplier);
            }

            new MySuccessNotification(
                    "Supplier " + (update ? "updated" : "created") + " successfully: " + saveSupplier.getName()
            ).open();

            close();
        } catch (Exception e) {
            new MyErrorNotification(e.getMessage()).open();
        }
    }

    private boolean validate() {
        boolean ok = true;

        if (name.isRequired() && StringUtils.isBlank(name.getValue())) { ok = false; name.setInvalid(true); } else name.setInvalid(false);
        if (contactInfo.isRequired() && StringUtils.isBlank(contactInfo.getValue())) { ok = false; contactInfo.setInvalid(true); } else contactInfo.setInvalid(false);
        if (address.isRequired() && StringUtils.isBlank(address.getValue())) { ok = false; address.setInvalid(true); } else address.setInvalid(false);
        if (email.isRequired() && StringUtils.isBlank(email.getValue())) { ok = false; email.setInvalid(true); } else email.setInvalid(false);
        if (phoneNumber.isRequired() && StringUtils.isBlank(phoneNumber.getValue())) { ok = false; phoneNumber.setInvalid(true); } else phoneNumber.setInvalid(false);

        return ok;
    }

    private void loadComponents() {
        saveSupplier.setName(name.getValue());
        saveSupplier.setContactInfo(contactInfo.getValue());
        saveSupplier.setAddress(address.getValue());
        saveSupplier.setEmail(email.getValue());
        saveSupplier.setPhoneNumber(phoneNumber.getValue());
    }

    private void fillFields() {
        name.setValue(StringUtils.defaultString(saveSupplier.getName(), ""));
        contactInfo.setValue(StringUtils.defaultString(saveSupplier.getContactInfo(), ""));
        address.setValue(StringUtils.defaultString(saveSupplier.getAddress(), ""));
        email.setValue(StringUtils.defaultString(saveSupplier.getEmail(), ""));
        phoneNumber.setValue(StringUtils.defaultString(saveSupplier.getPhoneNumber(), ""));
    }

    private void disableFields() {
        name.setReadOnly(true);
        contactInfo.setReadOnly(true);
        address.setReadOnly(true);
        email.setReadOnly(true);
        phoneNumber.setReadOnly(true);
        btnSave.setVisible(false);
    }
}
