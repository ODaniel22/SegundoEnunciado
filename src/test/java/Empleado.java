import lombok.Getter;
import lombok.Setter;

public class Empleado {
    @Getter  @Setter
    private String firstname;
    @Getter  @Setter
    private String lastname;
    @Getter  @Setter
    private float totalprice;
    @Getter  @Setter
    private boolean depositpaid;
    @Getter  @Setter
    private BookingDates bookingdates;
    // Constructor completo
    public Empleado(String firstname, String lastname, float totalprice, boolean depositpaid, BookingDates bookingdates) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.totalprice = totalprice;
        this.depositpaid = depositpaid;
        this.bookingdates = bookingdates;
    }


    // Clase anidada para bookingdates
    public static class BookingDates {
        @Getter  @Setter
        private String checkin;
        @Getter  @Setter
        private String checkout;
        // Constructor completo
        public BookingDates(String checkin, String checkout) {
            this.checkin = checkin;
            this.checkout = checkout;
        }

    }
}