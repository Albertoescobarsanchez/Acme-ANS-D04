/*
 * AirlineManagerLegCreateService.java
 *
 * Copyright (C) 2012-2025 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.features.administrator.weather;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.entities.weather.WeatherData;
import acme.realms.AirlineManager;

@GuiService
public class AdministratorWeatherCreateService extends AbstractGuiService<Administrator, WeatherData> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorWeatherRepository repository;


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(AirlineManager.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Flight flight;
		int managerId;
		AirlineManager manager;

		managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		manager = this.repository.findAirlineManagerById(managerId);

		flight = new Flight();
		flight.setAirlineManager(manager);
		flight.setDraftMode(true);
		super.getBuffer().addData(flight);
	}

	@Override
	public void bind(final WeatherData object) {
		assert object != null;

		super.bindObject(object, "tag", "selfTransfer", "cost", "description");
	}

	@Override
	public void validate(final WeatherData object) {
		assert object != null;

	}

	@Override
	public void perform(final WeatherData object) {
		assert object != null;
		object.setId(0);
		this.repository.save(object);
	}

	@Override
	public void unbind(final WeatherData object) {

		Dataset dataset;

		dataset = super.unbindObject(object, "tag", "selfTransfer", "cost", "description");

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
