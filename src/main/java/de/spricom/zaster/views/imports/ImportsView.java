package de.spricom.zaster.views.imports;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import de.spricom.zaster.data.Import;
import de.spricom.zaster.services.ImportService;
import de.spricom.zaster.views.MainLayout;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

@PageTitle("Imports")
@Route(value = "imports/:importId?/:action?(details)", layout = MainLayout.class)
@Log4j2
public class ImportsView extends Div implements BeforeEnterObserver {

    private final String IMPORT_ID = "importId";
    private final String IMPORT_DETAILS_ROUTE_TEMPLATE = "imports/%s/details";

    private final ImportService importService;

    private final Grid<Import> grid = new Grid<>(Import.class, false);

    private DateTimePicker importedAt;
    private TextField importerName;

    private final Button cancel = new Button("Abbrechen");
    private final Button bookings = new Button("Buchungen");
    private final Button delete = new Button("Löschen");

    private final Binder<Import> binder = new Binder<>(Import.class);

    private Import currentImport;

    public ImportsView(ImportService importService) {
        this.importService = importService;
        addClassNames("imports-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createDetailsLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn(this::importedAtDateTime).setHeader("Importdatum").setAutoWidth(true);
        grid.addColumn(Import::getImporterName).setHeader("Importer").setAutoWidth(true);
        grid.addColumn(Import::getImportedCount).setHeader("Anzahl").setAutoWidth(true);
        grid.setItems(this::loadImportsPage);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(IMPORT_DETAILS_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearDetails();
                UI.getCurrent().navigate(ImportsView.class);
            }
        });

        // Configure Details
        binder.bindReadOnly(importedAt, this::importedAtDateTime);
        binder.bindReadOnly(importerName, Import::getImporterName);

        cancel.addClickListener(event -> {
            clearDetails();
            refreshGrid();
        });

        delete.setVisible(false);
    }

    private LocalDateTime importedAtDateTime(Import imported) {
        return imported.getImportedAt().toLocalDateTime();
    }

    private Stream<Import> loadImportsPage(Query<Import, Void> query) {
        PageRequest pageable = PageRequest.of(query.getPage(), query.getPageSize(),
                VaadinSpringDataHelpers.toSpringDataSort(query));
        return importService.list(pageable).stream();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> importId = event.getRouteParameters().get(IMPORT_ID);
        if (importId.isPresent()) {
            Optional<Import> imported = importService.getImport(importId.get());
            if (imported.isPresent()) {
                populateDetails(imported.get());
            } else {
                Notification.show(String.format("Import nicht gefunden, ID = %s", importId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(ImportsView.class);
            }
        }
    }

    private void createDetailsLayout(SplitLayout splitLayout) {
        Div detailsLayoutDiv = new Div();
        detailsLayoutDiv.setClassName("details-layout");

        Div detailsDiv = new Div();
        detailsDiv.setClassName("details");
        detailsLayoutDiv.add(detailsDiv);

        importedAt = new DateTimePicker();
        importerName = new TextField();

        FormLayout formLayout = new FormLayout();
        formLayout.add(importedAt, importerName);

        detailsDiv.add(formLayout);
        createButtonLayout(detailsLayoutDiv);

        splitLayout.addToSecondary(detailsLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        bookings.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonLayout.add(bookings, cancel, delete);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearDetails() {
        populateDetails(null);
    }

    private void populateDetails(Import imported) {
        currentImport = imported;
        binder.readBean(currentImport);
        delete.setVisible(imported != null && imported.getId() != null);
    }
}
