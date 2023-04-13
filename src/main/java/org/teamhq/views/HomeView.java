package org.teamhq.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.teamhq.data.Role;
import org.teamhq.data.service.EventService;
import org.teamhq.security.AuthenticatedUser;
import org.teamhq.views.login.LoginView;

@PageTitle("Home")
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class HomeView extends VerticalLayout implements BeforeEnterObserver {

    private final EventService eventService;

    private final AuthenticatedUser authenticatedUser;

    public HomeView(EventService eventService, AuthenticatedUser authenticatedUser) {
        this.eventService = eventService;
        this.authenticatedUser = authenticatedUser;


    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (authenticatedUser.get().isEmpty()) {
            UI.getCurrent().navigate(LoginView.class);
            return;
        }

        if (this.eventService.findAll().isEmpty()) {
            if (this.authenticatedUser.require().getRoles().contains(Role.ADMIN)) {
                add(new H2("Welcome Admin!"),
                        new H4("It seems that you haven't created any events yet! Please use the \"Add new event\" from the side menu to begin the journey! ;)"));
            } else {
                add(new H2("Welcome!"),
                        new H4("The admin haven't created any events yet! If you consider this to be mistake, please contact the system administrator!"));
            }
            return;
        }
        add(new H4("Events on the side cannot wait anymore to be selected!!!"));
    }
}
