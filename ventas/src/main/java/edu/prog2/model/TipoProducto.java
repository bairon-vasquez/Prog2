package edu.prog2.model;

import org.json.JSONArray;
import org.json.JSONObject;

public enum TipoProducto {

    ASEO_PERSONAL("Aseo personal"),
    ASEO_GENERAL("Aseo general"),
    BEBIDAS("Bebida o gaseosa"),
    CARNICOS("Cárnico"),
    CEREALES("Cereal"),
    CONDIMENTOS("Condimento"),
    ENDULZANTES("Endulzante"),
    FRUTAS_Y_VERDURAS("Fruta o verdura"),
    GOLOSINAS("Golosina"),
    HUEVOS("Huevo"),
    HARINAS("Harina"),
    LACTEOS("Lácteo"),
    LEGUMBRES("Legumbre"),
    OLEAGINOSAS("Oleaginosa"),
    PESCADOS_Y_MARISCOS("Pescado o marisco"),
    TUBERCULOS("Tubérculo"),
    OTROS("Otros");

    private String value;

    private TipoProducto(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Dado un string, devuelve la constante enumerada correspondiente. Ejemplo:
     * TipoProducto.getEnum("Sin definir el tipo");
     * no confundir con TipoProducto.valueOf("CONSTANTE_ENUMERADA")
     * 
     * @param value La expresión para humanos correspondiente a la constante
     *              enumerada
     * @return La constante enumerada
     */
    public static TipoProducto getEnum(String value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }

        for (TipoProducto tp : values()) {
            if (value.equalsIgnoreCase(tp.getValue())) {
                return tp;
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        return this.getValue();
    }

    // punto 48 taller 6
    public static JSONArray getAll() {
        JSONArray jsonArr = new JSONArray();
        for (TipoProducto tp : values()) {
            jsonArr.put(new JSONObject().put("ordinal", tp.ordinal()).put("key", tp).put("value", tp.value));
        }
        return jsonArr;
    }

}
