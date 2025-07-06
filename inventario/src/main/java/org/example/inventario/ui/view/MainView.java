package org.example.inventario.ui.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.example.inventario.ui.component.ViewToolbar;

@Route
@PermitAll
public final class MainView extends Main {



    MainView() {
        addClassName(LumoUtility.Padding.MEDIUM);
        add(new ViewToolbar("Main"));
        add(new Div("Please select a view from the menu on the left."));
    }

    public static void showMainView() {
        UI.getCurrent().navigate(MainView.class);
    }
}
