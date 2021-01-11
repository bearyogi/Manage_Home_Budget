package com.university.project.backend.service;

import com.university.project.backend.entity.User;
import com.university.project.backend.repository.UserRepository;
import com.university.project.ui.views.HomeView;
import com.university.project.ui.views.MainView;
import com.university.project.utils.Constants;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {
    @Autowired
    private final UserRepository userRepository;

    public AuthService(@Autowired UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void authenticate(String username, String password) throws AuthException {
        User user = userRepository.getByUsername(username);
        if (user != null && user.checkPassword(password)) {
            VaadinSession.getCurrent().setAttribute(Constants.USER_ID, user.getId());
            VaadinSession.getCurrent().setAttribute(User.class, user);
            createRoutes();
        } else {
            throw new AuthException();
        }
    }

    private void createRoutes() {
        getAuthorizedRoutes()
                .forEach(route ->
                        RouteConfiguration.forSessionScope().setRoute(route.route, route.view, MainView.class));
    }

    public List<AuthorizedRoute> getAuthorizedRoutes() {
        List<AuthorizedRoute> routes = new ArrayList<>();

        routes.add(new AuthorizedRoute("home", "Home", HomeView.class));
/*        routes.add(new AuthorizedRoute("credentials", "Credentials", CredentialsView.class));
        routes.add(new AuthorizedRoute("payments", "Payments", PaymentsView.class));
        routes.add(new AuthorizedRoute("cards", "Cards", CardsView.class));
        routes.add(new AuthorizedRoute("credits", "Credits", CreditView.class));

        }*/

        return routes;
    }

    public void register(String username, String password, String firstName, String lastName,
                         String email, String phone) {
        User user = new User(username, password, firstName, lastName, email, phone);

        userRepository.save(user);
    }


    public static class AuthorizedRoute {
        private final String route;
        private final String name;
        private final Class<? extends Component> view;

        public AuthorizedRoute(String route, String name, Class<? extends Component> view) {
            this.route = route;
            this.name = name;
            this.view = view;
        }

        public String getRoute() {
            return route;
        }

        public String getName() {
            return name;
        }

        public Class<? extends Component> getView() {
            return view;
        }
    }

    public static class AuthException extends Exception {
    }
}
