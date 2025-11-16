package org.vaskozlov.is.course.api;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.vaskozlov.is.course.bean.User;
import org.vaskozlov.is.course.repository.UserRepository;
import org.vaskozlov.is.course.service.RegistrationValidator;

@PermitAll
@ApplicationScoped
@Path("/auth/register")
public class UserCreation {
    private static final JsonbConfig JSONB_CONFIG = new JsonbConfig();
    private static final Jsonb JSONB = JsonbBuilder.create(JSONB_CONFIG);

    @Inject
    private RegistrationValidator registrationValidator;

    @Inject
    private UserRepository userRepository;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(JsonObject body) {
        try {
            System.out.println(body.toString());
            return doUserCreation(body);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    private Response doUserCreation(JsonObject data) {
        var validationResult = registrationValidator.validate(data);

        if (validationResult.isError()) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(validationResult.getError())
                    .build();
        }

        User.UserBuilder userBuilder = User.builder();

        userBuilder.username(data.getString("username"));
        userBuilder.password(data.getString("password"));
        userBuilder.email(data.getString("email"));

        userRepository.save(userBuilder.build());

        return Response.status(Response.Status.CREATED).build();
    }
}
