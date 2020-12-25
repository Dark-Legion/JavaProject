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
import me.web_server.service.GenericService;
import me.web_server.service.ProductService;

@RequestMapping("/api/manage/products")
@RestController
public class ProductsController {
	@Autowired
	private ProductService productService;

	@GetMapping("/{page}")
	public Callable<HashMap<String, Object>> getProductList(
		@PathVariable("page") Integer page,
		@RequestParam("user") String username,
		@RequestParam("pass") String password
	) {
		return GenericService.handleAsyncRestRequest(
			() -> productService.getProductList(username, Hasher.hash(password), page)
		);
	}

	@GetMapping
	public Callable<HashMap<String, Object>> getProductListPageCount(
		@RequestParam("user") String username,
		@RequestParam("pass") String password
	) {
		return GenericService.handleAsyncRestRequest(
			() -> productService.getProductListPageCount(username, Hasher.hash(password))
		);
	}

	@PutMapping
	public Callable<HashMap<String, Object>> addProduct(
		@RequestParam("user") String username,
		@RequestParam("pass") String password,
		@RequestParam("new_product") String product,
		@RequestParam("new_price") Double price
	) {
		return GenericService.handleAsyncRestRequest(
			() -> productService.addProduct(username, Hasher.hash(password), product, price)
		);
	}

	@PostMapping
	public Callable<HashMap<String, Object>> changeProduct(
		@RequestParam("user") String username,
		@RequestParam("pass") String password,
		@RequestParam("change_product") String product,
		@RequestParam(name = "new_name", required = false) String newName,
		@RequestParam(name = "new_price", required = false) Double newPrice
	) {
		return GenericService.handleAsyncRestRequest(
			() -> productService.changeProduct(username, Hasher.hash(password), product, newName, newPrice)
		);
	}

	@DeleteMapping
	public Callable<HashMap<String, Object>> deleteProduct(
		@RequestParam("user") String username,
		@RequestParam("pass") String password,
		@RequestParam("delete_product") String product,
		@RequestParam("reason") String reason
	) {
		return GenericService.handleAsyncRestRequest(
			() -> productService.deleteProduct(username, Hasher.hash(password), product, reason)
		);
	}
}
