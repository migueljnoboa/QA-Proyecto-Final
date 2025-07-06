package org.example.inventario.ui.view.login;

import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@AnonymousAllowed
public class Login extends VerticalLayout  {
    private LoginOverlay loginOverlay = new LoginOverlay();

    public Login() {
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        loginOverlay.setAction("login");
        loginOverlay.setOpened(true);
        loginOverlay.setTitle("Login");
        loginOverlay.setDescription("Please enter your username and password");
        loginOverlay.setForgotPasswordButtonVisible(false);
        add(loginOverlay);
    }
//    @Override
//    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
//        if(beforeEnterEvent.getLocation()
//                .getQueryParameters()
//                .getParameters()
//                .containsKey("error")) {
//            loginOverlay.setError(true);
//        }
//    }
}
