package org.example.inventario.ui.component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;


public class ConfirmWindow extends ConfirmDialog {

    public ConfirmWindow(String mensaje, Runnable callbackYes) {
        this("", mensaje, callbackYes);
    }

    public ConfirmWindow(String titulo, String mensaje, Runnable callbackYes) {
        setCloseOnEsc(true);
        setCancelable(true);
        setHeader(titulo);
        setText(mensaje);

        setConfirmText("Confirm");
        addConfirmListener(event -> {
            callbackYes.run();

            close();
        });

        setCancelText("Cancel");
        addCancelListener(cancelEvent -> close());
    }

    public ConfirmWindow(String titulo, String mensaje, String preguntaCheckBox, boolean defaultPreguntaValue, Runnable callbackYes) {
        Checkbox chQuest = new Checkbox(preguntaCheckBox);
        chQuest.setValue(defaultPreguntaValue);

        VerticalLayout vlContenido = new VerticalLayout();
        vlContenido.setSizeFull();
        vlContenido.setMargin(false);
        vlContenido.setPadding(false);
        vlContenido.add(new Span(mensaje));
        vlContenido.add(chQuest);

        setCloseOnEsc(true);
        setCancelable(true);
        setHeader(titulo);
        add(vlContenido);

        setConfirmText("Confirm");
        addConfirmListener(event -> {
            if (!chQuest.getValue()) {
                MyErrorNotification miErrorNotification = new MyErrorNotification("You must confirm the question to continue...");
                miErrorNotification.open();
                return;
            }

            callbackYes.run();

            close();
        });

        setCancelText("Cancel");
        addCancelListener(cancelEvent -> close());
    }
}
