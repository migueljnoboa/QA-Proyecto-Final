package org.example.inventario.ui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFilter extends Accordion {

    private FormLayout formFilter;
    private final Runnable callback;
    private final Map<String, Component> filtrosMap;

    public SearchFilter(Runnable callback) {
        this.callback = callback;
        this.filtrosMap = new HashMap<>();

        setId("filter-search");

        add("Search Filter",buildWindow()).addThemeVariants(DetailsVariant.FILLED, DetailsVariant.REVERSE, DetailsVariant.SMALL);

        setWidthFull();
    }

    private Component buildWindow() {
        Button btnClearFilter = new Button("Clean Filter");
        btnClearFilter.setIcon(new Icon(VaadinIcon.CROP));
        btnClearFilter.addThemeVariants(ButtonVariant.LUMO_SMALL);
        btnClearFilter.addClickListener(buttonClickEvent -> cleanFilter());

        formFilter = new FormLayout();
        formFilter.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0px", 1),
                new FormLayout.ResponsiveStep("300px", 2),
                new FormLayout.ResponsiveStep("600px", 3),
                new FormLayout.ResponsiveStep("900px", 4),
                new FormLayout.ResponsiveStep("1200px", 5));
        formFilter.add(btnClearFilter, 1);
        formFilter.add(new Div(), 4);

        return formFilter;
    }

    public Component getFilter(String id) {
        return filtrosMap.get(id);
    }

    public <T> void addFilter(Class<T> clase, String id, String name, List<T> ObjectList, CallbackDataProvider.FetchCallback<T, String> funcionPaginacion, T defaultValue, boolean multiSelect) {
        if ((ObjectList != null || funcionPaginacion != null) && !multiSelect) {
            ComboBox<T> cbFiltro = new ComboBox<>();
            cbFiltro.setLabel(name);
            cbFiltro.setPlaceholder("ALL");
            cbFiltro.setId(id);

            if (funcionPaginacion != null) {
                cbFiltro.setItems(funcionPaginacion);
            } else {
                cbFiltro.setItems(ObjectList);
            }

            cbFiltro.setValue(defaultValue);
            cbFiltro.setClearButtonVisible(true);
            cbFiltro.getElement().setAttribute("theme", "small");
            cbFiltro.addValueChangeListener(comboBoxTComponentValueChangeEvent -> refresh());
            register(id, cbFiltro, "");
        } else if ((ObjectList != null || funcionPaginacion != null) && multiSelect) {
            MultiSelectComboBox<T> cbFiltro = new MultiSelectComboBox<T>();
            cbFiltro.setLabel(name);
            cbFiltro.setId(id);

            if (funcionPaginacion != null) {
                cbFiltro.setItems(funcionPaginacion);
            } else {
                cbFiltro.setItems(ObjectList);
            }

            if (defaultValue != null) {
                cbFiltro.setValue(defaultValue);
            }

            cbFiltro.setAllowCustomValue(false);
            cbFiltro.setPlaceholder("ALL");
            cbFiltro.getElement().setAttribute("theme", "small");
            cbFiltro.setRequired(false);
            cbFiltro.setRequiredIndicatorVisible(false);
            cbFiltro.setClearButtonVisible(true);
            cbFiltro.addValueChangeListener(comboBoxTComponentValueChangeEvent -> refresh());
            register(id, cbFiltro, "");
        } else if (clase == String.class) {
            TextField tfFiltro = new TextField(name);
            tfFiltro.setId(id);
            tfFiltro.setClearButtonVisible(true);
            tfFiltro.addThemeVariants(TextFieldVariant.LUMO_SMALL);
            tfFiltro.addValueChangeListener(textFieldStringComponentValueChangeEvent -> refresh());
            register(id, tfFiltro, "");
        } else if (clase == Long.class) {
            IntegerField tfFiltro = new IntegerField(name);
            tfFiltro.setId(id);
            tfFiltro.setClearButtonVisible(true);
            tfFiltro.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
            tfFiltro.setPlaceholder("0");
            tfFiltro.addThemeVariants(TextFieldVariant.LUMO_SMALL);
            tfFiltro.addValueChangeListener(integerFieldIntegerComponentValueChangeEvent -> refresh());
            register(id, tfFiltro, "");
        } else if (clase == Integer.class) {
            IntegerField tfFiltro = new IntegerField(name);
            tfFiltro.setId(id);
            tfFiltro.setClearButtonVisible(true);
            tfFiltro.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
            tfFiltro.setPlaceholder("0");
            tfFiltro.addThemeVariants(TextFieldVariant.LUMO_SMALL);
            tfFiltro.addValueChangeListener(integerFieldIntegerComponentValueChangeEvent -> refresh());
            register(id, tfFiltro, "");
        } else if (clase == BigDecimal.class) {
            BigDecimalField tfFiltro = new BigDecimalField(name);
            tfFiltro.setId(id);
            tfFiltro.setClearButtonVisible(true);
            tfFiltro.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
            tfFiltro.setPlaceholder("0");
            tfFiltro.addThemeVariants(TextFieldVariant.LUMO_SMALL);
            tfFiltro.addValueChangeListener(bigDecimalFieldBigDecimalComponentValueChangeEvent -> refresh());
            register(id, tfFiltro, "");
        } else if (clase == Date.class || clase == LocalDate.class) {
            DatePicker pdfDateInitial = new DatePicker(name);
            pdfDateInitial.setWidthFull();
            pdfDateInitial.setId(id + "start");
            pdfDateInitial.setClearButtonVisible(true);
            pdfDateInitial.setValue(defaultValue != null ? (LocalDate) defaultValue : null);
            pdfDateInitial.getElement().setAttribute("theme", "small");
            pdfDateInitial.setLocale(UI.getCurrent().getLocale());
            pdfDateInitial.addValueChangeListener(datePickerLocalDateComponentValueChangeEvent -> refresh());

            DatePicker pdfDateEnd = new DatePicker("End Date");
            pdfDateEnd.setWidthFull();
            pdfDateEnd.setId(id + "end");
            pdfDateEnd.setClearButtonVisible(true);
            pdfDateEnd.setValue(null);
            pdfDateEnd.getElement().setAttribute("theme", "small");
            pdfDateEnd.setLocale(UI.getCurrent().getLocale());
            pdfDateEnd.addValueChangeListener(datePickerLocalDateComponentValueChangeEvent -> refresh());

            FormLayout hlDateFilter = new FormLayout();
            hlDateFilter.setSizeFull();
            hlDateFilter.setResponsiveSteps(new FormLayout.ResponsiveStep("1px", 2));
            hlDateFilter.add(pdfDateInitial);
            hlDateFilter.add(pdfDateEnd);

            register(id, hlDateFilter, "");

            filtrosMap.put(id + "start", pdfDateInitial);
            filtrosMap.put(id + "end", pdfDateEnd);
        } else if (clase == Span.class) {
            Span label = new Span(name);
            label.setId(id);
            register(id, label, "");
        }
    }

    public void addIntegerRange(String idBase, String minLabel, String maxLabel) {
        IntegerField min = new IntegerField(minLabel);
        min.setId(idBase + "Min");
        min.setClearButtonVisible(true);
        min.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT, TextFieldVariant.LUMO_SMALL);
        min.setPlaceholder("0");
        min.addValueChangeListener(e -> refresh());

        IntegerField max = new IntegerField(maxLabel);
        max.setId(idBase + "Max");
        max.setClearButtonVisible(true);
        max.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT, TextFieldVariant.LUMO_SMALL);
        max.setPlaceholder("0");
        max.addValueChangeListener(e -> refresh());

        FormLayout range = new FormLayout();
        range.setSizeFull();
        range.setResponsiveSteps(new FormLayout.ResponsiveStep("1px", 2));
        range.add(min, max);

        register(idBase, range, "");

        filtrosMap.put(idBase + "Min", min);
        filtrosMap.put(idBase + "Max", max);
    }

    private void register(String id, Component component, String label) {
        if (StringUtils.isNotBlank(label)) {
            formFilter.addFormItem(component, label);
        } else {
            formFilter.add(component);
        }
        filtrosMap.put(id, component);
    }

    private void refresh() {
        callback.run();
    }

    private void cleanFilter() {
        formFilter.getChildren().forEach(component -> {
            if (component instanceof HorizontalLayout || component instanceof VerticalLayout || component instanceof FormLayout) {
                component.getChildren().forEach(this::cleanComponents);
            } else {
                cleanComponents(component);
            }
        });
    }

    private void cleanComponents(Component component) {
        if (component instanceof HasValueAndElement && ((HasValueAndElement<?, ?>) component).isReadOnly()) {
            return;
        }

        if (component instanceof TextField) {
            ((TextField) component).setValue("");
        } else if (component instanceof IntegerField) {
            ((IntegerField) component).setValue(null);
        } else if (component instanceof BigDecimalField) {
            ((BigDecimalField) component).setValue(null);
        } else if (component instanceof Select) {
            ((Select<?>) component).setValue(null);
        } else if (component instanceof ComboBox) {
            ((ComboBox<?>) component).setValue(null);
        } else if (component instanceof DatePicker) {
            ((DatePicker) component).clear();
        } else if (component instanceof MultiSelectComboBox) {
            ((MultiSelectComboBox<?>) component).clear();
        }
    }
}
