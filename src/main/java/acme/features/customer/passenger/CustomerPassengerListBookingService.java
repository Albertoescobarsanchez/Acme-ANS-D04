
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.passenger.Passenger;
import acme.features.customer.booking.CustomerBookingsRepository;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerListBookingService extends AbstractGuiService<Customer, Passenger> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerPassengerRepository	repository;

	@Autowired
	private CustomerBookingsRepository	bookingRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean isCustomer;

		isCustomer = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(isCustomer);
	}

	@Override
	public void load() {
		//		Collection<Passenger> passengers;
		//		passengers = this.repository.findPassengersByCustomerId(super.getRequest().getPrincipal().getActiveRealm().getUserAccount().getId());

		int bookingId = super.getRequest().getData("bookingId", int.class);
		Collection<Passenger> passengers = this.bookingRepository.findPassengersByBookingId(bookingId);

		super.getBuffer().addData(passengers);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;

		dataset = super.unbindObject(passenger, "fullName", "email");

		super.getResponse().addData(dataset);
	}
}
