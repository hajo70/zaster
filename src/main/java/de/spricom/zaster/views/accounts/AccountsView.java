package de.spricom.zaster.views.accounts;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import de.spricom.zaster.data.Account;
import de.spricom.zaster.services.AccountService;
import de.spricom.zaster.views.MainLayout;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;
import java.util.stream.Stream;

@PageTitle("Konten")
@Route(value = "accounts/:accountId?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@Log4j2
public class AccountsView extends Div implements BeforeEnterObserver {

    private final String ACCOUNT_ID = "accountId";
    private final String ACCOUNT_EDIT_ROUTE_TEMPLATE = "accounts/%s/edit";

    private final Grid<Account> grid = new Grid<>(Account.class, false);

    private final TextField accountName = new TextField("Name");
    private final TextField accountCode = new TextField("Nummer");

    private final Button cancel = new Button("Abbrechen");
    private final Button save = new Button("Speichern");
    private final Button delete = new Button("Löschen");

    private final BeanValidationBinder<Account> binder;

    private final AccountService accountService;

    private Account currentAccount;

    public AccountsView(AccountService accountService) {
        this.accountService = accountService;
        addClassNames("accounts-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn(Account::getAccountName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(Account::getAccountCode).setHeader("Nummer").setAutoWidth(true);
        grid.setItems(this::loadAccountPage);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(ACCOUNT_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(AccountsView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Account.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.bindInstanceFields(this);

        cancel.addClickListener(event -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(event -> saveAccount());
        delete.addClickListener(event -> deleteAccount());
        delete.setVisible(false);
    }

    private void saveAccount() {
        try {
            if (this.currentAccount == null) {
                this.currentAccount = new Account();
            }
            binder.writeBean(this.currentAccount);
            accountService.saveAccount(this.currentAccount);
            clearForm();
            refreshGrid();
            Notification.show("Konto gespeichert");
            UI.getCurrent().navigate(AccountsView.class);
        } catch (ObjectOptimisticLockingFailureException exception) {
            Notification n = Notification.show(
                    "Fehler beim Speichern. Daten wurden inzwischen geändert.");
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            Notification.show("Konnte Daten nicht speichern. Bitte Eingaben prüfen!");
        }
    }

    private void deleteAccount() {
        try {
            accountService.deleteAccount(this.currentAccount);
            clearForm();
            refreshGrid();
            Notification.show("Konto gelöscht");
            UI.getCurrent().navigate(AccountsView.class);
        } catch (DataIntegrityViolationException ex) {
            Notification n = Notification.show(
                    "Kann Konto nicht löschen, da sie noch verwendet wird.");
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (RuntimeException ex) {
            log.error("Unable to delete {}", this.currentAccount, ex);
            Notification n = Notification.show(
                    "Fehler beim Löschen: " + ex.getLocalizedMessage());
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private Stream<Account> loadAccountPage(Query<Account, Void> query) {
        PageRequest pageable = PageRequest.of(query.getPage(), query.getPageSize(),
                VaadinSpringDataHelpers.toSpringDataSort(query));
        return accountService.list(pageable).stream();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> accountId = event.getRouteParameters().get(ACCOUNT_ID);
        if (accountId.isPresent()) {
            Optional<Account> account = accountService.getAccount(accountId.get());
            if (account.isPresent()) {
                populateForm(account.get());
            } else {
                Notification.show(String.format("Konto nicht gefunden, ID = %s", accountId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(AccountsView.class);
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
        formLayout.add(accountName, accountCode);

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

    private void populateForm(Account account) {
        currentAccount = account;
        binder.readBean(currentAccount);
        delete.setVisible(account != null && account.getId() != null);
    }

}
