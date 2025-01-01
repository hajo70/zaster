package de.spricom.zaster.views.currencies;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import de.spricom.zaster.data.Currency;
import de.spricom.zaster.data.CurrencyType;
import de.spricom.zaster.data.ZasterCurrency;
import de.spricom.zaster.services.CurrencyService;
import de.spricom.zaster.views.MainLayout;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;
import java.util.stream.Stream;

@PageTitle("Währungen")
@Route(value = "currencies/:currencyId?/:action?(edit)", layout = MainLayout.class)
@Log4j2
public class CurrenciesView extends Div implements BeforeEnterObserver {

    private final String CURRENCY_ID = "currencyId";
    private final String CURRENCY_EDIT_ROUTE_TEMPLATE = "currencies/%s/edit";

    private final Grid<Currency> grid = new Grid<>(Currency.class, false);

    private final TextField currencyName = new TextField("Währung");
    private final TextField currencyCode = new TextField("Code");
    private final ComboBox<CurrencyType> currencyType = new ComboBox<>("Typ", CurrencyType.values());
    private final ComboBox<ZasterCurrency> zasterCurrency = new ComboBox<>("Logik", ZasterCurrency.values());

    private final Button cancel = new Button("Abbrechen");
    private final Button save = new Button("Speichern");
    private final Button delete = new Button("Löschen");

    private final BeanValidationBinder<Currency> binder;

    private final CurrencyService currencyService;

    private Currency currentCurrency;

    public CurrenciesView(CurrencyService currencyService) {
        this.currencyService = currencyService;
        addClassNames("currencies-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn(Currency::getCurrencyName).setHeader("Währung").setAutoWidth(true);
        grid.addColumn(Currency::getCurrencyCode).setHeader("Code").setAutoWidth(true);
        grid.addColumn(Currency::getCurrencyType).setHeader("Typ").setAutoWidth(true);
        grid.addColumn(Currency::getZasterCurrency).setHeader("Logik").setAutoWidth(true);
        grid.setItems(this::loadCurrencyPage);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(CURRENCY_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(CurrenciesView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Currency.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.bindInstanceFields(this);

        cancel.addClickListener(event -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(event -> saveCurrency());
        delete.addClickListener(event -> deleteCurrency());
        delete.setVisible(false);
    }

    private void saveCurrency() {
        try {
            if (this.currentCurrency == null) {
                this.currentCurrency = new Currency();
            }
            binder.writeBean(this.currentCurrency);
            currencyService.saveCurrency(this.currentCurrency);
            clearForm();
            refreshGrid();
            Notification.show("Währung gespeichert");
            UI.getCurrent().navigate(CurrenciesView.class);
        } catch (ObjectOptimisticLockingFailureException exception) {
            Notification n = Notification.show("Fehler beim Speichern. Daten wurden inzwischen geändert.");
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            Notification.show("Konnte Daten nicht speichern. Bitte Eingaben prüfen!");
        }
    }

    private void deleteCurrency() {
        try {
            currencyService.deleteCurrency(this.currentCurrency);
            clearForm();
            refreshGrid();
            Notification.show("Währung gelöscht");
            UI.getCurrent().navigate(CurrenciesView.class);
        } catch (DataIntegrityViolationException ex) {
            Notification n = Notification.show("Kann Währung nicht löschen, da sie noch verwendet wird.");
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (RuntimeException ex) {
            log.error("Unable to delete {}", this.currentCurrency, ex);
            Notification n = Notification.show("Fehler beim Löschen: " + ex.getLocalizedMessage());
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private Stream<Currency> loadCurrencyPage(Query<Currency, Void> query) {
        PageRequest pageable = PageRequest.of(query.getPage(), query.getPageSize(),
                VaadinSpringDataHelpers.toSpringDataSort(query));
        return currencyService.list(pageable).stream();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> currencyId = event.getRouteParameters().get(CURRENCY_ID);
        if (currencyId.isPresent()) {
            Optional<Currency> currency = currencyService.getCurrency(currencyId.get());
            if (currency.isPresent()) {
                populateForm(currency.get());
            } else {
                Notification.show(String.format("Währung nicht gefunden, ID = %s", currencyId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(CurrenciesView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        formLayout.add(currencyName, currencyCode, currencyType, zasterCurrency);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonLayout.add(save, cancel, delete);
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

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Currency currency) {
        currentCurrency = currency;
        binder.readBean(currentCurrency);
        delete.setVisible(currency != null && currency.getId() != null);
    }
}
