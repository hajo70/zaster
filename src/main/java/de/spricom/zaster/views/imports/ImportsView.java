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
import de.spricom.zaster.data.FileSource;
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

    private final Grid<FileSource> grid = new Grid<>(FileSource.class, false);

    private DateTimePicker importedAt;
    private TextField importerName;

    private final Button cancel = new Button("Abbrechen");
    private final Button bookings = new Button("Buchungen");
    private final Button delete = new Button("Löschen");

    private final Binder<FileSource> binder = new Binder<>(FileSource.class);

    private FileSource currentFileSource;

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
        grid.addColumn(this::importerName).setHeader("Importer").setAutoWidth(true);
        grid.addColumn(this::counts).setHeader("Anzahl").setAutoWidth(true);
        grid.addColumn(this::filename).setHeader("Datei").setAutoWidth(true);
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
        binder.bindReadOnly(importerName, this::importerName);

        cancel.addClickListener(event -> {
            clearDetails();
            refreshGrid();
        });

        delete.setVisible(false);
    }

    private LocalDateTime importedAtDateTime(FileSource fileSource) {
        return fileSource.getImported().getImportedAt().toLocalDateTime();
    }

    private String importerName(FileSource fileSource) {
        return fileSource.getImported().getImporterName();
    }

    private String counts(FileSource fileSource) {
        return fileSource.getImported().getImportedCount() + " / " + fileSource.getTotalCount();
    }

    private String filename(FileSource fileSource) {
        return fileSource.getFilename();
    }

    private Stream<FileSource> loadImportsPage(Query<FileSource, Void> query) {
        PageRequest pageable = PageRequest.of(query.getPage(), query.getPageSize(),
                VaadinSpringDataHelpers.toSpringDataSort(query));
        return importService.list(pageable).stream();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> fileSourceId = event.getRouteParameters().get(IMPORT_ID);
        if (fileSourceId.isPresent()) {
            Optional<FileSource> imported = importService.getFileSource(fileSourceId.get());
            if (imported.isPresent()) {
                populateDetails(imported.get());
            } else {
                Notification.show(String.format("Import nicht gefunden, ID = %s", fileSourceId.get()),
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

    private void populateDetails(FileSource fileSource) {
        currentFileSource = fileSource;
        binder.readBean(currentFileSource);
        delete.setVisible(fileSource != null && fileSource.getId() != null);
    }
}
