package eu.lantech.patientenverwaltung.views.mitarbeiter;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.lantech.patientenverwaltung.database.model.*;
import eu.lantech.patientenverwaltung.repo.AnredeRepo;
import eu.lantech.patientenverwaltung.repo.MitarbeiterRepo;
import eu.lantech.patientenverwaltung.views.MainLayout;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@PageTitle("Mitarbeiter")
@Route(value = "mitarbeiter", layout = MainLayout.class)
public class MitarbeiterView extends Div {

    MitarbeiterRepo mitarbeiterRepo;
    AnredeRepo anredeRepo;

    Grid<Mitarbeiter> grid = new Grid<>();
    Binder<Mitarbeiter> binder = new Binder<>();

    VerticalLayout master = new VerticalLayout();
    VerticalLayout detail = new VerticalLayout();

    Button btnSave;

    public MitarbeiterView(MitarbeiterRepo mitarbeiterRepo,AnredeRepo anredeRepo) {
        this.mitarbeiterRepo = mitarbeiterRepo;
        this.anredeRepo = anredeRepo;
        binder.setBean(new Mitarbeiter());

        SplitLayout splitlayout = new SplitLayout(master,detail);

        master.setHeight("50%");

        splitlayout.setOrientation(SplitLayout.Orientation.VERTICAL);
        splitlayout.setSizeFull();

        master.setHeight("50%");

        add(splitlayout);
        setSizeFull();

        initSearch();
        initGrid();
        initForm();
        initButton();


    }
    private void initGrid(){

        // grid hinzufügen
        master.add(grid);
        // Beim Click auf ein Eintrag in das Grid, wird der Inhalt auf die Textfelder übertragen
        grid.addSelectionListener(e -> {
            e.getFirstSelectedItem().ifPresentOrElse(
                    patient -> binder.setBean(patient),
                    () -> binder.setBean(new Mitarbeiter()));
        });

        // Spalte im grid erzeugen
        grid.addColumn(a-> a.getSalutation() != null ? a.getSalutation().getSalutation() : "")
                        .setHeader("Anrede").setSortable(true);
        grid.addColumn(Mitarbeiter::getFirstname).setHeader("Vornamen");
        grid.addColumn(Mitarbeiter::getLastname).setHeader("Nachname").setSortable(true);
        grid.addColumn(Mitarbeiter::getDateOfBirthAsString).setHeader("Geburtsdatum").setSortable(true);
        grid.addColumn(Mitarbeiter::getAdresse).setHeader("Adresse");
        grid.addColumn(Mitarbeiter::getPhoneNumber).setHeader("Telefon");
        grid.addColumn(Mitarbeiter::getEMail).setHeader("E-Mail").setResizable(true);

        List<Mitarbeiter> list = mitarbeiterRepo.findAll();
        grid.setItems(list);

    }
    public void initSearch(){

        TextField txtSearch = new TextField();
        txtSearch.setPlaceholder("Suchen");
        txtSearch.setPrefixComponent(VaadinIcon.SEARCH.create());
        txtSearch.setClearButtonVisible(true);

        Button btnSearch = new Button("Suchen");
        btnSearch.setAutofocus(true);
        btnSearch.addThemeVariants(ButtonVariant.LUMO_LARGE);
        btnSearch.addClickListener(clickEvent -> {
            List<Mitarbeiter> mitarbeiters = new ArrayList<>();
            if (Strings.isBlank(txtSearch.getValue())){
                mitarbeiters.addAll(mitarbeiterRepo.findAll());
            }else{
                mitarbeiters.addAll(mitarbeiterRepo.search2(txtSearch.getValue()));
            }
            grid.setItems(mitarbeiters);

        });

        HorizontalLayout horizontalLayout = new HorizontalLayout(txtSearch, btnSearch);
        horizontalLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        master.add(horizontalLayout);
    }
    private void initForm(){
        ComboBox<Anrede> comboBoxAnrede = new ComboBox<>("Anrede");
        List<Anrede> anreden = anredeRepo.findAll();
        comboBoxAnrede.setPlaceholder("Anrede");
        comboBoxAnrede.setItems(anreden);
        comboBoxAnrede.setItemLabelGenerator(anrede -> anrede.getSalutation());
        if (!anreden.isEmpty()) {
            comboBoxAnrede.setValue(anreden.get(1));
        }

        TextField txtFirstName = new TextField("Vorname","Max");
        TextField txtLastName = new TextField("Nachname","Mustermann");

        DatePicker dateBirth = new DatePicker("Geburtstag");
        dateBirth.setPlaceholder("01.01.1974");
        dateBirth.setLocale(Locale.GERMANY);
        dateBirth.setMax(LocalDate.now());

        TextField txtLocation = new TextField("Wohnort", "Musterhausen");
        TextField txtPostalCode = new TextField("Postleitzahl","12345");
        TextField txtStreet = new TextField("Straße","Musterstraße");
        TextField txtHouseNumber = new TextField("Hausnummer","1");
        TextField txtPhoneNumber = new TextField("Telefon","01234 / 567890");
        EmailField eMail = new EmailField("E-Mail Adresse");
        eMail.setPlaceholder("max@mustermann.de");

        FormLayout formLayout = new FormLayout();
        formLayout.add(comboBoxAnrede,txtFirstName,txtLastName,dateBirth,txtStreet,txtHouseNumber
                ,txtPostalCode,txtLocation,txtPhoneNumber,eMail);
        formLayout.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 3)
        );
        // Stretch the username field over 2 columns
        //formLayout.setColspan(username, 2);
        binder.bind(comboBoxAnrede, Mitarbeiter::getSalutation, Mitarbeiter::setSalutation);
        binder.forField(txtFirstName).asRequired()
                .withValidator(s-> Strings.isNotBlank(s) && s.length() >=3, "Min. 3 Zeichen!")
                .bind(Mitarbeiter::getFirstname, Mitarbeiter::setFirstname);
        // setter auf null = feld nicht zu bearbeiten
        binder.forField(txtLastName).asRequired()
                .withValidator(s-> Strings.isNotBlank(s) && s.length() >=3, "Min. 3 Zeichen!")
                .bind(Mitarbeiter::getLastname, Mitarbeiter::setLastname);
        binder.bind(txtLocation, Mitarbeiter::getLocation, Mitarbeiter::setLocation);
        binder.forField(dateBirth).asRequired()
                .withValidator(value -> value != null && value.isBefore(LocalDate.now()), "Geburtsdatum")
                .bind(Mitarbeiter::getDateOfBirthAsDate, Mitarbeiter::setDateOfBirthAsDate);
        binder.bind(txtPostalCode, Mitarbeiter::getPostalCode, Mitarbeiter::setPostalCode);
        binder.bind(txtStreet, Mitarbeiter::getStreet, Mitarbeiter::setStreet);
        binder.bind(txtHouseNumber, Mitarbeiter::getHouseNumber, Mitarbeiter::setHouseNumber);
        binder.bind(txtPhoneNumber, Mitarbeiter::getPhoneNumber, Mitarbeiter::setPhoneNumber);
        binder.bind(eMail, Mitarbeiter::getEMail, Mitarbeiter::setEMail);
        binder.addStatusChangeListener(e-> btnSave.setEnabled(!e.hasValidationErrors() && binder.isValid()));

        detail.add(formLayout);
    }
    private void initButton(){

        ConfirmDialog dialog = new ConfirmDialog();

        dialog.setHeader("Termin löschen?");
        dialog.setText("Wollen Sie diesen Termin wirklich Löschen?");

        dialog.setCancelable(true);
        dialog.setCancelText("Abbrechen");

        dialog.setConfirmText("Bestätigen");
        dialog.addConfirmListener(event -> {
            mitarbeiterRepo.delete(binder.getBean());
            grid.setItems(mitarbeiterRepo.findAll());
            Notification notHinweis = Notification.show("Mitarbeiter gelöscht");
            notHinweis.setDuration(3000);
            notHinweis.setPosition(Notification.Position.MIDDLE);
            btnSave.setEnabled(false);
        });

        btnSave  = new Button("Speichern", VaadinIcon.DOWNLOAD.create());
        btnSave.setAutofocus(true);
        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.MATERIAL_CONTAINED,ButtonVariant.LUMO_LARGE);
        btnSave.setEnabled(false);
        btnSave.addClickListener(clickEvent -> {
            mitarbeiterRepo.saveAndFlush(binder.getBean());
            grid.setItems(mitarbeiterRepo.findAll());
            Notification notHinweis = Notification.show("Gespeichert");
            notHinweis.setDuration(3000);
            notHinweis.setPosition(Notification.Position.MIDDLE);
            btnSave.setEnabled(false);
        });

        Button btnDelete  = new Button("Löschen", VaadinIcon.TRASH.create());
        btnDelete.setAutofocus(true);
        btnDelete.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_ERROR,ButtonVariant.LUMO_LARGE);
        btnDelete.getStyle().set("margin-inline-start", "auto");
        btnDelete.addClickListener(clickEvent -> {
            dialog.open();
        });

        Button btnNew = new Button("Neu", VaadinIcon.PLUS.create());
        btnNew.setAutofocus(true);
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_SUCCESS,ButtonVariant.LUMO_LARGE);
        btnNew.addClickListener(clickEvent -> {
            binder.setBean(new Mitarbeiter());
            btnSave.setEnabled(false);
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(btnNew,btnSave,btnDelete);
        buttonLayout.getStyle().set("flex-wrap","wrap");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        detail.setPadding(false);
        detail.setAlignItems(FlexComponent.Alignment.STRETCH);

        detail.add(buttonLayout);
    }

}
