package simple.simple_auth.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.Mapping;
import simple.simple_auth.config.SecurityConfig;
import simple.simple_auth.domain.dtos.CreateUserDto;
import simple.simple_auth.domain.entities.UserEntity;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Mapping(target = "password", source = "password", qualifiedByName = "encodePassword")
    public abstract UserEntity toEntity(CreateUserDto dto);

    @Named("encodePassword")
    protected String encodePassword(String password) {
        return SecurityConfig.passwordEncoder().encode(password);
    }
}