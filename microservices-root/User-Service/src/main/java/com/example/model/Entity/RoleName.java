package com.example.model.Entity;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RoleName {
    USER,
    PM,
    ADMIN
}
