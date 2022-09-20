package eu.lantech.patientenverwaltung.views.krankenkasse;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.lantech.patientenverwaltung.database.model.Krankenkasse;
import eu.lantech.patientenverwaltung.repo.KrankenkasseRepo;
import eu.lantech.patientenverwaltung.views.MainLayout;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Krankenkasse")
@Route(value = "krankenkasse", layout = MainLayout.class)
public class KrankenkasseView extends VerticalLayout {
    KrankenkasseRepo krankenkasseRepo;

    Grid<Krankenkasse> grid = new Grid<>();
    Binder<Krankenkasse> binder = new Binder<>();

    Button btnSave;

    public KrankenkasseView(KrankenkasseRepo krankenkasseRepo){
        this.krankenkasseRepo = krankenkasseRepo;
        binder.setBean(new Krankenkasse());

        initSearch();
        initGrid();
        initForm();
        initButton();
    }
    public void initGrid(){
        add(grid);
        // Click Event mit Abfrage:
        // Click auf einen Eintrag im Grid überträgt die Inhalte auf die Texboxen.
        // Beim erneuten Click auf den Eintrag wird alles wieder geleert
        grid.addSelectionListener(e-> {
            e.getFirstSelectedItem().ifPresentOrElse(
                    krankenkasse -> binder.setBean(krankenkasse),
                    () -> binder.setBean(new Krankenkasse()));

        });
        // Gridspalten erstellen und mit Überschrift / sortieren / verschieben editiert
        grid.addColumn(Krankenkasse::getName).setHeader("Name").setSortable(true);
        grid.addColumn(Krankenkasse::getAddress).setHeader("Adresse").setAutoWidth(true);
        grid.addColumn(Krankenkasse::getPhoneNumber).setHeader("Telefon");
        grid.addColumn(Krankenkasse::getEMail).setHeader("E-Mail").setResizable(true);
        List<Krankenkasse> list = krankenkasseRepo.findAll();
        grid.setItems(list);

    }
    public void initForm(){
        TextField txtName = new TextField("Name","Musterkasse");
        TextField txtStreet = new TextField("Straße","Musterstraße");
        TextField txthousenummber = new TextField("Hausnummer","1");
        TextField txtLocation = new TextField("Stadt","Musterstadt");
        TextField txtPostelCode = new TextField("Postleitzahl","12345");
        TextField txtPhonenummber = new TextField("Telefon","01234 / 567890");
        EmailField eMail = new EmailField("E-Mail","max@mustermann.de");

        FormLayout formLayout = new FormLayout();
        formLayout.add(txtName,txtStreet,txthousenummber,txtPostelCode,txtLocation,txtPhonenummber,eMail);
        formLayout.setResponsiveSteps(
                // Standardmäßig eine Spalte verwenden
                new FormLayout.ResponsiveStep("0",1),
                // Zwei Spalten wenn Breite von 500px überschritten wird
                new FormLayout.ResponsiveStep("500px",3)
        );

        binder.forField(txtName).asRequired()
                .withValidator(value-> Strings.isNotBlank(value) && value.length() >= 3, "Min. 3 Zeichen")
                .bind(Krankenkasse::getName,Krankenkasse::setName);
        binder.bind(txtStreet,Krankenkasse::getStreet,Krankenkasse::setStreet);
        binder.bind(txthousenummber,Krankenkasse::getHouseNumber,Krankenkasse::setHouseNumber);
        binder.bind(txtPostelCode,Krankenkasse::getPostalCode,Krankenkasse::setPostalCode);
        binder.bind(txtLocation,Krankenkasse::getLocation,Krankenkasse::setLocation);
        binder.bind(eMail,Krankenkasse::getEMail,Krankenkasse::setEMail);
        binder.forField(txtPhonenummber).asRequired()
                        .withValidator(value-> Strings.isNotBlank(value) && value.length() >=3, "Ungültige Tele.!")
                                .bind(Krankenkasse::getPhoneNumber,Krankenkasse::setPhoneNumber);
        binder.addStatusChangeListener(e -> btnSave.setEnabled(!e.hasValidationErrors() && binder.isValid()));
        add(formLayout);
    }
    public void initButton(){

        ConfirmDialog dialog = new ConfirmDialog();

        dialog.setHeader("Termin löschen?");
        dialog.setText("Wollen Sie diesen Termin wirklich Löschen?");

        dialog.setCancelable(true);
        dialog.setCancelText("Abbrechen");

        dialog.setConfirmText("Bestätigen");
        dialog.addConfirmListener(event -> {
            krankenkasseRepo.delete(binder.getBean());
            grid.setItems(krankenkasseRepo.findAll());
            Notification notHinweis = Notification.show("Krankenkasse gelöscht");
            notHinweis.setDuration(3000);
            notHinweis.setPosition(Notification.Position.MIDDLE);
            btnSave.setEnabled(false);
        });

        // Button erzeugt und Icon gesetzt
        Button btnNew = new Button("Neu", VaadinIcon.PLUS.create());
        btnNew.setAutofocus(true);
        // Button Hintergrund / Größe
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_SUCCESS
                ,ButtonVariant.LUMO_LARGE);
        // Click Eevent alle Textfelder werden geleert
        btnNew.addClickListener(clickEvent -> {
            binder.setBean(new Krankenkasse());
            btnSave.setEnabled(false);
        });

        btnSave = new Button("Speichern",VaadinIcon.DOWNLOAD.create());
        btnSave.setAutofocus(true);
        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.MATERIAL_CONTAINED
                ,ButtonVariant.LUMO_LARGE);
        btnSave.setEnabled(false);
        btnSave.addClickListener(clickEvent ->{
            krankenkasseRepo.saveAndFlush(binder.getBean());
            grid.setItems(krankenkasseRepo.findAll());
            binder.setBean(new Krankenkasse());
            Notification notHinweis = Notification.show("Gespeichert");
            notHinweis.setDuration(3000);
            notHinweis.setPosition(Notification.Position.MIDDLE);
            btnSave.setEnabled(false);
        });

        Button btnDelete = new Button("Löschen",VaadinIcon.TRASH.create());
        btnDelete.setAutofocus(true);
        btnDelete.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_ERROR
                ,ButtonVariant.LUMO_LARGE);
        btnDelete.getStyle().set("margin-inline-start", "auto");
        btnDelete.addClickListener(clickEvent ->{
            dialog.open();
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(btnNew,btnSave,btnDelete);
        buttonLayout.getStyle().set("flex-wrap","wrap");
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);

        setPadding(false);
        setAlignItems(Alignment.STRETCH);

        add(buttonLayout);
    }
    public void initSearch(){

        TextField txtSearch = new TextField();
        txtSearch.setPlaceholder("Suchen");
        txtSearch.setPrefixComponent(VaadinIcon.SEARCH.create());
        txtSearch.setClearButtonVisible(true);

        Button btnSearch = new Button("Suchen");
        btnSearch.setAutofocus(true);
        btnSearch.addThemeVariants(ButtonVariant.LUMO_LARGE);
        btnSearch.addClickListener(clickEvent ->{
            List<Krankenkasse> krankenkasseList = new ArrayList<>();
            if(Strings.isBlank(txtSearch.getValue())){
                krankenkasseList.addAll(krankenkasseRepo.findAll());
            }else{
                krankenkasseList.addAll(krankenkasseRepo.search2(txtSearch.getValue()));
            }
            grid.setItems(krankenkasseList);
        });
        HorizontalLayout horizontalLayout = new HorizontalLayout(txtSearch, btnSearch);
        horizontalLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        add(horizontalLayout);
    }

}
