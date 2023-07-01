package com.Datys.CapaIntermedia.Manipulator;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final JwtGrantedAuthoritiesConverter jwtConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt source) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtConverter.convert(source).stream(),
                extractRole(source).stream()
        ).collect(Collectors.toSet());

        return new JwtAuthenticationToken(
                source,
                authorities,
                source.getClaim("preferred_username")
        );
    }

    private Collection<? extends GrantedAuthority> extractRole(Jwt source) {
        Map<String, Object> resourceAccess;
        Map<String, Object> resource;
        Collection<String> resourceRole;

        if (source.getClaim("resource_access") == null)
            return Set.of();

        resourceAccess = source.getClaim("resource_access");

        if (resourceAccess.get("SpringClient") == null)
            return Set.of();

        resource = (Map<String, Object>) resourceAccess.get("SpringClient");

        resourceRole = (Collection<String>) resource.get("roles");

        return resourceRole
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_"+role))
                .collect(Collectors.toSet());

    }
}
