package com.tecsup.dto; import jakarta.validation.constraints.*; public record PedidoRequest(@NotNull Long clienteId,@NotNull Long productoId,@NotNull @Positive Integer cantidad){}
