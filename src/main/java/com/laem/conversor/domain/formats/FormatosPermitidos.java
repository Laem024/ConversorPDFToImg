package com.laem.conversor.domain.formats;

public enum FormatosPermitidos {
    PDF ("pdf"),
    JPG ("jpg"),
    PNG ("png");

    private final String formato;

    FormatosPermitidos(String formato) {
        this.formato = formato;
    }

    public static FormatosPermitidos getFormato(String formato) {
        for (FormatosPermitidos f : FormatosPermitidos.values()) {
            if (f.formato.equalsIgnoreCase(formato)) {
                return f;
            }
        }
        throw new IllegalArgumentException("Formato: " + formato + " no permitido");
    }
}
