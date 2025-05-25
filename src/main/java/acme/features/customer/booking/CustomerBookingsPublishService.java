
package acme.features.customer.booking;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingRecord;
import acme.entities.booking.TravelClass;
import acme.entities.flight.Flight;
import acme.features.airlineManager.flight.AirlineManagerFlightRepository;
import acme.features.customer.bookingRecord.CustomerBookingRecordRepository;
import acme.realms.Customer;

@GuiService
public class CustomerBookingsPublishService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingsRepository		repository;

	@Autowired
	private CustomerBookingRecordRepository	bookingRecordRepository;

	@Autowired
	private AirlineManagerFlightRepository	flightRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int customerId = super.getRequest().getPrincipal().getActiveRealm().getUserAccount().getId();
		int bookingId = super.getRequest().getData("id", int.class);
		Booking booking = this.repository.findBookingById(bookingId);

		boolean status = booking.getCustomer().getUserAccount().getId() == customerId && super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Booking booking;
		int bookingId = super.getRequest().getData("id", int.class);

		booking = this.repository.findBookingById(bookingId);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		int flightId;
		Flight flight;

		flightId = super.getRequest().getData("flight", int.class);
		flight = this.flightRepository.findFlightById(flightId);

		super.bindObject(booking, "locatorCode", "lastNibble", "travelClass");
		booking.setFlight(flight);
	}

	@Override
	public void validate(final Booking booking) {
		if (booking.getLastNibble() == null || booking.getLastNibble().isBlank() || booking.getLastNibble().isEmpty()) {
			String lastNibbleStored = this.repository.findBookingById(booking.getId()).getLastNibble();
			if (lastNibbleStored == null || lastNibbleStored.isBlank() || lastNibbleStored.isBlank())
				super.state(false, lastNibbleStored, "acme.validation.confirmation.message.lastNibble");
		}

		Collection<BookingRecord> br = this.bookingRecordRepository.findBookingRecordByBookingId(booking.getId());
		if (br.isEmpty())
			super.state(false, "passenger", "acme.validation.confirmation.message.passenger");
	}

	@Override
	public void perform(final Booking booking) {
		//		if (booking.getLastNibble() == null || booking.getLastNibble().isBlank() || booking.getLastNibble().isEmpty())
		//			booking.setLastNibble(this.repository.findBookingById(booking.getId()).getLastNibble());
		//		Booking b = this.repository.findBookingById(booking.getId());
		//		b.setFlight(booking.getFlight());
		//		b.setLastNibble(booking.getLastNibble());
		//		b.setLocatorCode(booking.getLocatorCode());
		//		b.setTravelClass(booking.getTravelClass());
		booking.setDraftMode(false);
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;
		SelectChoices choices;
		SelectChoices flightChoices;

		Date today = MomentHelper.getCurrentMoment();
		Collection<Flight> flights = this.repository.findAllPublishedFlightsWithFutureDeparture(today);
		flightChoices = SelectChoices.from(flights, "tag", booking.getFlight());
		choices = SelectChoices.from(TravelClass.class, booking.getTravelClass());
		Collection<String> passengers = this.repository.findPassengersNameByBooking(booking.getId());

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "price", "lastNibble", "draftMode");
		dataset.put("travelClass", choices);
		dataset.put("passengers", passengers);
		dataset.put("flight", flightChoices.getSelected().getKey());
		dataset.put("flights", flightChoices);

		super.getResponse().addData(dataset);
	}

}
