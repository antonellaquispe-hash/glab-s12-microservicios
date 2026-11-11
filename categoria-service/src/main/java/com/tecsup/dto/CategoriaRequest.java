package com.tecsup.dto; import jakarta.validation.constraints.*; public record CategoriaRequest(@NotBlank String nombre, @NotBlank String estado) {}
