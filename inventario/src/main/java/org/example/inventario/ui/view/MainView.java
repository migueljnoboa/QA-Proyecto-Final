package org.example.inventario.ui.view;

import com.github.appreciated.apexcharts.config.PlotOptions;
import com.github.appreciated.apexcharts.config.plotoptions.Bar;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.example.inventario.model.entity.inventory.Category;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.security.Permit;
import org.example.inventario.service.inventory.ProductService;
import org.example.inventario.utils.Utilidades;
import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.*;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.helper.Series;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Route("")
@PageTitle("Home")
@RolesAllowed({Permit.DASHBOARD_MENU})
@Menu(order = 1, icon = "vaadin:dashboard", title = "Dashboard")
public final class MainView extends Main {

    private ProductService productService;
    private List<Product> productsLowStock;

    public MainView(ProductService productService) {
        this.productService = productService;
        setWidthFull();
        getStyle().set("display", "flex");
        getStyle().set("flexDirection", "column");
        getStyle().set("gap", "1.25rem");
        getStyle().set("max-width", "1200px");
        getStyle().set("margin", "0 auto");
        getStyle().set("padding", "1rem");

        add(buildHeader(), buildStatCards(), buildLowStockAlert(), buildAnalysis());
    }

    private Component buildHeader() {
        Div header = new Div();
        header.getStyle().set("display", "flex");
        header.getStyle().set("flexDirection", "column");
        header.getStyle().set("gap", "0.25rem");

        H1 title = new H1("Dashboard de Inventario");
        title.getStyle().set("margin", "0");

        Span subtitle = new Span("Resumen general y análisis");
        subtitle.getStyle().set("color", "var(--lumo-secondary-text-color)");

        header.add(title, subtitle);
        return header;
    }

    private Component buildStatCards() {
        productsLowStock = productService.productLowStock();
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.getStyle().set("display", "grid");
        row.getStyle().set("grid-template-columns", "repeat(auto-fit,minmax(240px,1fr))");
        row.getStyle().set("gap", "1rem");

        row.add(
                statCard("Total Productos", String.valueOf(productService.countAllProducts()), "productos registrados"),
                statCard("Valor Total", Utilidades.convertAndFormatBigDecimalToString(productService.getTotalStockValue()), "valor del inventario"),
                statCard("Stock Bajo", String.valueOf(productsLowStock.size()), "productos con stock bajo"),
                statCard("Categorías", String.valueOf(Category.values().length), "categorías activas")
        );
        return row;
    }

    private Div statCard(String title, String big, String subtitle) {
        Div card = new Div();
        card.addClassName("card");
        card.getStyle().set("padding", "1rem 1.25rem");
        card.getStyle().set("border-radius", "12px");
        card.getStyle().set("background", "var(--lumo-base-color)");
        card.getStyle().set("box-shadow", "var(--lumo-box-shadow-s)");
        card.getStyle().set("display", "flex");
        card.getStyle().set("flexDirection", "column");
        card.getStyle().set("gap", "0.25rem");

        Span t = new Span(title);
        t.getStyle().set("color", "var(--lumo-secondary-text-color)");
        t.getStyle().set("font-size", "var(--lumo-font-size-s)");

        H2 b = new H2(big);
        b.getStyle().set("margin", "0");
        b.getStyle().set("font-size", "1.6rem");

        Span s = new Span(subtitle);
        s.getStyle().set("color", "var(--lumo-secondary-text-color)");
        s.getStyle().set("font-size", "var(--lumo-font-size-s)");

        card.add(t, b, s);
        return card;
    }

    private Component buildLowStockAlert() {
        if (productsLowStock.isEmpty()) {
            return new Div();
        }
        Div wrap = new Div();
        wrap.getStyle().set("padding", "1rem 1.25rem");
        wrap.getStyle().set("border-radius", "12px");
        wrap.getStyle().set("background", "var(--lumo-error-color-10pct)");
        wrap.getStyle().set("border", "1px solid var(--lumo-error-color-50pct)");
        wrap.getStyle().set("display", "flex");
        wrap.getStyle().set("flexDirection", "column");
        wrap.getStyle().set("gap", "0.75rem");

        H3 title = new H3("Stock Bajo");
        title.getStyle().set("margin", "0");

        Paragraph note = new Paragraph("Los siguientes productos tienen stock por debajo del mínimo:");
        note.getStyle().set("margin", "0");

        wrap.add(title, note);
        productsLowStock.stream().map(p -> lowStockItem(p.getName(), String.valueOf(p.getStock()), String.valueOf(p.getMinStock()))).forEach(wrap::add);
        return wrap;
    }

    private Div lowStockItem(String name, String stock, String min) {
        Div item = new Div();
        item.getStyle().set("display", "flex");
        item.getStyle().set("alignItems", "center");
        item.getStyle().set("justifyContent", "space-between");
        item.getStyle().set("gap", "1rem");
        item.getStyle().set("padding", "0.5rem 0.75rem");
        item.getStyle().set("background", "var(--lumo-error-color-10pct)");
        item.getStyle().set("border-radius", "10px");

        Span label = new Span(name);
        label.getStyle().set("font-weight", "600");

        Span badge = new Span("Stock: " + stock + " / Min: " + min);
        badge.getStyle().set("background", "var(--lumo-error-color)");
        badge.getStyle().set("color", "white");
        badge.getStyle().set("padding", "0.2rem 0.6rem");
        badge.getStyle().set("border-radius", "999px");
        badge.getStyle().set("font-size", "var(--lumo-font-size-s)");

        item.add(label, badge);
        return item;
    }

    private Component buildAnalysis() {
        Div section = new Div();
        section.getStyle().set("display", "grid");
        section.getStyle().set("grid-template-columns", "repeat(auto-fit,minmax(320px,1fr))");
        section.getStyle().set("gap", "1rem");
        section.setWidthFull();

        section.add(
                chartCard("Productos por Categoría", buildProductsPerCategoryChart()),
                chartCard("Valor por Categoría", buildValuePerCategoryChart()),
                chartCard("Participación de Valor (%)", buildValueSharePie())
        );

        Div fullWidth = chartCard("Ventas por mes", buildMonthlySalesChart());
        fullWidth.getStyle().set("grid-column", "1 / -1");
        fullWidth.getStyle().set("min-height", "200px");
        section.add(fullWidth);

        return section;
    }

    private Div chartCard(String title, ApexCharts chart) {
        Div card = new Div();
        card.getStyle().set("padding", "1rem 1.25rem");
        card.getStyle().set("border-radius", "12px");
        card.getStyle().set("background", "var(--lumo-base-color)");
        card.getStyle().set("box-shadow", "var(--lumo-box-shadow-s)");
        card.getStyle().set("min-height", "320px");
        card.getStyle().set("display", "flex");
        card.getStyle().set("flexDirection", "column");
        card.getStyle().set("gap", "0.5rem");

        H4 t = new H4(title);
        t.getStyle().set("margin", "0");

        chart.setHeight("280px");
        card.add(t, chart);
        return card;
    }

    private ApexCharts buildProductsPerCategoryChart() {
        List<Category> categories = List.of(Category.values());

        String[] categoryNames = categories.stream().map(Category::name).toArray(String[]::new);
        Number[] productCounts = categories.stream()
                .map(category -> productService.countProductsByCategory(category))
                .toArray(Number[]::new);

        return ApexChartsBuilder.get()
                .withChart(ChartBuilder.get().withType(Type.BAR).build())
                .withXaxis(XAxisBuilder.get().withCategories(categoryNames).build())
                .withSeries(new Series<>("Productos por categoría", productCounts))
                .build();
    }

    private ApexCharts buildValuePerCategoryChart() {
        List<Category> categories = List.of(Category.values());


        String[] categoryNames = categories.stream().map(Category::name).toArray(String[]::new);
        Number[] values = categories.stream()
                .map(category -> productService.getTotalStockValueByCategory(category))
                .toArray(Number[]::new);

        PlotOptions plotOptions = new PlotOptions();
        Bar bar = new Bar();
        bar.setHorizontal(true);
        plotOptions.setBar(bar);

        return ApexChartsBuilder.get()
                .withChart(ChartBuilder.get().withType(Type.BAR).build())
                .withPlotOptions(plotOptions)
                .withXaxis(XAxisBuilder.get().withCategories(categoryNames).build())
                .withSeries(new Series<>("Valor", values))

                .build();
    }

    private ApexCharts buildValueSharePie() {
        List<Category> categories = List.of(Category.values());

        String[] categoryNames = categories.stream()
                .map(Category::name)
                .toArray(String[]::new);

        Double[] percentages = categories.stream()
                .map(category -> {
                    Double percent = productService.getTotalStockValuePercentByCategory(category);
                    return BigDecimal.valueOf(percent != null ? percent : 0.0)
                            .setScale(2, RoundingMode.HALF_UP)
                            .doubleValue();
                })
                .toArray(Double[]::new);

        return ApexChartsBuilder.get()
                .withChart(ChartBuilder.get().withType(Type.PIE).build())
                .withLabels(categoryNames)
                .withSeries(percentages)
                .withLegend(LegendBuilder.get().withShow(false).build())
                .build();
    }

    private ApexCharts buildMonthlySalesChart() {
        String[] months = {"Ene", "Feb", "Mar", "Abr", "May", "Jun",
                "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        Number[] salesData = {1200, 1450, 1320, 1580, 1760, 1650,
                1890, 2010, 1950, 2100, 2230, 2400};

        return ApexChartsBuilder.get()
                .withChart(ChartBuilder.get().withType(Type.AREA).build())
                .withXaxis(XAxisBuilder.get().withCategories(months).build())
                .withSeries(new Series<>("Ventas", salesData))
                .build();
    }
}
