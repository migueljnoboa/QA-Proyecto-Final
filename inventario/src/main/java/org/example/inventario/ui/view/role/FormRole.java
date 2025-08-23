package org.example.inventario.ui.view.role;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.apache.commons.lang3.StringUtils;
import org.example.inventario.model.entity.security.Permit;
import org.example.inventario.model.entity.security.Role;
import org.example.inventario.service.security.PermitService;
import org.example.inventario.service.security.RoleService;
import org.example.inventario.ui.component.ConfirmWindow;
import org.example.inventario.ui.component.MyErrorNotification;
import org.example.inventario.ui.component.MySuccessNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.LinkedHashSet;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FormRole extends Dialog {

    private RoleService roleService;
    private PermitService permitService;

    private Role saveRole;
    private boolean view;
    private boolean isEdit;

    // Fields
    private TextField name;
    private TextArea description;
    private MultiSelectComboBox<Permit> permits;
    private Checkbox enabled;

    private Button btnSave, btnExit;

    public FormRole() {
        this(new Role(), false);
    }

    // Constructor used by RolePage via applicationContext.getBean(FormRole.class, selectedItem, view)
    public FormRole(Role role, boolean view) {
        this.saveRole = role != null ? role : new Role();
        this.view = view;
        this.isEdit = this.saveRole.getId() != null;

        setCommonProps(view ? "View Role" : (isEdit ? "Edit Role" : "New Role"),
                view ? "role-form-view" : (isEdit ? "role-form-edit" : "role-form-new"));

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
    public void setServices(RoleService roleService, PermitService permitService) {
        this.roleService = roleService;
        this.permitService = permitService;
        // load items for permits
        if (permits != null) {
            permits.setItems(permitService.findAll());
        }
        if (isEdit) fillFields();
    }

    private Component buildWindow() {
        TabSheet tabs = new TabSheet();
        tabs.setSizeUndefined();
        tabs.add(isEdit ? "Role" : "New Role", buildForm());

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
        btnSave.setId("role-form-btn-save");
        btnSave.addClickShortcut(Key.F10);
        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
        btnSave.addClickListener(e -> saveChanges());

        btnExit = new Button("Exit (ESC)");
        btnExit.setId("role-form-btn-exit");
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
        name = new TextField("Role Name");
        name.setId("role-form-name");
        name.setRequired(true);
        name.setRequiredIndicatorVisible(true);
        name.setWidthFull();

        description = new TextArea("Description");
        description.setId("role-form-description");
        description.setWidthFull();
        description.setMaxLength(500);

        permits = new MultiSelectComboBox<>("Permits");
        permits.setId("role-form-permits");
        permits.setItemLabelGenerator(Permit::getName);
        permits.setClearButtonVisible(true);
        // items loaded in setServices()

        enabled = new Checkbox("Enabled");
        enabled.setId("role-form-enabled");
        enabled.setValue(Boolean.TRUE);

        // Row1: name, enabled
        HorizontalLayout row1 = new HorizontalLayout(name, enabled);
        row1.setWidthFull();
        row1.setFlexGrow(1, name);

        // Row2: description
        HorizontalLayout row2 = new HorizontalLayout(description);
        row2.setWidthFull();
        row2.setFlexGrow(1, description);

        // Row3: permits
        HorizontalLayout row3 = new HorizontalLayout(permits);
        row3.setWidthFull();
        row3.setFlexGrow(1, permits);

        VerticalLayout main = new VerticalLayout(row1, row2, row3);
        main.setSpacing(true);
        main.setPadding(true);
        main.setWidthFull();
        main.setId("role-form-body");

        return main;
    }

    private void saveChanges() {
        if (!validate()) return;

        try {
            loadComponents();

            boolean update = (saveRole.getId() != null);
            if (update) {
                roleService.updateRole(saveRole.getId(), saveRole);
            } else {
                roleService.createRole(saveRole);
            }

            new MySuccessNotification(
                    "Role " + (update ? "updated" : "created") + " successfully: " + saveRole.getName()
            ).open();

            close();
        } catch (Exception e) {
            new MyErrorNotification(e.getMessage()).open();
        }
    }

    private boolean validate() {
        boolean ok = true;
        if (name.isRequired() && StringUtils.isBlank(name.getValue())) {
            ok = false; name.setInvalid(true);
        } else name.setInvalid(false);

        if (permits.getSelectedItems() == null || permits.getSelectedItems().isEmpty()) {
            ok = false; permits.setInvalid(true);
        } else permits.setInvalid(false);

        return ok;
    }

    private void loadComponents() {
        saveRole.setName(name.getValue());
        saveRole.setDescription(StringUtils.trimToNull(description.getValue()));
        saveRole.setPermits(new LinkedHashSet<>(permits.getSelectedItems()));
        saveRole.setEnabled(enabled.getValue() != null ? enabled.getValue() : true);
    }

    private void fillFields() {
        name.setValue(StringUtils.defaultString(saveRole.getName(), ""));
        description.setValue(StringUtils.defaultString(saveRole.getDescription(), ""));
        enabled.setValue(saveRole.isEnabled());
        // set permits items (ensure setServices ran)
        if (saveRole.getPermits() != null) {
            permits.select(saveRole.getPermits());
        }
    }

    private void disableFields() {
        name.setReadOnly(true);
        description.setReadOnly(true);
        permits.setReadOnly(true);
        enabled.setReadOnly(true);
        btnSave.setVisible(false);
    }
}
