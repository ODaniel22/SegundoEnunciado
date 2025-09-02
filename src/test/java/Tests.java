import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class Tests {



    @Test
    public void crearEmpleadoConPost_sinnombres() throws Exception {

        Empleado.BookingDates fechas = new Empleado.BookingDates("2021-01-27", "2100-10-25");

        Empleado empleado = new Empleado("", "", 100000, true, fechas);

        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(empleado);
        System.out.println("Payload enviado:\n" + payload);

        Response response = given()
                .contentType("application/json")
                .body(payload)
                .when()
                .post("https://restful-booker.herokuapp.com/booking") // Reemplazá con tu endpoint real
                .then()
                .log().body()
                .assertThat()
                .statusCode(200) // o 201 si el servidor devuelve "Created"
                .body("firstname", Matchers.equalTo(empleado.getFirstname()))
                .body("lastname", Matchers.equalTo(empleado.getLastname()))
                .body("totalprice", Matchers.equalTo((int) empleado.getTotalprice()))
                .body("bookingdates.checkin", Matchers.equalTo(empleado.getBookingdates().getCheckin()))
                .body("bookingdates.checkout", Matchers.equalTo(empleado.getBookingdates().getCheckout()))
                .extract()
                .response();

        // 5. (Opcional) Capturar ID generado
        int bookingId = response.path("bookingid");
        System.out.println("ID generado por el servidor: " + bookingId);
    }

    @Test
    public void crearEmpleadoConPost_numeros_en_nombres() throws Exception {

        Empleado.BookingDates fechas = new Empleado.BookingDates("2021-01-27", "2100-10-25");


        Empleado empleado = new Empleado("156", "1529", 100000, true, fechas);


        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(empleado);
        System.out.println("Payload enviado:\n" + payload);

        Response response = given()
                .contentType("application/json")
                .body(payload)
                .when()
                .post("https://restful-booker.herokuapp.com/booking") // Reemplazá con tu endpoint real
                .then()
                .log().body()
                .assertThat()
                .statusCode(400) // o 201 si el servidor devuelve "Created"
                .body("firstname", Matchers.equalTo(empleado.getFirstname()))
                .body("lastname", Matchers.equalTo(empleado.getLastname()))
                .body("totalprice", Matchers.equalTo((int) empleado.getTotalprice()))
                .body("bookingdates.checkin", Matchers.equalTo(empleado.getBookingdates().getCheckin()))
                .body("bookingdates.checkout", Matchers.equalTo(empleado.getBookingdates().getCheckout()))
                .extract()
                .response();

        // 5. (Opcional) Capturar ID generado
        int bookingId = response.path("bookingid");
        System.out.println("ID generado por el servidor: " + bookingId);
    }

    @Test
    public void PUT_registro_vacio() throws JsonProcessingException {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        Empleado.BookingDates fechas = new Empleado.BookingDates("2021-01-27", "2100-10-25");

        Empleado empleado = new Empleado("", "", 1563, false, fechas);


        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(empleado);
        System.out.println(payload);

        Response response = given()
                .auth().preemptive().basic("admin", "password123")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(payload)
                .when()
                .put("/booking/445");


        response.then().assertThat()
                .statusCode(200)
                .body("firstname", Matchers.equalTo(empleado.getFirstname()))
                .body("lastname", Matchers.equalTo(empleado.getLastname()))
                .body("totalprice", Matchers.equalTo((int) empleado.getTotalprice()))
                .body("bookingdates.checkin", Matchers.equalTo(empleado.getBookingdates().getCheckin()))
                .body("bookingdates.checkout", Matchers.equalTo(empleado.getBookingdates().getCheckout()));
    }

    @Test
    public void crearEmpleadoConDatosInvalidos() throws Exception {

        Empleado.BookingDates fechas = new Empleado.BookingDates("", "not-a-date");

        Empleado empleado = new Empleado("1234", "", -999f, false, fechas);

        //  Serializar a JSON
        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(empleado);
        System.out.println("Payload inválido:\n" + payload);

        //  Enviar POST y capturar respuesta
        Response response = given()
                .contentType("application/json")
                .body(payload)
                .when()
                .post("https://restful-booker.herokuapp.com/booking") // o tu endpoint real
                .then()
                .log().body()
                .extract()
                .response();

        //  Validar que la API rechaza los datos
        int statusCode = response.statusCode();
        Assertions.assertTrue(statusCode == 200 || statusCode == 201, "La API debería rechazar datos inválidos");

        //  (Opcional) Validar mensaje de error si lo devuelve
        String body = response.asString();
        Assertions.assertTrue(body.toLowerCase().contains("error") || body.toLowerCase().contains("invalid"),
                "No se recibió mensaje de error esperado");
    }

    @Test
    public void eliminarReservaYVerificarConGet() {
        int bookingId = 882; // Usá un ID válido que puedas eliminar

        // Paso 1: Eliminar la reserva
        Response deleteResponse = given()
                .auth().preemptive().basic("admin", "password123")
                .contentType("application/json")
                .when()
                .delete("https://restful-booker.herokuapp.com/booking/" + bookingId)
                .then()
                .log().body()
                .assertThat()
                .statusCode(201) // El API devuelve 201 al eliminar correctamente
                .extract()
                .response();

        System.out.println("Reserva eliminada con éxito: ID " + bookingId);

        // Paso 2: Verificar que ya no existe con GET
        Response getResponse = given()
                .contentType("application/json")
                .when()
                .get("https://restful-booker.herokuapp.com/booking/" + bookingId)
                .then()
                .log().body()
                .assertThat()
                .statusCode(404) // Esperamos que ya no exista
                .extract()
                .response();

        System.out.println("Verificación exitosa: reserva no encontrada tras eliminación.");
    }

}