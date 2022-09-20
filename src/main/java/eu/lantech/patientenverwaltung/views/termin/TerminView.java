package eu.lantech.patientenverwaltung.views.termin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.lantech.patientenverwaltung.database.model.Arzt;
import eu.lantech.patientenverwaltung.database.model.Patient;
import eu.lantech.patientenverwaltung.database.model.Termin;
import eu.lantech.patientenverwaltung.repo.ArztRepo;
import eu.lantech.patientenverwaltung.repo.PatientRepo;
import eu.lantech.patientenverwaltung.repo.TerminRepo;
import eu.lantech.patientenverwaltung.views.MainLayout;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;


@PageTitle("Termin")
@Route(value = "termin", layout = MainLayout.class)
//@RouteAlias(value = "", layout = MainLayout.class)
public class TerminView extends Div{

    TerminRepo terminRepo;
    PatientRepo patientRepo;
    ArztRepo arztRepo;

    Grid<Termin> grid = new Grid<>();
    Binder<Termin> binder = new Binder<>();

    VerticalLayout master = new VerticalLayout();
    VerticalLayout detail = new VerticalLayout();
    HorizontalLayout masterHorizonal = new HorizontalLayout();
    VerticalLayout patientArztLayout = new VerticalLayout();

    Button btnSave;
    Button btnLight = new Button("Light");
    Button btnDark = new Button("Dark");

    HorizontalLayout themeLayout = new HorizontalLayout(btnLight,btnDark);

    public TerminView(TerminRepo terminRepo,PatientRepo patientRepo,ArztRepo arztRepo){
        this.terminRepo = terminRepo;
        this.patientRepo = patientRepo;
        this.arztRepo = arztRepo;

        binder.setBean(new Termin());

        SplitLayout splitLayout = new SplitLayout(master,detail);

        master.setHeight("50%");

        splitLayout.setOrientation(SplitLayout.Orientation.VERTICAL);
        splitLayout.setSizeFull();

        add(splitLayout);
        setSizeFull();

        initForm();
        initButton();
        initGrid();

    }

    public void initForm(){

        ComboBox<Arzt> comboBoxArzt = new ComboBox<>("Arzt");
        ComboBox<Patient> comboBoxPatient = new ComboBox<>("Patient");

        DateTimePicker date =new DateTimePicker("Termin");
        date.setDatePlaceholder("Datum");
        date.setTimePlaceholder("Uhrzeit");
        date.setLocale(Locale.GERMANY);
        date.setMin(LocalDateTime.now());
        date.setStep(Duration.ofMinutes(15));
        date.addValueChangeListener(e -> {
           if (e.getValue() != null) {
               LocalDateTime from = e.getValue();
               LocalDateTime to = from.plusMinutes(30);
               List<Integer> arztIds = terminRepo.findArztIdsWithTermin(from, to);
               List<Integer> patientIds = terminRepo.findPatientIdsWithTermin(from,to);
               if (arztIds.isEmpty() || (patientIds.isEmpty())) {
                   comboBoxArzt.setItems(arztRepo.findAll());
                   comboBoxPatient.setItems(patientRepo.findAll());
               } else{
                   List<Arzt> aerzteWithoutTermin = arztRepo.notIn(arztIds);
                   comboBoxArzt.setItems(aerzteWithoutTermin);
                   List<Patient> patientWithoutTermin = patientRepo.notIn(patientIds);
                   comboBoxPatient.setItems(patientWithoutTermin);
//                   comboBoxArzt.setHelperText("Kein Termine mehr möglich");
               }
           }
        });

        List<Patient> patientList = patientRepo.findAll();
        comboBoxPatient.setItems(patientList);
        comboBoxPatient.setMaxWidth("500px");
        comboBoxPatient.setPlaceholder("Patient auswählen");
        comboBoxPatient.setItemLabelGenerator(patient -> patient.getNameWithDateOfBirth());
        if (!patientList.isEmpty()) {
            comboBoxPatient.setValue(patientList.get(0));
        }


        List<Arzt> arztList =arztRepo.findAll();
        comboBoxArzt.setItems(arztList);
        comboBoxArzt.setMaxWidth("500px");
        comboBoxArzt.setPlaceholder("Arzt auswählen");
        comboBoxArzt.setItemLabelGenerator(arzt -> arzt.getTitelWithName());
        if(!arztList.isEmpty()){
            comboBoxArzt.setValue(arztList.get(0));
        }

        FormLayout formPatientArztLayout = new FormLayout();
        formPatientArztLayout.add(comboBoxPatient,comboBoxArzt);
        formPatientArztLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0",1),
                new FormLayout.ResponsiveStep("500px",2)
        );
        masterHorizonal.setSizeFull();
        VerticalLayout dateLayout = new VerticalLayout();
        dateLayout.add(date);
        dateLayout.setHeightFull();
        dateLayout.setWidth("450px");
        patientArztLayout.setHeightFull();
        patientArztLayout.setWidthFull();
        patientArztLayout.add(formPatientArztLayout);
        masterHorizonal.add(dateLayout,patientArztLayout);

        binder.forField(date).asRequired()
                .withValidator(value -> value != null && value.isAfter(LocalDateTime.now()),"Uhrzeit wählen")
                .withValidator(value -> value.getDayOfWeek().getValue() >= 1
                        && value.getDayOfWeek().getValue() <= 5, "Nur Wochentage")
                .withValidator(value -> value.getHour()>=8 && value.getHour()<=17,"Uhrzeit von 8-17")
                .bind(Termin::getDate,Termin::setDate);
        binder.forField(comboBoxPatient).asRequired()
                .withValidator(value-> value != null ,"Bitte Patient wählen")
                .bind(Termin::getPatient,Termin::setPatient);
        binder.bind(comboBoxArzt,Termin::getArzt,Termin::setArzt);
        binder.addStatusChangeListener(e-> btnSave.setEnabled(!e.hasValidationErrors() && binder.isValid()));

        master.add(masterHorizonal);
    }
    public void initButton(){

        ConfirmDialog dialog = new ConfirmDialog();

        dialog.setHeader("Termin löschen?");
        dialog.setText("Wollen Sie diesen Termin wirklich Löschen?");

        dialog.setCancelable(true);
        dialog.setCancelText("Abbrechen");

        dialog.setConfirmText("Bestätigen");
        dialog.addConfirmListener(event -> {
                    terminRepo.delete(binder.getBean());
                    grid.setItems(terminRepo.findAll());
                    Notification notHinweis = Notification.show("Termin gelöscht");
                    notHinweis.setDuration(3000);
                    notHinweis.setPosition(Notification.Position.MIDDLE);
                    btnSave.setEnabled(false);
                });
        Button btnNew = new Button("Neu",VaadinIcon.PLUS.create());
        btnNew.setAutofocus(true);
        btnNew.addThemeVariants(ButtonVariant.LUMO_SUCCESS,ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_LARGE);
        btnNew.addClickListener(clickEvent->{
           binder.setBean(new Termin());
           btnSave.setEnabled(false);
        });

        btnSave  = new Button("Speichern", VaadinIcon.DOWNLOAD.create());
        btnSave.setAutofocus(true);
        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.MATERIAL_CONTAINED,ButtonVariant.LUMO_LARGE);
        btnSave.setEnabled(false);
        btnSave.addClickListener(clickEvent -> {
           terminRepo.saveAndFlush(binder.getBean());
           grid.setItems(terminRepo.findAll());
           binder.setBean(new Termin());
            Notification notHinweis = Notification.show("Gespeichert");
            notHinweis.setDuration(3000);
            notHinweis.setPosition(Notification.Position.MIDDLE);
           btnSave.setEnabled(false);
        });

        Button btnDelete  = new Button("Löschen", VaadinIcon.TRASH.create());
        btnDelete.setAutofocus(true);
        btnDelete.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_ERROR,ButtonVariant.LUMO_LARGE);
        btnDelete.getStyle().set("margin-inline-start", "auto");
        btnDelete.addClickListener(clickEvent-> {
                    dialog.open();
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(btnNew,btnSave,btnDelete);
        buttonLayout.getStyle().set("flex-wrap","wrap");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        btnLight.addClickListener(e -> {
            UI.getCurrent().getElement().setAttribute("theme", "light");
        });

        btnDark.addClickListener(e -> {
            UI.getCurrent().getElement().setAttribute("theme", "dark");
        });

        patientArztLayout.setPadding(false);
        patientArztLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        patientArztLayout.add(buttonLayout);

    }
    public void initGrid(){
        // grid hinzufügen
        detail.add(grid,themeLayout);
        // Beim Click auf ein Eintrag in das Grid, wird der Inhalt auf die Textfelder übertragen
        grid.addSelectionListener(e -> {
            e.getFirstSelectedItem().ifPresentOrElse(
                    termin -> binder.setBean(termin),
                    () -> binder.setBean(new Termin()));
        });
        grid.addColumn(Termin::getDateAsString).setHeader("Uhrzeit").setSortable(true);

        Grid.Column<Termin> patientColumn = grid.addColumn(p-> p.getPatient() != null ? p.getPatient()
                .getNameWithDateOfBirth() : "");
        patientColumn.setHeader("Patient").setSortable(true);

        Grid.Column<Termin> arztColumn = grid.addColumn(a-> a.getArzt() != null ? a.getArzt()
                .getTitelWithName() : "");
       arztColumn.setHeader("Arzt").setSortable(true);

        List<Termin> list = terminRepo.findAll();
        grid.setItems(list);
    }

}
