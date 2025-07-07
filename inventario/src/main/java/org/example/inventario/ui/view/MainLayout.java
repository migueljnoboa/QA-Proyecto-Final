package org.example.inventario.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.example.inventario.model.entity.security.User;
import org.example.inventario.service.security.SecurityService;

@Layout
@PermitAll
public final class MainLayout extends AppLayout {

    private final SecurityService service;


    MainLayout(SecurityService service) {
        this.service = service;
        setPrimarySection(Section.DRAWER);
        addToDrawer(createHeader(), new Scroller(createSideNav()), createUserMenu());
    }

    private Div createHeader() {
        var appLogo = VaadinIcon.CUBES.create();
        appLogo.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.IconSize.LARGE);

        var appName = new Span("Stock2u");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);

        var header = new Div(appLogo, appName);
        header.addClassNames(LumoUtility.Display.FLEX, LumoUtility.Padding.MEDIUM, LumoUtility.Gap.MEDIUM, LumoUtility.AlignItems.CENTER);
        return header;
    }

    private SideNav createSideNav() {
        var nav = new SideNav();
        nav.addClassNames(LumoUtility.Margin.Horizontal.MEDIUM);
        MenuConfiguration.getMenuEntries().forEach(entry -> nav.addItem(createSideNavItem(entry)));
        return nav;
    }

    private SideNavItem createSideNavItem(MenuEntry menuEntry) {
        if (menuEntry.icon() != null) {
            return new SideNavItem(menuEntry.title(), menuEntry.path(), new Icon(menuEntry.icon()));
        } else {
            return new SideNavItem(menuEntry.title(), menuEntry.path());
        }
    }

    private Component createUserMenu() {
        User user = null;
        var authenticatedUser = service.getAuthenticatedUser();
        if (authenticatedUser != null) {
            user = authenticatedUser.getUsuario();
        }

        var userMenu = new MenuBar();
        userMenu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        userMenu.addClassNames(LumoUtility.Margin.MEDIUM);

        if (user != null) {
            var avatar = new Avatar(user.getUsername());
            avatar.addThemeVariants(AvatarVariant.LUMO_XSMALL);
            avatar.addClassNames(LumoUtility.Margin.Right.SMALL);
            avatar.setColorIndex(5);

            var userMenuItem = userMenu.addItem(avatar);
            userMenuItem.add(user.getUsername());
            //        if (user.getProfileUrl() != null) {
            //            userMenuItem.getSubMenu().addItem("View Profile",
            //                    event -> UI.getCurrent().getPage().open(user.getProfileUrl()));
            //        }
            userMenuItem.getSubMenu().addItem("Logout", event -> service.logout());
        } else {
            userMenu.addItem("Login", event -> UI.getCurrent().navigate("login"));
        }

        return userMenu;
    }

}
