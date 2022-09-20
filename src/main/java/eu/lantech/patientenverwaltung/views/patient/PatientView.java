package eu.lantech.patientenverwaltung.views.patient;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.lantech.patientenverwaltung.database.model.Anrede;
import eu.lantech.patientenverwaltung.database.model.Krankenkasse;
import eu.lantech.patientenverwaltung.database.model.Patient;
import eu.lantech.patientenverwaltung.database.model.VersicherungsArt;
import eu.lantech.patientenverwaltung.repo.AnredeRepo;
import eu.lantech.patientenverwaltung.repo.KrankenkasseRepo;
import eu.lantech.patientenverwaltung.repo.PatientRepo;
import eu.lantech.patientenverwaltung.repo.VersicherungsArtRepo;
import eu.lantech.patientenverwaltung.views.MainLayout;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@PageTitle("Patienten")
@Route(value = "patient", layout = MainLayout.class)
public class PatientView extends VerticalLayout {

    PatientRepo patientRepo;
    AnredeRepo anredeRepo;
    KrankenkasseRepo krankenkasseRepo;
    VersicherungsArtRepo versicherungsArtRepo;

    Grid<Patient> grid = new Grid<>();
    Binder<Patient> binder = new Binder<>();

    VerticalLayout master = new VerticalLayout();
    VerticalLayout detail = new VerticalLayout();

    Button btnSave;

    public PatientView(PatientRepo patientRepo, AnredeRepo anredeRepo, KrankenkasseRepo krankenkasseRepo,
                       VersicherungsArtRepo versicherungsArtRepo) {
        this.patientRepo = patientRepo;
        this.anredeRepo = anredeRepo;
        this.krankenkasseRepo = krankenkasseRepo;
        this.versicherungsArtRepo = versicherungsArtRepo;
        binder.setBean(new Patient());

        SplitLayout splitlayout = new SplitLayout(master,detail);

        master.setHeight("50%");

        splitlayout.setOrientation(SplitLayout.Orientation.VERTICAL);
        splitlayout.setSizeFull();

        add(splitlayout);
        setSizeFull();
        // add(new H2("Das ist eine Überschrift"));
        initSearch();
        initGrid();
        initForm();
        initButton();
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
        txtFirstName.setValueChangeMode(ValueChangeMode.EAGER);
        TextField txtLastName = new TextField("Nachname","Mustermann");
        txtLastName.setValueChangeMode(ValueChangeMode.EAGER);

        DatePicker dateBirth = new DatePicker("Geburtstag");
        dateBirth.setPlaceholder("01.01.1974");
        dateBirth.setLocale(Locale.GERMANY);
        dateBirth.setMax(LocalDate.now());

        ComboBox<Krankenkasse> comboBoxVersicherung = new ComboBox<>("Versicherung");
        List<Krankenkasse> krankenkasse = krankenkasseRepo.findAll();
        comboBoxVersicherung.setPlaceholder("Krankenkasse");
        comboBoxVersicherung.setItems(krankenkasse);
        comboBoxVersicherung.setItemLabelGenerator(krankenkasse1 -> krankenkasse1.getName());
        if (!krankenkasse.isEmpty()) {
            comboBoxVersicherung.setValue(krankenkasse.get(0));
        }

        ComboBox<VersicherungsArt> comboBoxVersicherungsArt = new ComboBox<>("Versicherungs-Art");
        List<VersicherungsArt> versicherungsArt = versicherungsArtRepo.findAll();
        comboBoxVersicherungsArt.setPlaceholder("Versicherungsart");
        comboBoxVersicherungsArt.setItems(versicherungsArt);
        comboBoxVersicherungsArt.setItemLabelGenerator(versicherungsArt1 -> versicherungsArt1.getType());
        if(!versicherungsArt.isEmpty()){
            comboBoxVersicherungsArt.setValue(versicherungsArt.get(0));
        }

        TextField txtLocation = new TextField("Wohnort", "Musterhausen");
        TextField txtPostalCode = new TextField("Postleitzahl","12345");
        TextField txtStreet = new TextField("Straße","Musterstraße");
        TextField txtHouseNumber = new TextField("Hausnummer","1");
        TextField txtPhoneNumber = new TextField("Telefon","01234 / 567890");
        EmailField eMail = new EmailField("E-Mail Adresse");
        eMail.setPlaceholder("max@mustermann.de");

        FormLayout formLayout = new FormLayout();
        formLayout.add(comboBoxAnrede,txtFirstName,txtLastName,dateBirth,comboBoxVersicherung,comboBoxVersicherungsArt,
                        txtStreet,txtHouseNumber,txtPostalCode,txtLocation,txtPhoneNumber,eMail);
        formLayout.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 3)
        );

        binder.bind(comboBoxAnrede, Patient::getSalutation, Patient::setSalutation);
        binder.forField(txtFirstName).asRequired()
                .withValidator(s-> Strings.isNotBlank(s) && s.length() >=3, "Min. 3 Zeichen!")
                .bind(Patient::getFirstname, Patient::setFirstname);
        // setter auf null = feld nicht zu bearbeiten
        binder.forField(txtLastName).asRequired()
                .withValidator(s-> Strings.isNotBlank(s) && s.length() >=3, "Min. 3 Zeichen!")
                .bind(Patient::getLastname, Patient::setLastname);
        binder.bind(txtLocation, Patient::getLocation, Patient::setLocation);
        binder.forField(dateBirth).asRequired()
                .withValidator(value -> value != null && value.isBefore(LocalDate.now()), "Geburtsdatum")
                .bind(Patient::getDateOfBirthAsDate, Patient::setDateOfBirthAsDate);
        binder.bind(comboBoxVersicherung, Patient::getName, Patient::setName);
        binder.bind(comboBoxVersicherungsArt,Patient::getType, Patient::setType);
        binder.bind(txtPostalCode, Patient::getPostalCode, Patient::setPostalCode);
        binder.bind(txtStreet, Patient::getStreet, Patient::setStreet);
        binder.bind(txtHouseNumber, Patient::getHouseNumber, Patient::setHouseNumber);
        binder.bind(txtPhoneNumber, Patient::getPhoneNumber, Patient::setPhoneNumber);
        binder.bind(eMail, Patient::getEMail, Patient::setEMail);
        binder.addStatusChangeListener(e-> btnSave.setEnabled(!e.hasValidationErrors() && binder.isValid()));

        detail.add(formLayout);
    }
    private void initGrid(){

        // grid hinzufügen
        master.add(grid);
        // Beim Click auf ein Eintrag in das Grid, wird der Inhalt auf die Textfelder übertragen
        grid.addSelectionListener(e -> {
           e.getFirstSelectedItem().ifPresentOrElse(
                   patient -> binder.setBean(patient),
                   () -> binder.setBean(new Patient()));
        });

        // Spalte im grid erzeugen
        Grid.Column<Patient> salutation = grid.addColumn(p -> p.getSalutation() != null ? p.getSalutation().getSalutation() : "");
        // Spalten anpassen / Überschrift / verschieben / sortieren
        salutation.setHeader("Anrede");
        salutation.setAutoWidth(true);
        salutation.setSortable(true);
        Grid.Column<Patient> column = grid.addColumn(Patient::getFirstname);
        column.setHeader("Vorname");
        column.setAutoWidth(true);
        column.setSortable(true);
        Grid.Column<Patient> column1 = grid.addColumn(Patient::getLastname);
        column1.setHeader("Nachname");
        column1.setAutoWidth(true);
        column1.setSortable(true);
        Grid.Column<Patient> column2 = grid.addColumn(Patient::getDateOfBirthAsString);
        column2.setHeader("Geburtstag");
        column2.setSortable(true);
        column2.setAutoWidth(true);
        Grid.Column<Patient> versicherung = grid.addColumn(p ->p.getName() != null ? p.getName().getName() : "");
        versicherung.setHeader("Versicherung");
        versicherung.setSortable(true);
        versicherung.setAutoWidth(true);
        Grid.Column<Patient> versicherungsart = grid.addColumn(p ->p.getType() != null ? p.getType().getType() : "");
        versicherungsart.setHeader("Versicherungs-Art");
        versicherungsart.setSortable(true);
        versicherungsart.setAutoWidth(true);
        // alternativ
        // grid.addColumn(Patient::getDateOfBirth).setHeader("Geburtstag");
        /*Grid.Column<Patient> column3 = grid.addColumn(Patient::getLocation);
        column3.setHeader("Wohnort");
        column3.setAutoWidth(true);
        column3.setSortable(true);
        Grid.Column<Patient> column4 = grid.addColumn(Patient::getPostalCode);
        column4.setHeader("Postleitzahl");
        column4.setAutoWidth(true);
        column4.setSortable(true);
        */
        grid.addColumn(Patient::getAddress).setHeader("Adresse");

        /*Grid.Column<Patient> column5 = grid.addColumn(Patient::getStreet);
        column5.setHeader("Straße");
        column5.setAutoWidth(true);
        // column5.setSortable(true);
        Grid.Column<Patient> column6 = grid.addColumn(Patient::getHouseNumber);
        column6.setHeader("Hausnummer");
        column6.setAutoWidth(true);
        // column6.setSortable(true);
        */
        Grid.Column<Patient> column7 = grid.addColumn(Patient::getPhoneNumber);
        column7.setHeader("Telefon");
        column7.setAutoWidth(true);
        /*
        Grid.Column<Patient> column8 = grid.addColumn(Patient::getEMail);
        column8.setHeader("E-Mail");
        column8.setResizable(true);
        */
        List<Patient> list = patientRepo.findAll();
        grid.setItems(list);

    }
    private void initButton(){

        ConfirmDialog dialog = new ConfirmDialog();

        dialog.setHeader("Patient löschen?");
        dialog.setText("Wollen Sie diesen Patient wirklich Löschen?");

        dialog.setCancelable(true);
        dialog.setCancelText("Abbrechen");

        dialog.setConfirmText("Bestätigen");
        dialog.addConfirmListener(event -> {
            patientRepo.delete(binder.getBean());
            grid.setItems(patientRepo.findAll());
            Notification notHinweis = Notification.show("Patient gelöscht");
            notHinweis.setDuration(3000);
            notHinweis.setPosition(Notification.Position.MIDDLE);
            btnSave.setEnabled(false);
        });

        btnSave  = new Button("Speichern", VaadinIcon.DOWNLOAD.create());
        btnSave.setAutofocus(true);
        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.MATERIAL_CONTAINED,ButtonVariant.LUMO_LARGE);
        btnSave.setEnabled(false);
        btnSave.addClickListener(clickEvent -> {
                patientRepo.saveAndFlush(binder.getBean());
                grid.setItems(patientRepo.findAll());
                binder.setBean(new Patient());
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
            binder.setBean(new Patient());
            btnSave.setEnabled(false);
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(btnNew,btnSave,btnDelete);
        buttonLayout.getStyle().set("flex-wrap","wrap");
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);

        detail.setPadding(false);
        detail.setAlignItems(Alignment.STRETCH);

        detail.add(buttonLayout);
    }
    private void initSearch(){
        TextField txtSearch = new TextField();
        txtSearch.setPlaceholder("Suchen");
        txtSearch.setPrefixComponent(VaadinIcon.SEARCH.create());
        txtSearch.setClearButtonVisible(true);

        Button btnSearch  = new Button("Suchen");
        btnSearch.setAutofocus(true);
        btnSearch.addThemeVariants(ButtonVariant.LUMO_LARGE);
        btnSearch.addClickListener(clickEvent -> {
            List<Patient> patients = new ArrayList<>();
            if (Strings.isBlank(txtSearch.getValue())) {
                patients.addAll(patientRepo.findAll());
            }else{
                patients.addAll(patientRepo.search2(txtSearch.getValue()));
            }
            grid.setItems(patients);
        });
        HorizontalLayout horizontalLayout = new HorizontalLayout(txtSearch, btnSearch);
        horizontalLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        master.add(horizontalLayout);
    }

}
