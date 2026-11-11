package com.tecsup.dto; import jakarta.validation.constraints.*; public record ClienteRequest(@NotBlank String nombre, @NotBlank String apellidos, @NotBlank String direccion, @NotBlank String estado, @Pattern(regexp="[0-9]{8}") String nroDni) {}

