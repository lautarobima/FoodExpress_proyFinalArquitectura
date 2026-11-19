
package com.foodexpress.pedidos.exception;

public class ApiErrorDTO {

    private int status;
    private String mensaje;

    public ApiErrorDTO(int status, String mensaje) {
        this.status = status;
        this.mensaje = mensaje;
    }

    public int getStatus() {
        return status;
    }

    public String getMensaje() {
        return mensaje;
    }
}
