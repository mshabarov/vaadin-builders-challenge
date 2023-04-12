package org.teamhq.views.profile;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.PermitAll;
import org.teamhq.data.entity.User;
import org.teamhq.data.repository.UserRepository;
import org.teamhq.security.AuthenticatedUser;
import org.teamhq.views.MainLayout;

import java.io.ByteArrayInputStream;

@PageTitle("Profile")
@Route(value = "profile", layout = MainLayout.class)
@PermitAll
public class ProfileView extends VerticalLayout {

    public ProfileView(AuthenticatedUser authenticatedUser, UserRepository userRepository) {
        User user = authenticatedUser.get().get();
        if (!userRepository.existsById(user.getId())) {
            user = userRepository.save(user);
        }

        Binder<User> userBinder = new Binder<>();
        userBinder.setBean(user);

        Avatar avatar = new Avatar();
        avatar.setHeight("100px");
        avatar.setWidth("100px");
        byte[] profilePicture = user.getProfilePicture();
        if (profilePicture != null && profilePicture.length > 0) {
            StreamResource profilePictureResource = new StreamResource("profile-pic", () -> new ByteArrayInputStream(profilePicture));
            avatar.setImageResource(profilePictureResource);
        }

        TextField nameField = new TextField("Name");
        userBinder.forField(nameField).bindReadOnly(User::getName);

        TextField emailField = new TextField("E-mail");
        userBinder.forField(emailField).bindReadOnly(User::getEmail);

        Button saveCommentButton = new Button("Save comment", click -> {
            userRepository.save(userBinder.getBean());
            Notification.show("Changes were successfully saved.");
        });
        saveCommentButton.setEnabled(false);

        TextArea commentsField = new TextArea("Comments", "You can specify any food preferences, allergies, or health related exceptions.");
        userBinder.forField(commentsField).bind(User::getComment, User::setComment);
        commentsField.setValueChangeMode(ValueChangeMode.EAGER);
        commentsField.addValueChangeListener(change -> saveCommentButton.setEnabled(true));
        commentsField.setHeight("150px");
        commentsField.setWidth("300px");

        VerticalLayout basicInformationLayout = new VerticalLayout(nameField, emailField);
        basicInformationLayout.setMargin(false);
        basicInformationLayout.setPadding(false);

        HorizontalLayout readOnlyLayout = new HorizontalLayout(basicInformationLayout, avatar);
        readOnlyLayout.setVerticalComponentAlignment(Alignment.CENTER, avatar);
        readOnlyLayout.setMargin(false);
        readOnlyLayout.setPadding(false);

        add(readOnlyLayout, commentsField, saveCommentButton);
    }
}
