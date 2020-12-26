package me.web_server.controller.rest.manage;

import java.util.HashMap;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.web_server.Hasher;
import me.web_server.controller.rest.GenericRestController;
import me.web_server.service.ClientService;
import me.web_server.service.GenericService;

@RequestMapping("/api/manage/clients")
@RestController
public class ManageClientsRestController extends GenericRestController {
	@Autowired
	private ClientService clientService;

	@GetMapping
	public Callable<HashMap<String, Object>> getClientListPageCount(
		@RequestParam("user") String username,
		@RequestParam("pass") String password
	) {
		return GenericService.handleAsyncRestRequest(
			() -> clientService.getClientListPageCount(username, Hasher.hash(password))
		);
	}

	@GetMapping("/{page}")
	public Callable<HashMap<String, Object>> getClientList(
		@PathVariable("page") Integer page,
		@RequestParam("user") String username,
		@RequestParam("pass") String password
	) {
		return GenericService.handleAsyncRestRequest(
			() -> clientService.getClientList(username, Hasher.hash(password), page)
		);
	}

	@PutMapping
	public Callable<HashMap<String, Object>> addClient(
		@RequestParam("user") String username,
		@RequestParam("pass") String password,
		@RequestParam("name") String client,
		@RequestParam("is_company") Boolean isCompany
	) {
		return GenericService.handleAsyncRestRequest(
			() -> clientService.addClient(
				username,
				Hasher.hash(password),
				client,
				isCompany
			)
		);
	}

	@PostMapping
	public Callable<HashMap<String, Object>> changeClientName(
		@RequestParam("user") String username,
		@RequestParam("pass") String password,
		@RequestParam("change") String client,
		@RequestParam(name = "name", required = false) String newName
	) {
		return GenericService.handleAsyncRestRequest(
			() -> clientService.changeClient(
				username,
				Hasher.hash(password),
				client,
				newName
			)
		);
	}

	@DeleteMapping
	public Callable<HashMap<String, Object>> deleteClient(
		@RequestParam("user") String username,
		@RequestParam("pass") String password,
		@RequestParam("name") String client,
		@RequestParam("reason") String reason
	) {
		return GenericService.handleAsyncRestRequest(
			() -> clientService.deleteClient(
				username,
				Hasher.hash(password),
				client,
				reason
			)
		);
	}
}
