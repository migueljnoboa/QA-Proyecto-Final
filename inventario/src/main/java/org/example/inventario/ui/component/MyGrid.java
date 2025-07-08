package org.example.inventario.ui.component;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;

public class MyGrid<T> extends Grid<T> {

    public MyGrid() {
        setSizeFull();
        setPageSize(50);
        setMultiSort(false); //Multisort por defecto false, pero el se tiene soporte para multisort por fuera (se debe indicar una lista de Sort.Order)
        setColumnReorderingAllowed(true);
        setSelectionMode(SelectionMode.SINGLE);
        addThemeVariants(
                GridVariant.LUMO_COMPACT,
                GridVariant.LUMO_COLUMN_BORDERS,
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_WRAP_CELL_CONTENT
        );
    }

    public MyGrid(Class<T> beanType, boolean autoCreateColumns) {
        super(beanType, autoCreateColumns);
        setSizeFull();
        setPageSize(50);
        setMultiSort(false);
        setColumnReorderingAllowed(true);
        setSelectionMode(SelectionMode.SINGLE);
        addThemeVariants(
                GridVariant.LUMO_COMPACT,
                GridVariant.LUMO_COLUMN_BORDERS,
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_WRAP_CELL_CONTENT
        );
    }

    public MyGrid(Class<T> beanType, boolean autoCreateColumns, boolean footerRow) {
        super(beanType, autoCreateColumns);
        setSizeFull();
        setPageSize(50);
        setMultiSort(false);
        setColumnReorderingAllowed(true);
        setSelectionMode(SelectionMode.SINGLE);
        addThemeVariants(
                GridVariant.LUMO_COMPACT,
                GridVariant.LUMO_COLUMN_BORDERS,
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_WRAP_CELL_CONTENT
        );
        if(footerRow) {
            appendFooterRow();
        }
    }

}
