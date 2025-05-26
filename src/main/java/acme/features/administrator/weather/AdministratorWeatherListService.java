/*
 * AnyLegListService.java
 *
 * Copyright (C) 2012-2024 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.features.administrator.weather;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.entities.weather.WeatherData;
import acme.realms.AirlineManager;

@GuiService
public class AdministratorWeatherListService extends AbstractGuiService<Administrator, WeatherData> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorWeatherRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(AirlineManager.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Flight> objects;
		objects = this.repository.findFlightsByAirlineManagerId(super.getRequest().getPrincipal().getActiveRealm().getId());

		super.getBuffer().addData(objects);
	}

	@Override
	public void unbind(final WeatherData object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbindObject(object, "tag", "origin", "destination");

		super.getResponse().addData(dataset);
	}

}
