package com.stemapplication.mappers;

import com.stemapplication.DTO.RegisterDto;
import com.stemapplication.Models.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RegisterMapper {
    UserEntity toEntity(RegisterDto registerDto);
}
