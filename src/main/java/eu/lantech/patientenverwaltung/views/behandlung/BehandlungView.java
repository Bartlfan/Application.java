package eu.lantech.patientenverwaltung.views.behandlung;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.lantech.patientenverwaltung.database.model.Arzt;
import eu.lantech.patientenverwaltung.database.model.Behandlung;
import eu.lantech.patientenverwaltung.database.model.Diagnose;
import eu.lantech.patientenverwaltung.database.model.Patient;
import eu.lantech.patientenverwaltung.repo.*;
import eu.lantech.patientenverwaltung.views.MainLayout;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@PageTitle("Behandlung")
@Route(value = "behandlung", layout = MainLayout.class)
public class BehandlungView extends VerticalLayout {

    BehandlungRepo behandlungRepo;
    TerminRepo terminRepo;
    PatientRepo patientRepo;
    DiagnoseRepo diagnoseRepo;
    ArztRepo arztRepo;
    KrankenkasseRepo krankenkasseRepo;
    VersicherungsArtRepo versicherungsArtRepo;

    Grid<Behandlung> grid = new Grid<>();
    Binder<Behandlung> binder = new Binder<>();
    Button btnSave;

    VerticalLayout vlArzt = new VerticalLayout();
    VerticalLayout vlPatientDiagnose = new VerticalLayout();

    public BehandlungView(BehandlungRepo behandlungRepo,TerminRepo terminRepo,PatientRepo patientRepo
                          ,DiagnoseRepo diagnoseRepo,ArztRepo arztRepo
                          ,KrankenkasseRepo krankenkasseRepo,VersicherungsArtRepo versicherungsArtRepo)
    {
        this.behandlungRepo = behandlungRepo;
        this.terminRepo = terminRepo;
        this.patientRepo = patientRepo;
        this.diagnoseRepo = diagnoseRepo;
        this.arztRepo = arztRepo;
        this.krankenkasseRepo = krankenkasseRepo;
        this.versicherungsArtRepo = versicherungsArtRepo;

        binder.setBean(new Behandlung());

        VerticalLayout vlMain = new VerticalLayout(vlArzt, vlPatientDiagnose);
        vlMain.setPadding(false);

        add(vlMain);

        initForm();
        initGrid();
        initButton();
        initForm2();

    }

    public void initGrid(){

        add(grid);
        grid.addSelectionListener(e -> {
            e.getFirstSelectedItem().ifPresentOrElse(
                    patient -> binder.setBean(patient),
                    () -> binder.setBean(new Behandlung()));
        });
        grid.addColumn(behandlung -> behandlung.getDateAsString()).setHeader("Datum");
        grid.addColumn(behandlung -> behandlung.getArzt().getTitelWithName()).setHeader("Behandelnder Arzt");
        grid.addColumn(behandlung -> behandlung.getDiagnose().getName()).setHeader("Diagnose");
        // Verschiedene Schreibweisen um Spalte Notizen hinzuzufügen
//        grid.addColumn(Behandlung::getNote).setHeader("Notizen");
        grid.addColumn(behandlung -> behandlung.getNote()).setHeader("Notiz");

    }
    public void initForm(){
        ComboBox<Arzt> arztComboBox = new ComboBox<>("Arzt");
        List<Arzt> arztList = arztRepo.findAll();
        arztComboBox.setItems(arztList);
        arztComboBox.setPlaceholder("Arzt wählen");
        arztComboBox.setClearButtonVisible(true);
        arztComboBox.setItemLabelGenerator(arzt -> arzt.getTitelWithName());
        arztComboBox.setMaxWidth("300px");

        ComboBox<Patient> patientComboBox = new ComboBox<>("Patient");
        List<Patient> patientList= patientRepo.findAll();
        patientComboBox.setItems(patientList);
        patientComboBox.setPlaceholder("Patient wählen");
        patientComboBox.setClearButtonVisible(true);
        patientComboBox.setItemLabelGenerator(patient -> patient.getFullName());
        patientComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null){
                List<Behandlung> behandlungList = behandlungRepo.findByPatient(event.getValue());
                grid.setItems(behandlungList);
            }else{
                grid.setItems(new ArrayList<>());
            }

        });
        patientComboBox.setMaxWidth("300px");

        DateTimePicker dateTime = new DateTimePicker("Termin");
        dateTime.setDatePlaceholder("Datum");
        dateTime.setTimePlaceholder("Uhrzeit");
        dateTime.setLocale(Locale.GERMANY);
        dateTime.setMin(LocalDateTime.now());
        dateTime.setStep(Duration.ofMinutes(30));
        dateTime.setMaxWidth("400px");

        ComboBox<Diagnose> diagnoseComboBox = new ComboBox<>("Diagnose");
        List<Diagnose> diagnoseList = diagnoseRepo.findAll();
        diagnoseComboBox.setItems(diagnoseList);
        diagnoseComboBox.setPlaceholder("Diagnose wählen");
        diagnoseComboBox.setClearButtonVisible(true);
        diagnoseComboBox.setItemLabelGenerator(diagnose -> diagnose.getName());
        diagnoseComboBox.setMaxWidth("400px");

        TextArea txtNotizen = new TextArea("Notizen","Behandlungs-Notizen");
        txtNotizen.setMaxWidth("400px");

        FormLayout flArzt = new FormLayout();
        flArzt.add(arztComboBox,dateTime);
        flArzt.setHeight("50px");
        flArzt.setResponsiveSteps(
                // Standardmäßig eine Spalte verwenden
                new FormLayout.ResponsiveStep("0",1),
                // Zwei Spalten wenn Breite von 500px überschritten wird
                new FormLayout.ResponsiveStep("500px",3)
        );

        FormLayout fLPatient = new FormLayout();
        fLPatient.add(patientComboBox,diagnoseComboBox,txtNotizen);
        fLPatient.setResponsiveSteps(
                // Standardmäßig eine Spalte verwenden
                new FormLayout.ResponsiveStep("0",1),
                // Zwei Spalten wenn Breite von 500px überschritten wird
                new FormLayout.ResponsiveStep("500px",3)
        );

        binder.forField(arztComboBox).asRequired()
                .withValidator(value -> value != null,"Bitte Arzt auswählen")
                .bind(Behandlung::getArzt,Behandlung::setArzt);
        binder.forField(dateTime).asRequired()
                .withValidator(value -> value != null, "Bitte Datum wählen")
                .withValidator(value -> value.getDayOfWeek().getValue()>= 1
                        && value.getDayOfWeek().getValue()<= 5, "Nur Wochentage")
                .withValidator(value -> value.getHour()>=8 && value.getHour()<= 18,"Uhrzeit von 8-18")
                .bind(Behandlung::getCreateDate,Behandlung::setCreateDate);
        binder.forField(patientComboBox).asRequired()
                .withValidator(value -> value != null,"Bitte Patienten wählen")
                .bind(Behandlung::getPatient,Behandlung::setPatient);
        binder.forField(diagnoseComboBox).asRequired()
                .withValidator(value -> value != null,"Bitte Diagnose wählen")
                .bind(Behandlung::getDiagnose,Behandlung::setDiagnose);
        binder.bind(txtNotizen,Behandlung::getNote,Behandlung::setNote);
        binder.addStatusChangeListener(e-> btnSave.setEnabled(!e.hasValidationErrors() && binder.isValid()));

        vlArzt.add(flArzt);
        vlArzt.setPadding(false);
        vlPatientDiagnose.add(fLPatient);
        vlPatientDiagnose.setPadding(false);
    }
    public void initForm2(){
        TextField txtPatient = new TextField("Patient");
        txtPatient.setReadOnly(true);
        TextField txtVersicherung = new TextField("Versicherung");
        txtVersicherung.setReadOnly(true);

        FormLayout fLInfo = new FormLayout();
        fLInfo.add(txtPatient,txtVersicherung);
        fLInfo.setWidthFull();
        fLInfo.setResponsiveSteps(
                // Standardmäßig eine Spalte verwenden
                new FormLayout.ResponsiveStep("0",1),
                // Zwei Spalten wenn Breite von 500px überschritten wird
                new FormLayout.ResponsiveStep("500px",3)
        );
        binder.bind(txtPatient,behandlung -> behandlung != null && behandlung.getPatient()
                != null ? behandlung.getPatient().getPatientKomplett() : null, null);
        binder.bind(txtVersicherung,behandlung -> behandlung != null && behandlung.getPatient()
                != null ? behandlung.getPatient().getVersicherung() : null, null);
        add(fLInfo);
    }
    public void initButton(){

        btnSave  = new Button("Speichern", VaadinIcon.DOWNLOAD.create());
        btnSave.setAutofocus(true);
        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.MATERIAL_CONTAINED,ButtonVariant.LUMO_LARGE);
        btnSave.setEnabled(false);
        btnSave.addClickListener(clickEvent -> {
            behandlungRepo.saveAndFlush(binder.getBean());
            binder.setBean(new Behandlung());
            Notification notHinweis = Notification.show("Gespeichert");
            notHinweis.setDuration(3000);
            notHinweis.setPosition(Notification.Position.MIDDLE);
            btnSave.setEnabled(false);
        });
        HorizontalLayout buttonLayout = new HorizontalLayout(btnSave);
        add(buttonLayout);
    }






}
