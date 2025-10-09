
package acme.features.any.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import acme.entities.service.Service;

@ControllerAdvice
public class AnyServiceAdvisor {

	@Autowired
	private AnyServiceRepository repository;


	@ModelAttribute("service")
	public Service getService() {
		Service result;
		try {
			result = this.repository.findRandomService();
		} catch (final Throwable oops) {
			result = null;
		}
		return result;
	}

}
