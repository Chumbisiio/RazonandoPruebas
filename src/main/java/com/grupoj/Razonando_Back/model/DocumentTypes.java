package com.grupoj.Razonando_Back.model;

public enum DocumentTypes {
    CC("Cédula de ciudadanía"),
    CE("Cédula de extranjería"),
    TI("Tarjeta de identidad"),
    PA("Pasaporte");

    private final String descripcion;

    DocumentTypes(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
