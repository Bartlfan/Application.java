package eu.lantech.patientenverwaltung.views.diagnose;

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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.lantech.patientenverwaltung.database.model.Diagnose;
import eu.lantech.patientenverwaltung.repo.DiagnoseRepo;
import eu.lantech.patientenverwaltung.views.MainLayout;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Diagnose")
@Route(value = "diagnose", layout = MainLayout.class)
public class DiagnoseView extends VerticalLayout {

    DiagnoseRepo diagnoseRepo;
    // Grid erzeugen
    Grid<Diagnose> grid = new Grid<>();
    // Verbindung Grid Eintrag in Textfeld
    Binder<Diagnose> binder = new Binder<>();

    Button btnSave;
    TextField txtName;

    public DiagnoseView(DiagnoseRepo diagnoseRepo){
        this.diagnoseRepo = diagnoseRepo;
        binder.setBean(new Diagnose());

        setSizeFull();

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
                    diagnose -> binder.setBean(diagnose),
                    () -> binder.setBean(new Diagnose()));
        });

        grid.addColumn(Diagnose::getName).setHeader("Name").setSortable(true);
        grid.addColumn(Diagnose::getBeschreibung).setHeader("Beschreibung").setResizable(true);
        List<Diagnose> list= diagnoseRepo.findAll();
        grid.setItems(list);
    }
    public void initForm(){
        txtName = new TextField("Name","Diagnose");
        txtName.setValueChangeMode(ValueChangeMode.EAGER);

        TextArea txtBeschreibung = new TextArea("Beschreibung","Beschreibungstext");
        FormLayout formLayout = new FormLayout();
        formLayout.add(txtName,txtBeschreibung);
        formLayout.setResponsiveSteps(
                // Standardmäßig eine Spalte verwenden
                new FormLayout.ResponsiveStep("0",1),
                // Zwei Spalten wenn Breite von 500px überschritten wird
                new FormLayout.ResponsiveStep("500px",2)
        );

         binder.forField(txtName).asRequired()
                    .withValidator(value -> Strings.isNotBlank(value) && value.length() >=3,
                            "Min. 3 Zeichen!").bind(Diagnose::getName,Diagnose::setName);
         binder.forField(txtBeschreibung).bind(Diagnose::getBeschreibung,Diagnose::setBeschreibung);
         binder.addStatusChangeListener(e-> btnSave.setEnabled(!e.hasValidationErrors() && binder.isValid()));
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
            diagnoseRepo.delete(binder.getBean());
            grid.setItems(diagnoseRepo.findAll());
            Notification notHinweis = Notification.show("Diagnose gelöscht");
            notHinweis.setDuration(3000);
            notHinweis.setPosition(Notification.Position.MIDDLE);
            btnSave.setEnabled(false);
        });

        Button btnNew = new Button("Neu", VaadinIcon.PLUS.create());
        btnNew.setAutofocus(true);
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_SUCCESS,ButtonVariant.LUMO_LARGE,ButtonVariant.MATERIAL_CONTAINED);
        btnNew.addClickListener(clickEvent -> {
           binder.setBean(new Diagnose());
           btnSave.setEnabled(false);
        });

        btnSave = new Button("Speichern",VaadinIcon.DOWNLOAD.create());
        btnSave.setAutofocus(true);
        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.MATERIAL_CONTAINED,ButtonVariant.LUMO_LARGE);
        btnSave.setEnabled(false);
        btnSave.addClickListener(clickEvent ->{
                diagnoseRepo.saveAndFlush(binder.getBean());
                grid.setItems(diagnoseRepo.findAll());
                binder.setBean(new Diagnose());
            Notification notHinweis = Notification.show("Gespeichert");
            notHinweis.setDuration(3000);
            notHinweis.setPosition(Notification.Position.MIDDLE);
                btnSave.setEnabled(false);
        });

        Button btnDelete = new Button("Löschen",VaadinIcon.TRASH.create());
        btnDelete.setAutofocus(true);
        btnDelete.getStyle().set("margin-inline-start","auto");
        btnDelete.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_ERROR,ButtonVariant.LUMO_LARGE);
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
    private void initSearch(){
        TextField txtSearch = new TextField();
        txtSearch.setPlaceholder("Suchen");
        txtSearch.setPrefixComponent(VaadinIcon.SEARCH.create());
        txtSearch.setClearButtonVisible(true);

        Button btnSearch  = new Button("Suchen");
        btnSearch.setAutofocus(true);
        btnSearch.addThemeVariants(ButtonVariant.LUMO_LARGE);
        btnSearch.addClickListener(clickEvent -> {
            List<Diagnose> diagnoseList = new ArrayList<>();
            if (Strings.isBlank(txtSearch.getValue())) {
                diagnoseList.addAll(diagnoseRepo.findAll());
            }else{
                diagnoseList.addAll(diagnoseRepo.search2(txtSearch.getValue()));
            }
            grid.setItems(diagnoseList);
        });
        HorizontalLayout horizontalLayout = new HorizontalLayout(txtSearch, btnSearch);
        horizontalLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        add(horizontalLayout);
    }

}
