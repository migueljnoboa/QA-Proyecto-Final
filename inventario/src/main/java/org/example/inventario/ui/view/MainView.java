package org.example.inventario.ui.view;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.Responsive;
import com.github.appreciated.apexcharts.config.builder.*;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.chart.builder.ToolbarBuilder;
import com.github.appreciated.apexcharts.config.plotoptions.builder.BarBuilder;
import com.github.appreciated.apexcharts.config.stroke.Curve;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
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
@Menu(order = 1, icon = "vaadin:dashboard", title = "Dashboard")
public final class MainView extends Main {

    public MainView() {
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
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.getStyle().set("display", "grid");
        row.getStyle().set("grid-template-columns", "repeat(auto-fit,minmax(240px,1fr))");
        row.getStyle().set("gap", "1rem");

        row.add(
                statCard("Total Productos", "4", "productos registrados"),
                statCard("Valor Total", "$22,429.72", "valor del inventario"),
                statCard("Stock Bajo", "2", "productos con stock bajo"),
                statCard("Categorías", "2", "categorías activas")
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
        Div wrap = new Div();
        wrap.getStyle().set("padding", "1rem 1.25rem");
        wrap.getStyle().set("border-radius", "12px");
        wrap.getStyle().set("background", "var(--lumo-error-color-10pct)");
        wrap.getStyle().set("border", "1px solid var(--lumo-error-color-50pct)");
        wrap.getStyle().set("display", "flex");
        wrap.getStyle().set("flexDirection", "column");
        wrap.getStyle().set("gap", "0.75rem");

        H3 title = new H3("Alerta de Stock Bajo");
        title.getStyle().set("margin", "0");

        Paragraph note = new Paragraph("Los siguientes productos tienen stock por debajo del mínimo:");
        note.getStyle().set("margin", "0");

        wrap.add(title, note,
                lowStockItem("Mouse Logitech MX Master", "3", "10"),
                lowStockItem("Teclado Mecánico RGB", "2", "5")
        );
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
        // contenedor de análisis con grid
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

        card.add(t, chart);
        return card;
    }

    private ApexCharts buildProductsPerCategoryChart() {
        String[] categorias = {"Electrónicos", "Accesorios"};
        Double[] cantidades = {2d, 2d};

        return ApexChartsBuilder.get()
                .withChart(ChartBuilder.get().withType(Type.BAR).build())
                .withPlotOptions(PlotOptionsBuilder.get()
                        .withBar(BarBuilder.get().withHorizontal(false).build())
                        .build())
                .withSeries(new Series<>("Productos", cantidades))
                .withXaxis(XAxisBuilder.get().withCategories(categorias).build())
                .withResponsive(new Responsive[] { ResponsiveBuilder.get().build() })
                .build();
    }

    private ApexCharts buildValuePerCategoryChart() {
        String[] categorias = {"Electrónicos", "Accesorios"};
        Double[] valores = {21899.77, 529.95};

        return ApexChartsBuilder.get()
                .withChart(ChartBuilder.get().withType(Type.BAR).build())
                .withPlotOptions(PlotOptionsBuilder.get()
                        .withBar(BarBuilder.get().withHorizontal(true).build())
                        .build())
                .withSeries(new Series<>("Valor (USD)", valores))
                .withXaxis(XAxisBuilder.get().withCategories(categorias).build())
                .build();
    }

    private ApexCharts buildValueSharePie() {
        Double[] valores = {21899.77, 529.95};
        String[] categorias = {"Electrónicos", "Accesorios"};

        return ApexChartsBuilder.get()
                .withChart(ChartBuilder.get().withType(Type.PIE).build())
                .withLabels(categorias)
                .withSeries(valores)
                .build();
    }

    private ApexCharts buildMonthlySalesChart() {
        String[] meses = {"Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic"};
        Double[] ventas = {1200d, 1450d, 1320d, 1580d, 1760d, 1650d, 1890d, 2010d, 1950d, 2100d, 2230d, 2400d};

        ApexCharts chart = ApexChartsBuilder.get()
                .withChart(ChartBuilder.get()
                        .withType(Type.AREA)
                        .withToolbar(ToolbarBuilder.get().withShow(true).build())
                        .build())
                .withDataLabels(DataLabelsBuilder.get().withEnabled(false).build())
                .withStroke(StrokeBuilder.get().withCurve(Curve.SMOOTH).build())
                .withSeries(new Series<>("Ventas", ventas))
                .withXaxis(XAxisBuilder.get().withCategories(meses).build())
                .withYaxis(YAxisBuilder.get().withDecimalsInFloat(0d).build())
                .withFill(FillBuilder.get()
                        .withOpacity(0.35)
                        .build())
                .build();

        chart.setHeight("300px");
        return chart;
    }
}
