package eu.lantech.patientenverwaltung.views.helloworld;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import eu.lantech.patientenverwaltung.views.MainLayout;

@PageTitle("Willkommen")
@Route(value = "hello", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class HelloWorldView extends VerticalLayout {

    public HelloWorldView() {
        this.getElement().getStyle().set("background-image","url('images/empfang.jpg')");

        setSizeFull();
        add();
    }

}
