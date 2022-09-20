package eu.lantech.patientenverwaltung.views.arzt;

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
import eu.lantech.patientenverwaltung.database.model.Arzt;
import eu.lantech.patientenverwaltung.repo.AnredeRepo;
import eu.lantech.patientenverwaltung.repo.ArztRepo;
import eu.lantech.patientenverwaltung.views.MainLayout;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@PageTitle("Arzt")
@Route(value = "arzt", layout = MainLayout.class)
public class ArztView extends VerticalLayout {

    ArztRepo arztRepo;
    AnredeRepo anredeRepo;
    // Grid erzeugen
    Grid<Arzt> grid = new Grid<>();
    // Verbindung Grid-Eintrag in Textfeld
    Binder<Arzt> binder = new Binder<>();

    VerticalLayout master = new VerticalLayout();
    VerticalLayout detail = new VerticalLayout();

    Button btnSave;

    public ArztView(ArztRepo arztRepo,AnredeRepo anredeRepo){
        this.arztRepo = arztRepo;
        this.anredeRepo = anredeRepo;
        binder.setBean(new Arzt());

        SplitLayout splitlayout = new SplitLayout(master,detail);

        master.setHeight("50%");

        splitlayout.setOrientation(SplitLayout.Orientation.VERTICAL);
        splitlayout.setSizeFull();

        add(splitlayout);
        setSizeFull();

        initSearch();
        initGrid();
        initForm();
        initButton();
    }

    public void initGrid(){
        master.add(grid);
        // Click Event mit Abfrage:
        // Click auf einen Eintrag im Grid überträgt die Inhalte auf die Texboxen.
        // Beim erneuten Click auf den Eintrag wird alles wieder geleert
        grid.addSelectionListener(e-> {
            e.getFirstSelectedItem().ifPresentOrElse(
                    arzt -> binder.setBean(arzt),
                    () -> binder.setBean(new Arzt()));

        });
        // Gridspalten erstellen und mit Überschrift / sortieren / verschieben editiert
        grid.addColumn(a-> a.getSalutation() != null ? a.getSalutation().getSalutation() : "")
                .setHeader("Anrede").setSortable(true);
        grid.addColumn(Arzt::getTitle).setHeader("Titel").setSortable(true);
        grid.addColumn(Arzt::getFirstname).setHeader("Vorname");
        grid.addColumn(Arzt::getLastname).setHeader("Nachname").setSortable(true);
        grid.addColumn(Arzt::getAddress).setHeader("Adresse").setAutoWidth(true);
        grid.addColumn(Arzt::getPhoneNumber).setHeader("Telefon");
        grid.addColumn(Arzt::getEMail).setHeader("E-Mail").setResizable(true);
        List<Arzt> list = arztRepo.findAll();
        grid.setItems(list);

    }
    public void initForm(){
        // Combobox ereugen und mit Inhalt von Class Anrede füllen
        ComboBox<Anrede> comboBoxAnrede = new ComboBox<>("Anrede");
        List<Anrede> anreden = anredeRepo.findAll();
        comboBoxAnrede.setPlaceholder("Anrede");
        comboBoxAnrede.setItems(anreden);
        comboBoxAnrede.setItemLabelGenerator(anrede -> anrede.getSalutation());
        if(!anreden.isEmpty()){
            comboBoxAnrede.setValue(anreden.get(1));
        }

        TextField txtTitel = new TextField("Titel", "Dr.");
        TextField txtFirstName = new TextField("Vorname","Max");
        txtFirstName.setValueChangeMode(ValueChangeMode.EAGER);
        TextField txtLastName = new TextField("Nachname","Mustermann");
        txtLastName.setValueChangeMode(ValueChangeMode.EAGER);

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
        formLayout.add(comboBoxAnrede,txtTitel,txtFirstName,txtLastName,dateBirth,txtStreet,txtHouseNumber
                ,txtPostalCode,txtLocation,txtPhoneNumber,eMail);
        formLayout.setResponsiveSteps(
                // Standardmäßig eine Spalte verwenden
                new FormLayout.ResponsiveStep("0", 1),
                // Zwei Spalten wenn Breite von 500px überschritten wird
                new FormLayout.ResponsiveStep("500px", 3)
        );
        detail.add(formLayout);

        // Binder füllt Textfelder mit Inhalt aus Grid
        // Arzt::getSalutation holt den Eintrag
        // Arzt::setSalutation setzt den Eintrag
        binder.bind(comboBoxAnrede,Arzt::getSalutation,Arzt::setSalutation);
        binder.bind(txtTitel,Arzt::getTitle,Arzt::setTitle);
        binder.forField(txtFirstName).asRequired()
                .withValidator(s -> Strings.isNotBlank(s) && s.length() >=3, "Mind. 3 Zeichen!")
            .bind(Arzt::getFirstname, Arzt::setFirstname);
        binder.forField(txtLastName).asRequired().withValidator
                (s-> Strings.isNotBlank(s) && s.length() >=3, "Min. 3 Zeichen!")
                .bind(Arzt::getLastname,Arzt::setLastname);
        binder.forField(dateBirth).asRequired()
                .withValidator(value-> value != null && value.isBefore(LocalDate.now()),"Geburtsdatum")
                .bind(Arzt::getDateOfBirthAsDate,Arzt::setDateOfBirthAsDate);
        binder.bind(txtPostalCode,Arzt::getPostalCode,Arzt::setPostalCode);
        binder.bind(txtLocation,Arzt::getLocation,Arzt::setLocation);
        binder.bind(txtStreet,Arzt::getStreet,Arzt::setStreet);
        binder.bind(txtHouseNumber,Arzt::getHouseNumber,Arzt::setHouseNumber);
        binder.bind(txtPhoneNumber,Arzt::getPhoneNumber,Arzt::setPhoneNumber);
        binder.bind(eMail,Arzt::getEMail,Arzt::setEMail);
        binder.addStatusChangeListener(e -> btnSave.setEnabled(!e.hasValidationErrors() && binder.isValid()));
    }
    public void initButton(){

        ConfirmDialog dialog = new ConfirmDialog();

        dialog.setHeader("Arzt löschen?");
        dialog.setText("Wollen Sie diesen Arzt wirklich Löschen?");

        dialog.setCancelable(true);
        dialog.setCancelText("Abbrechen");

        dialog.setConfirmText("Bestätigen");
        dialog.addConfirmListener(event -> {
            arztRepo.delete(binder.getBean());
            grid.setItems(arztRepo.findAll());
            Notification notHinweis = Notification.show("Arzt gelöscht");
            notHinweis.setDuration(3000);
            notHinweis.setPosition(Notification.Position.MIDDLE);
            btnSave.setEnabled(false);
        });

        // Button erzeugt und Icon gesetzt
        Button btnNew = new Button("Neu", VaadinIcon.PLUS.create());
        btnNew.setAutofocus(true);
        // Button Hintergrund / Größe
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_SUCCESS,ButtonVariant.LUMO_LARGE);
        // Click Eevent alle Textfelder werden geleert
        btnNew.addClickListener(clickEvent -> {
            binder.setBean(new Arzt());
            btnSave.setEnabled(false);
        });

        btnSave  = new Button("Speichern", VaadinIcon.DOWNLOAD.create());
        btnSave.setAutofocus(true);
        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.MATERIAL_CONTAINED,ButtonVariant.LUMO_LARGE);
        btnSave.setEnabled(false);
        btnSave.addClickListener(clickEvent -> {
            arztRepo.saveAndFlush(binder.getBean());
            grid.setItems(arztRepo.findAll());
            binder.setBean(new Arzt());
            Notification notHinweis = Notification.show("Gespeichert");
            notHinweis.setDuration(3000);
            notHinweis.setPosition(Notification.Position.MIDDLE);
            btnSave.setEnabled(false);
        });

        Button btnDelete  = new Button("Löschen", VaadinIcon.TRASH.create());
        btnDelete.setAutofocus(true);
        btnDelete.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_ERROR,ButtonVariant.LUMO_LARGE);
        // Position des Button bestimmt
        btnDelete.getStyle().set("margin-inline-start", "auto");
        btnDelete.addClickListener(clickEvent -> {
            dialog.open();
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
            List<Arzt> arzts = new ArrayList<>();
            if (Strings.isBlank(txtSearch.getValue())) {
                arzts.addAll(arztRepo.findAll());
            }else{
               arzts.addAll(arztRepo.search2(txtSearch.getValue()));
            }
            grid.setItems(arzts);
        });
        HorizontalLayout horizontalLayout = new HorizontalLayout(txtSearch, btnSearch);
        horizontalLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        master.add(horizontalLayout);

    }

}
