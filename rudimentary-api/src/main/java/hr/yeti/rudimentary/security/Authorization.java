package hr.yeti.rudimentary.security;

import hr.yeti.rudimentary.http.Request;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Authorization extends Predicate<Request> {

    static Predicate<Request> rolesAllowed(String... roles) {
        if (Objects.isNull(roles) || roles.length == 0) {
            return ALLOW_ALL;
        }

        return (request) -> {
            return Objects.nonNull(request.getIdentity()) && Stream.of(roles)
                .anyMatch(role -> request.getIdentity().getRoles().contains(role));
        };
    }

    static Predicate<Request> groupsAllowed(String... groups) {
        if (Objects.isNull(groups) || groups.length == 0) {
            return ALLOW_ALL;
        }

        return (request) -> {
            return Objects.nonNull(request.getIdentity()) && Stream.of(groups)
                .anyMatch(group -> request.getIdentity().getGroups().contains(group));
        };
    }

    static Predicate<Request> ALLOW_ALL = (request) -> {
        return true;
    };

    static Predicate<Request> DENY_ALL = (request) -> {
        return false;
    };

}
