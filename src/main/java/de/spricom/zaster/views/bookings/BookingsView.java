package de.spricom.zaster.views.bookings;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import de.spricom.zaster.data.Account;
import de.spricom.zaster.data.AccountCurrency;
import de.spricom.zaster.data.AccountCurrency_;
import de.spricom.zaster.data.Account_;
import de.spricom.zaster.data.Booking;
import de.spricom.zaster.data.Booking_;
import de.spricom.zaster.data.TrackingDateTime_;
import de.spricom.zaster.data.Transfer;
import de.spricom.zaster.data.Transfer_;
import de.spricom.zaster.services.BookingService;
import de.spricom.zaster.views.MainLayout;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@PageTitle("Buchungen")
@Route(value = "bookings/:accountId?", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@Uses(Icon.class)
public class BookingsView extends Div implements BeforeEnterObserver {

    private final String ACCOUNT_ID = "accountId";

    private final BookingService bookingService;

    private Grid<Booking> grid;
    private final Filters filters;

    public BookingsView(BookingService bookingService) {
        this.bookingService = bookingService;
        setSizeFull();
        addClassNames("bookings-view");

        filters = new Filters(this::refreshGrid);
        VerticalLayout layout = new VerticalLayout(filters, createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
    }

    static class Filters extends Div implements Specification<Booking> {

        private final TextField text = new TextField("Text");
        private final DatePicker startDate = new DatePicker("Buchungsdatum");
        private final DatePicker endDate = new DatePicker();
        private String accountId;

        public Filters(Runnable onSearch) {
            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);

            // Action buttons
            text.addBlurListener(ev -> onSearch.run());
            startDate.addBlurListener(ev -> onSearch.run());
            endDate.addBlurListener(ev -> onSearch.run());

            Button resetBtn = new Button("Leeren");
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetBtn.addClickListener(event -> {
                text.clear();
                startDate.clear();
                endDate.clear();
                accountId = null;
                onSearch.run();
            });
            Button searchBtn = new Button("Suchen");
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());

            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

            add(text, createDateRangeFilter(), actions);
        }

        private Component createDateRangeFilter() {
            startDate.setPlaceholder("Von");

            endDate.setPlaceholder("Bis");

            // For screen readers
            startDate.setAriaLabel("Datum von");
            endDate.setAriaLabel("Datum bis");

            FlexLayout dateRangeComponent = new FlexLayout(startDate, new Text(" – "), endDate);
            dateRangeComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
            dateRangeComponent.addClassName(LumoUtility.Gap.XSMALL);

            return dateRangeComponent;
        }


        @Override
        public Predicate toPredicate(Root<Booking> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            List<Predicate> predicates = new ArrayList<>();

            if (!text.isEmpty()) {
                String lowerCaseFilter = text.getValue().toLowerCase();
                Predicate descriptionMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                        "%" + lowerCaseFilter + "%");
                predicates.add(criteriaBuilder.or(descriptionMatch));
            }
            Path<LocalDate> bookedAtColumn = root.get(Booking_.BOOKED_AT).get(TrackingDateTime_.DATE);
            if (startDate.getValue() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(bookedAtColumn,
                        criteriaBuilder.literal(startDate.getValue())));
            }
            if (endDate.getValue() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.literal(endDate.getValue()),
                        bookedAtColumn));
            }

            if (accountId != null) {
                Subquery<Booking> subquery = query.subquery(Booking.class);
                Root<Booking> booking = subquery.from(Booking.class);
                Join<Object, Object> transfer = root.join(Booking_.TRANSFERS);
                Join<Object, Object> accountCurrency = transfer.join(Transfer_.ACCOUNT_CURRENCY);

                Path<Object> accountIdPath = accountCurrency.get(AccountCurrency_.ACCOUNT).get(Account_.ID);
                subquery.select(booking)
                        .distinct(true)
                        .where(criteriaBuilder.equal(accountIdPath, accountId));
                predicates.add(criteriaBuilder.in(root).value(subquery));
            }

            if (false) {
                // sample for filtering by amount
                Subquery<Booking> subquery = query.subquery(Booking.class);
                Root<Booking> booking = subquery.from(Booking.class);
                Join<Object, Object> transfer = root.join(Booking_.TRANSFERS);

                subquery.select(booking)
                        .distinct(true)
                        .where(criteriaBuilder.greaterThanOrEqualTo(
                                transfer.get(Transfer_.AMOUNT),
                                criteriaBuilder.literal(BigDecimal.valueOf(1000))
                        ));
                predicates.add(criteriaBuilder.in(root).value(subquery));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }
    }

    private Component createGrid() {
        grid = new Grid<>(Booking.class, false);
        grid.addColumn(this::bookingDate).setHeader("Buchungsdatum").setAutoWidth(true)
                .setSortProperty("bookedAt.date", "bookedAt.time");
        grid.addColumn(Booking::getDescription).setHeader("Beschreibung").setAutoWidth(true)
                .setResizable(true).setSortProperty("description");
        grid.addColumn(this::receipient).setHeader("Empfänger").setAutoWidth(true).setResizable(true);
        grid.addColumn(amountRenderer(this::amount)).setHeader("Betrag").setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END);
        grid.addColumn(amountRenderer(this::balance)).setHeader("Kontoststand").setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END);

        grid.setItems(query -> bookingService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
                filters).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COLUMN_BORDERS);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        return grid;
    }

    private LocalDate bookingDate(Booking booking) {
        return booking.getBookedAt().getDate();
    }

    private String receipient(Booking booking) {
        return booking.getTransfers().stream()
                .filter(transfer -> transfer.getPosition() == 2)
                .findAny()
                .map(Transfer::getAccountCurrency)
                .map(AccountCurrency::getAccount)
                .map(Account::getAccountName)
                .orElse(null);
    }

    private NumberRenderer amountRenderer(ValueProvider<Booking, BigDecimal> amountProvider) {
        return new NumberRenderer(amountProvider, NumberFormat.getCurrencyInstance());
    }

    private BigDecimal revenue(Booking booking) {
        BigDecimal amount = amount(booking);
        return amount.signum() == 1 ? amount : null;
    }

    private BigDecimal expenditure(Booking booking) {
        BigDecimal amount = amount(booking);
        return amount.signum() == -1 ? amount.negate() : null;
    }

    private BigDecimal amount(Booking booking) {
        return booking.getTransfers().stream()
                .filter(transfer -> transfer.getPosition() == 1)
                .findAny()
                .map(Transfer::getAmount).orElse(null);
    }

    private BigDecimal balance(Booking booking) {
        return booking.getTransfers().stream()
                .filter(transfer -> transfer.getPosition() == 1)
                .findAny()
                .map(Transfer::getBalance).orElse(null);
    }

    private void refreshGrid() {
        grid.getDataProvider().refreshAll();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> accountId = event.getRouteParameters().get(ACCOUNT_ID);
        if (accountId.isPresent()) {
            filters.accountId = accountId.get();
        }
    }
}
