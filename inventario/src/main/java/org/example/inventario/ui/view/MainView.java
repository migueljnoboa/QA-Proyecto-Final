package org.example.inventario.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.example.inventario.service.security.SecurityService;
import org.example.inventario.ui.component.ViewToolbar;
import org.springframework.beans.factory.annotation.Autowired;


@Route("")
@PageTitle("Home")
@RolesAllowed({"DASHBOARD_MENU"})
public final class MainView extends Main {
    @Autowired
    private SecurityService securityService;

    MainView() {
        addClassName(LumoUtility.Padding.MEDIUM);
        add(new ViewToolbar("Main"));
        add(new Div("Please select a view from the menu on the left."));
        add(newButton());
    }
    public Component newButton() {
        Button button = new Button("Click me");
        button.addClickListener(event -> {
            String username = securityService.getAuthenticatedUsuario().getUsuario().getUsername();
            Notification.show("Button clicked by: "+ username);
        });
        return button;
    }
    public static void showMainView() {
        UI.getCurrent().navigate(MainView.class);
    }
}
