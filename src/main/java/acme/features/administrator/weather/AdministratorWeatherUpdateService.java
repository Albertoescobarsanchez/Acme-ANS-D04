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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.weather.WeatherData;

@GuiService
public class AdministratorWeatherUpdateService extends AbstractGuiService<Administrator, WeatherData> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorWeatherRepository repository;


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		List<WeatherData> objects = new ArrayList<>();

		super.getBuffer().addData(objects);
	}

	@Override
	public void bind(final WeatherData object) {

	}

	@Override
	public void validate(final WeatherData object) {

	}

	@Override
	public void perform(final WeatherData object) {
		assert object != null;
		this.repository.save(object);
	}

	@Override
	public void unbind(final WeatherData object) {

	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
