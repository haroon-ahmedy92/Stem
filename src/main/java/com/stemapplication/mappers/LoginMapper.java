package com.stemapplication.mappers;

import com.stemapplication.DTO.AuthResponseDto;
import com.stemapplication.DTO.LoginDto;
import com.stemapplication.Models.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoginMapper {
    UserEntity toEntity(LoginDto loginDto);
    AuthResponseDto toDto(UserEntity userEntity);
}
