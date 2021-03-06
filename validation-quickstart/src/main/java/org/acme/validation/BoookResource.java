package org.acme.validation;

import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/books")
public class BoookResource {

	@Inject Validator validator;
	
	@Inject BookService bookService;
	
	@Path("/manual-validation")
	@POST
	public Result tryMeManualValidation(Book book) {
		Set<ConstraintViolation<Book>> violations = validator.validate(book);
		if (violations.isEmpty()) {
	        return new Result("Book is valid! It was validated by manual validation.");
	    } else {
	        return new Result(violations);
	    }
	}
	
	@Path("/end-point-method-validation")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Result tryMeEndPointMethodValidation(@Valid Book book) {
		return new Result("Book is valid! It was validated by end point method validation.");
	}
	
	@Path("/service-method-validation")
	@POST
	public Result tryMeServiceMethodValidation(Book book) {
		try {
			bookService.validateBook(book);
			return new Result("Book is valid! It was validated by service method validation.");
		} catch (ConstraintViolationException e) {
			return new Result(e.getConstraintViolations());
		}
	}
	
	public static class Result {
		
		private String message;
		private boolean success;

		public Result(String message) {
			this.success = true;
			this.message = message;
		}
		
		public Result(Set<? extends ConstraintViolation<?>> violations) {
			this.success = true;
			this.message = violations.stream()
					.map(cv -> cv.getMessage())
					.collect(Collectors.joining(", "));
		}

		public String getMessage() {
			return message;
		}

		public boolean isSuccess() {
			return success;
		}
	}
}
