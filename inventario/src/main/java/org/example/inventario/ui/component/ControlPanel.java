package org.example.inventario.ui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;

public abstract class ControlPanel<T> extends Div {
    protected T selectedItem;
    protected final SearchFilter searchFilter;
    protected final MyGrid<T> grid;

    protected VerticalLayout content;

    protected HorizontalLayout layoutTop, layoutBottom, layoutBottom2;

    protected Button btnRefresh, btnNew, btnEdit, btnView, btnCancel, btnPrint;

    public ControlPanel() {

        searchFilter = new SearchFilter(this::fillGrid);


        grid = new MyGrid<>();
        grid.getLazyDataView().setItemIndexProvider((t, query) -> 0);
        grid.addSelectionListener(event -> {
            selectedItem = event.getFirstSelectedItem().orElse(null);
            enableButtons(selectedItem);
        });

        content = new VerticalLayout();
        content.setSizeFull();
        content.setSpacing(true);
        content.add(searchFilter);
        content.add(createButtons());
        content.add(grid);


        add(content);

        setId("ControlPanel");
        setSizeFull();
    }


    protected void setConfig() {

        configurateFilter(searchFilter);

        configGrid(grid);

        grid.setItems(configDataSource());

        configButtons();

        addSecurity();

        fillGrid();
    }


    protected void fillGrid() {
        grid.getDataProvider().refreshAll();
        grid.select(null);
    }

    private Component createButtonsTop() {
        btnRefresh = new Button("Refresh");
        btnRefresh.setIcon(VaadinIcon.REFRESH.create());
        btnRefresh.addClickListener(event -> fillGrid());

        btnNew = new Button("New");
        btnNew.setIcon(VaadinIcon.PLUS.create());

        btnEdit = new Button("Edit");
        btnEdit.setIcon(VaadinIcon.EDIT.create());
        btnEdit.setEnabled(false);

        btnView = new Button("View");
        btnView.setIcon(VaadinIcon.SEARCH.create());
        btnView.setEnabled(false);

        btnCancel = new Button("Cancel");
        btnCancel.setEnabled(false);
        btnCancel.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        btnCancel.setIcon(VaadinIcon.TRASH.create());

        btnPrint = new Button("Print");
        btnPrint.setEnabled(false);
        btnPrint.setVisible(false);
        btnPrint.setIcon(VaadinIcon.PRINT.create());

        layoutTop = new HorizontalLayout();
        layoutTop.setWidthFull();
        layoutTop.setSpacing(true);
        layoutTop.getStyle().set("flex-wrap", "wrap");
        layoutTop.getStyle().set("gap", "10px");
        layoutTop.add(btnRefresh);
        layoutTop.add(btnNew);
        layoutTop.add(btnEdit);
        layoutTop.add(btnView);
        layoutTop.add(btnCancel);
        layoutTop.add(btnPrint);

        return layoutTop;
    }

    private Component createButtonsBottom() {
        layoutBottom = new HorizontalLayout();
        layoutBottom.setWidthFull();
        layoutBottom.setSpacing(true);
        layoutBottom.addClassName("spacing-s");
        layoutBottom.getStyle().set("flex-wrap", "wrap");
        layoutBottom.getStyle().set("gap", "10px");

        return layoutBottom;
    }
    protected Component createButtons() {
        VerticalLayout vlBotones = new VerticalLayout();
        vlBotones.setSpacing(false);
        vlBotones.setMargin(false);
        vlBotones.setPadding(false);
        vlBotones.add(createButtonsTop());
        vlBotones.add(createButtonsBottom());
       
        return vlBotones;
    }

    protected abstract void configurateFilter(SearchFilter filter);

    protected abstract void addSecurity();

    protected abstract void configGrid(Grid<T> grid);

    protected abstract CallbackDataProvider.FetchCallback<T, Void> configDataSource();

    protected abstract void configButtons();

    protected abstract void deleteRow();

    protected void enableButtons(T objetoSeleccionado) {
        if (objetoSeleccionado != null) {
            btnEdit.setEnabled(true);
            btnView.setEnabled(true);
            btnCancel.setEnabled(true);
            btnPrint.setEnabled(true);
        } else {
            btnEdit.setEnabled(false);
            btnView.setEnabled(false);
            btnCancel.setEnabled(false);
            btnPrint.setEnabled(false);
        }
    }
}
