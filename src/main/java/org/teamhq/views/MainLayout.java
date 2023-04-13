package org.teamhq.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.teamhq.components.event.EventDialog;
import org.teamhq.data.Role;
import org.teamhq.data.entity.Event;
import org.teamhq.data.entity.User;
import org.teamhq.data.service.EventService;
import org.teamhq.security.AuthenticatedUser;
import org.teamhq.views.event.EventView;
import org.teamhq.views.profile.ProfileView;

import java.io.ByteArrayInputStream;
import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;

    private final AuthenticatedUser authenticatedUser;
    private final AccessAnnotationChecker accessChecker;

    private final VerticalLayout eventMenu = new VerticalLayout();


    private final EventService eventService;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker, EventService eventService) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;
        this.eventService = eventService;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        eventMenu.removeAll();
        eventMenu.addClassName("menu-layout");
        Scroller scroller = new Scroller(eventMenu);

        H1 appName = new H1("Food Planning");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Button addNewEvent = new Button("Add new event", new Icon(VaadinIcon.PLUS));
        addNewEvent.setVisible(false);
        addNewEvent.setWidthFull();
        addNewEvent.addClickListener(click -> {
            EventDialog dialog = new EventDialog(eventService, this::addRouterLinkToMenu);
            dialog.open();
        });
        authenticatedUser.get().ifPresent(user -> addNewEvent.setVisible(user.getRoles().contains(Role.ADMIN)));

        addToDrawer(header, addNewEvent, scroller, createFooter());

        eventService.findAll().forEach(this::addRouterLinkToMenu);
    }

    private void addRouterLinkToMenu(Event event) {
        RouterLink eventLink = new RouterLink(event.getName(), EventView.class,
                event.getId());
        eventMenu.add(eventLink);
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getName());
            if (user.getProfilePicture() != null && user.getProfilePicture().length != 0) {
                StreamResource resource = new StreamResource("profile-pic",
                        () -> new ByteArrayInputStream(user.getProfilePicture()));
                avatar.setImageResource(resource);
            }
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Profile", e -> {
                UI.getCurrent().navigate(ProfileView.class);
            });
            userName.getSubMenu().addItem("Sign out", e -> {
                authenticatedUser.logout();
            });

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
