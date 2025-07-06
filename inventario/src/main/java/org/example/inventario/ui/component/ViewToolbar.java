package org.example.inventario.ui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.theme.lumo.LumoUtility;

public final class ViewToolbar extends Composite<Header> {

    public ViewToolbar(String viewTitle, Component... components) {
        addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.JustifyContent.BETWEEN, LumoUtility.AlignItems.STRETCH, LumoUtility.Gap.MEDIUM,
                LumoUtility.FlexDirection.Breakpoint.Medium.ROW, LumoUtility.AlignItems.Breakpoint.Medium.CENTER);

        var drawerToggle = new DrawerToggle();
        drawerToggle.addClassNames(LumoUtility.Margin.NONE);

        var title = new H1(viewTitle);
        title.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.NONE, LumoUtility.FontWeight.LIGHT);

        var toggleAndTitle = new Div(drawerToggle, title);
        toggleAndTitle.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER);
        getContent().add(toggleAndTitle);

        if (components.length > 0) {
            var actions = new Div(components);
            actions.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.JustifyContent.BETWEEN, LumoUtility.Flex.GROW, LumoUtility.Gap.SMALL,
                    LumoUtility.FlexDirection.Breakpoint.Medium.ROW);
            getContent().add(actions);
        }
    }

    public static Component group(Component... components) {
        var group = new Div(components);
        group.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.AlignItems.STRETCH, LumoUtility.Gap.SMALL,
                LumoUtility.FlexDirection.Breakpoint.Medium.ROW, LumoUtility.AlignItems.Breakpoint.Medium.CENTER);
        return group;
    }
}
