package com.macc.timesheet.data.response

import com.google.gson.annotations.SerializedName
import com.macc.timesheet.presentation.model.EmployeeModel

data class ApiResponseEmployee(
    val prev: String?,
    val next: Int,
    val count: Int,
    val data:List<EmployeeInfo>,
    val message: String
)

data class EmployeeInfo (
    @SerializedName("id_empleado") val idEmpleado: Int,
    @SerializedName("id_area") val idArea: Int?,
    @SerializedName("id_departamento") val idDepartamento: Int?,
    @SerializedName("id_subarea") val idSubarea: Int?,
    @SerializedName("id_puesto") val idPuesto: Int?,
    @SerializedName("id_cedis") val idCedis: Int?,
    @SerializedName("clave_contable") val claveContable: String?,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("apellido_paterno") val apellidoPaterno: String?,
    @SerializedName("apellido_materno") val apellidoMaterno: String?,
    @SerializedName("calle") val calle: String?,
    @SerializedName("numero") val numero: String?,
    @SerializedName("colonia") val colonia: String?,
    @SerializedName("ciudad") val ciudad: String?,
    @SerializedName("estado") val estado: String?,
    @SerializedName("codigo_postal") val codigoPostal: String?,
    @SerializedName("correo") val correo: String?,
    @SerializedName("telefono") val telefono: String?,
    @SerializedName("rfc") val rfc: String?,
    @SerializedName("curp") val curp: String?,
    @SerializedName("imss") val imss: String?,
    @SerializedName("id_tipo_empleado") val idTipoEmpleado: String?,
    @SerializedName("usuario") val usuario: String?,
    @SerializedName("contrasenia") val contrasenia: String?,
    @SerializedName("limite_credito") val limiteCredito: Double?,
    @SerializedName("fecha_registro") val fechaRegistro: String?,
    @SerializedName("usuario_registro") val usuarioRegistro: Int?,
    @SerializedName("usuario_modifico") val usuarioModifico: Int?,
    @SerializedName("activo") val activo: Boolean,
    @SerializedName("entrada_matutina") val entradaMatutina: String?,
    @SerializedName("entrada_vespertina") val entradaVespertina: String?,
    @SerializedName("id_empresa") val idEmpresa: Int?,
    @SerializedName("puesto") val puesto: String?,
    @SerializedName("cedis") val cedis: String?,
    @SerializedName("area") val area: String?,
    @SerializedName("departamento") val departamento: String?,
    @SerializedName("nombre_completo") val nombreCompleto: String
){
    fun toPresentation():EmployeeModel{
        return EmployeeModel(
            idEmpleado = idEmpleado,
            idArea = idArea,
            idDepartamento = idDepartamento,
            idSubarea = idSubarea,
            idPuesto = idPuesto,
            idCedis = idCedis,
            claveContable = claveContable,
            nombre = nombre,
            apellidoPaterno = apellidoPaterno,
            apellidoMaterno = apellidoMaterno,
            calle = calle,
            numero = numero,
            colonia = colonia,
            ciudad = ciudad,
            estado = estado,
            codigoPostal = codigoPostal,
            correo = correo,
            telefono = telefono,
            rfc = rfc,
            curp = curp,
            imss = imss,
            idTipoEmpleado = idTipoEmpleado,
            usuario = usuario,
            contrasenia = contrasenia,
            limiteCredito = limiteCredito,
            fechaRegistro = fechaRegistro,
            usuarioRegistro = usuarioRegistro,
            usuarioModifico = usuarioModifico,
            activo = activo,
            entradaMatutina = entradaMatutina,
            entradaVespertina = entradaVespertina,
            idEmpresa = idEmpresa,
            puesto = puesto,
            cedis = cedis,
            area = area,
            departamento = departamento,
            nombreCompleto = nombreCompleto
        )
    }
}
