package org.example.inventario.ui.view.permit;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.security.Permit;
import org.example.inventario.service.security.PermitService;
import org.example.inventario.ui.component.ControlPanel;
import org.example.inventario.ui.component.SearchFilter;
import org.example.inventario.ui.view.MainLayout;
import org.springframework.data.domain.PageRequest;

@Route(value ="/permit", layout = MainLayout.class)
@PageTitle("Permit")
@RolesAllowed({"ROLES_MENU"})
@Menu(order = 4, icon = "vaadin:ticket", title = "Permit")
public class PermitPage extends ControlPanel<Permit> {

    private PermitService permitService;

    public PermitPage(PermitService permitService) {
        super();
        this.permitService = permitService;
        setConfig();
    }

    @Override
    protected void configurateFilter(SearchFilter filter) {
        filter.addFilter(String.class, "name", "Name", null, null, "", false);
    }

    @Override
    protected void addSecurity() {

    }

    @Override
    protected void configGrid(Grid<Permit> grid) {
        grid.addColumn(Permit::getName).setHeader("Name").setSortable(true);
        grid.setSizeFull();

    }

    @Override
    protected CallbackDataProvider.FetchCallback<Permit, Void> configDataSource() {
        return query -> {
            TextField name = (TextField) searchFilter.getFilter("name");
            return permitService.searchPermit(name.getValue(), PageRequest.of(query.getPage(), query.getLimit())).stream();
        };
    }

    @Override
    protected void configButtons() {

    }

    @Override
    protected void deleteRow() {

    }
}
