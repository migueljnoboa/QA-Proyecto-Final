package org.example.inventario.ui.view.user;

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
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.apache.commons.lang3.StringUtils;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.security.Role;
import org.example.inventario.model.entity.security.User;
import org.example.inventario.service.security.RoleService;
import org.example.inventario.service.security.UserService;
import org.example.inventario.ui.component.ConfirmWindow;
import org.example.inventario.ui.component.MyErrorNotification;
import org.example.inventario.ui.component.MySuccessNotification;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;

import java.util.LinkedHashSet;
import java.util.Set;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FormUser extends Dialog {

    private UserService userService;
    private RoleService roleService;

    private User saveUser;
    private final boolean view;
    private final boolean isEdit;

    private TextField tfUsername;
    private EmailField tfEmail;
    private PasswordField pfPassword;
    private PasswordField pfConfirm;
    private MultiSelectComboBox<Role> msRoles;
    private Checkbox cbEnabled;

    private Button btnSave, btnExit;

    public FormUser() {
        this(new User(), false);
    }

    public FormUser(@Nullable User user, boolean view) {
        this.saveUser = (user != null ? user : new User());
        this.view = view;
        this.isEdit = this.saveUser.getId() != null;

        setHeaderTitle(view ? "View User" : (isEdit ? "Edit User" : "New User"));
        setId(view ? "user-form-view" : (isEdit ? "user-form-edit" : "user-form-new"));
        setModal(false);
        setDraggable(true);
        addThemeVariants(DialogVariant.LUMO_NO_PADDING);

        add(buildWindow());

        if (view) disableFields();
    }

    @Autowired
    public void setServices(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
        if (isEdit) fillFields();
    }

    private Component buildWindow() {
        TabSheet tabs = new TabSheet();
        tabs.setSizeUndefined();
        tabs.add(isEdit ? "User" : "New User", buildForm());

        VerticalLayout root = new VerticalLayout();
        root.setMargin(false);
        root.setPadding(true);
        root.setSpacing(false);
        root.setSizeUndefined();
        root.add(tabs);

        buildFooterButtons();

        return root;
    }

    private Component buildForm() {
        tfUsername = new TextField("Username");
        tfUsername.setId("user-form-username");
        tfUsername.setRequired(true);
        tfUsername.setRequiredIndicatorVisible(true);
        tfUsername.setWidthFull();

        tfEmail = new EmailField("Email");
        tfEmail.setId("user-form-email");
        tfEmail.setRequired(true);
        tfEmail.setRequiredIndicatorVisible(true);
        tfEmail.setWidthFull();

        pfPassword = new PasswordField(isEdit ? "New password (optional)" : "Password");
        pfPassword.setId("user-form-password");
        pfPassword.setRequired(!isEdit); // required only on create
        pfPassword.setRequiredIndicatorVisible(!isEdit);
        pfPassword.setWidthFull();

        pfConfirm = new PasswordField(isEdit ? "Confirm new password" : "Confirm Password");
        pfConfirm.setId("user-form-password-confirm");
        pfConfirm.setRequired(!isEdit); // required only on create
        pfConfirm.setRequiredIndicatorVisible(!isEdit);
        pfConfirm.setWidthFull();

        msRoles = new MultiSelectComboBox<>("Roles");
        msRoles.setId("user-form-roles");
        msRoles.setItemLabelGenerator(Role::getName);
        msRoles.setWidthFull();
        msRoles.setClearButtonVisible(true);
        msRoles.setRequired(true);
        msRoles.setRequiredIndicatorVisible(true);
        msRoles.setItems(
                DataProvider.fromFilteringCallbacks(
                        q -> {
                            int page = q.getOffset() / Math.max(1, q.getLimit());
                            int size = Math.max(1, q.getLimit());
                            String filter = q.getFilter().orElse(null);
                            return roleService.searchRole(filter, null, PageRequest.of(page, size)).stream();
                        },
                        q -> {
                            String filter = q.getFilter().orElse(null);
                            return (int) roleService.searchRole(filter, null, PageRequest.of(0, 1)).getTotalElements();
                        }
                )
        );

        cbEnabled = new Checkbox("Enabled");
        cbEnabled.setId("user-form-enabled");
        cbEnabled.setValue(saveUser.getId() == null || saveUser.isEnabled());

        HorizontalLayout row1 = new HorizontalLayout(tfUsername, tfEmail);
        row1.setWidthFull();
        row1.setFlexGrow(1, tfUsername);
        row1.setFlexGrow(1, tfEmail);

        HorizontalLayout row2 = new HorizontalLayout(pfPassword, pfConfirm);
        row2.setWidthFull();
        row2.setFlexGrow(1, pfPassword);
        row2.setFlexGrow(1, pfConfirm);

        HorizontalLayout row3 = new HorizontalLayout(msRoles, cbEnabled);
        row3.setWidthFull();
        row3.setFlexGrow(2, msRoles);
        row3.setFlexGrow(0, cbEnabled);

        VerticalLayout main = new VerticalLayout(row1, row2, row3);
        main.setSpacing(true);
        main.setPadding(true);
        main.setWidthFull();
        main.setId("user-form-body");

        return main;
    }

    private void buildFooterButtons() {
        btnSave = new Button("Save (F10)");
        btnSave.setId("user-form-btn-save");
        btnSave.addClickShortcut(Key.F10);
        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
        btnSave.addClickListener(e -> saveChanges());

        btnExit = new Button("Exit (ESC)");
        btnExit.setId("user-form-btn-exit");
        btnExit.addClickShortcut(Key.ESCAPE);
        btnExit.addThemeVariants(ButtonVariant.LUMO_SMALL);
        btnExit.addClickListener(e -> {
            if (!view) {
                ConfirmWindow cw = new ConfirmWindow("Action Confirmation", "Are you sure to continue?", this::close);
                cw.setId("user-form-exit-confirm");
                cw.open();
            } else {
                close();
            }
        });

        if (view) btnSave.setVisible(false);

        getFooter().add(btnExit, btnSave);
    }

    private void saveChanges() {
        if (!validate()) return;

        try {
            loadComponents();

            boolean update = (saveUser.getId() != null);
            if (update) {
                userService.updateUser(saveUser.getId(), saveUser);
            } else {
                userService.createUser(saveUser);
            }

            new MySuccessNotification(
                    "User " + (update ? "updated" : "created") + " successfully: " + saveUser.getUsername()
            ).open();

            close();
        } catch (Exception e) {
            new MyErrorNotification(e.getMessage()).open();
        }
    }

    private boolean validate() {
        boolean ok = true;

        if (tfUsername.isRequired() && StringUtils.isBlank(tfUsername.getValue())) { ok = false; tfUsername.setInvalid(true); }
        else tfUsername.setInvalid(false);

        if (tfEmail.isRequired() && StringUtils.isBlank(tfEmail.getValue())) { ok = false; tfEmail.setInvalid(true); }
        else tfEmail.setInvalid(false);

        final String pass = StringUtils.defaultString(pfPassword.getValue(), "");
        final String conf = StringUtils.defaultString(pfConfirm.getValue(), "");

        if (!isEdit) {
            if (StringUtils.isBlank(pass)) { ok = false; pfPassword.setInvalid(true); } else pfPassword.setInvalid(false);
            if (StringUtils.isBlank(conf)) { ok = false; pfConfirm.setInvalid(true); } else pfConfirm.setInvalid(false);
            if (ok && !StringUtils.equals(pass, conf)) {
                ok = false;
                pfPassword.setInvalid(true);
                pfConfirm.setInvalid(true);
            }
        } else {
            boolean anyEntered = StringUtils.isNotBlank(pass) || StringUtils.isNotBlank(conf);
            if (anyEntered) {
                if (StringUtils.isBlank(pass)) { ok = false; pfPassword.setInvalid(true); }
                if (StringUtils.isBlank(conf)) { ok = false; pfConfirm.setInvalid(true); }
                if (ok && !StringUtils.equals(pass, conf)) {
                    ok = false;
                    pfPassword.setInvalid(true);
                    pfConfirm.setInvalid(true);
                }
            } else {
                pfPassword.setInvalid(false);
                pfConfirm.setInvalid(false);
            }
        }

        if (msRoles.isRequired() && (msRoles.getSelectedItems() == null || msRoles.getSelectedItems().isEmpty())) {
            ok = false;
            msRoles.setInvalid(true);
        } else {
            msRoles.setInvalid(false);
        }

        return ok;
    }

    private void loadComponents() {
        saveUser.setUsername(tfUsername.getValue());
        saveUser.setEmail(tfEmail.getValue());
        saveUser.setEnabled(cbEnabled.getValue());

        String newPass = StringUtils.trimToEmpty(pfPassword.getValue());
        if (!isEdit || StringUtils.isNotBlank(newPass)) {
            saveUser.setPassword(newPass);
        } else {
            saveUser.setPassword(null); // signal "no change"
        }

        Set<Role> selected = new LinkedHashSet<>(msRoles.getSelectedItems());
        saveUser.setRoles(selected);
    }

    private void fillFields() {
        tfUsername.setValue(StringUtils.defaultString(saveUser.getUsername(), ""));
        tfEmail.setValue(StringUtils.defaultString(saveUser.getEmail(), ""));
        cbEnabled.setValue(saveUser.isEnabled());

        if (saveUser.getRoles() != null && !saveUser.getRoles().isEmpty()) {
            msRoles.select(saveUser.getRoles().toArray(new Role[0]));
        }
    }

    private void disableFields() {
        tfUsername.setReadOnly(true);
        tfEmail.setReadOnly(true);
        pfPassword.setReadOnly(true);
        pfConfirm.setReadOnly(true);
        msRoles.setReadOnly(true);
        cbEnabled.setReadOnly(true);
        btnSave.setVisible(false);
    }
}
