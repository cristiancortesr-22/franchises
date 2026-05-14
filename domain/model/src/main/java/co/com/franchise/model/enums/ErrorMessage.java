package co.com.franchise.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorMessage {

    FRANCHISE_DOES_NOT_EXIST("FR-01", "La franquicia no existe."),
    FRANCHISE_ALREADY_EXISTS("FR-02", "La franquicia ya existe."),
    FRANCHISE_CREATION_FAILED("FR-03", "Error al crear la franquicia."),
    FRANCHISE_GET_FAILED("FR-04", "Error al consultar la franquicia."),

    BRANCH_ALREADY_EXISTS("BR-01", "La sucursal ya existe para esta franquicia."),
    BRANCH_CREATION_FAILED("BR-02", "Error al crear la sucursal."),
    BRANCH_DOES_NOT_EXIST("BR-03", "La sucursal no existe."),
    BRANCH_GET_FAILED("BR-04", "Error al consultar la sucursal."),

    PRODUCT_ALREADY_EXISTS("PR-02", "El producto ya existe para esta sucursal."),
    PRODUCT_CREATION_FAILED("PR-03", "Error al crear el producto."),
    PRODUCT_UPDATE_STOCK_AND_STATUS_FAILED("PR-08", "Error al actualizar el stock y el estado del producto."),

    INVALID_INPUT("VAL-01", "Los datos de entrada no son válidos."),
    INTERNAL_ERROR("VAL-02", "Error interno, vuelva a intentarlo.");

    private final String code;
    private final String message;
}
