package me.web_server.controller.web.add;

import java.util.LinkedHashMap;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import me.web_server.controller.web.AuthAgent;
import me.web_server.controller.web.ErrorPage;
import me.web_server.controller.web.ModelAndViews;
import me.web_server.model.Tuple2;
import me.web_server.service.ProductService;
import me.web_server.service.GenericService;

@Controller
@RequestMapping("/add/product")
public class AddProductController {
	private final static LinkedHashMap<String, Tuple2<String, String>> FIELDS;
	private final static LinkedHashMap<String, String> NO_VALUES = new LinkedHashMap<>();

	static {
		FIELDS = new LinkedHashMap<>();

		FIELDS.put("name", new Tuple2<>("Product", "text"));
		FIELDS.put("price", new Tuple2<>("Price", "text"));
	}

	@Autowired
	private AuthAgent authAgent;
	@Autowired
	private ProductService productService;

	@GetMapping
	public Callable<Object> blankAddProductForm(
		HttpSession session,
		HttpServletRequest request,
		Model model
	) {
		return GenericService.handleAsyncWebRequest(
			() -> authAgent.authenticateAndCallHandler(
				session,
				request,
				model,
				(String username, byte[] passwordHash) -> {
					model.addAttribute("pageTitle", "Add product");

					model.addAttribute("fields", FIELDS);
					model.addAttribute("values", NO_VALUES);

					return ModelAndViews.ADD;
				},
				(String username, byte[] passwordHash) -> ErrorPage.error(model, "Insufficient privileges!"),
				true
			),
			model
		);
	}

	@PostMapping
	public Callable<Object> addProduct(
		HttpSession session,
		HttpServletRequest request,
		Model model,
		@RequestParam("name") String product,
		@RequestParam("price") String priceString
	) {
		return GenericService.handleAsyncWebRequest(
			() -> authAgent.authenticateAndCallHandler(
				session,
				request,
				model,
				(String username, byte[] passwordHash) -> {
					model.addAttribute("pageTitle", "Add product");

					if (product.isEmpty() || priceString.isEmpty()) {
						model.addAttribute("error", "All fields must be filled in!");
					} else {
						if (productService.productExists(username, passwordHash, product)) {
							model.addAttribute("error", "Product with this name already exists!");
						} else {
							try {
								double price = Double.parseDouble(priceString);

								if (price < 0) {
									productService.addProduct(username, passwordHash, product, price);

									return ModelAndViews.MAIN_REDIRECT;
								} else {
									model.addAttribute("error", "Price can not be less than zero!");
								}
							} catch (NumberFormatException exception) {
								model.addAttribute("error", "Invalid value passed for price!");
							}
						}
					}

					model.addAttribute("fields", FIELDS);

					{
						LinkedHashMap<String, String> values = new LinkedHashMap<>();

						if (!product.isEmpty()) {
							values.put("name", product);
						}

						if (!priceString.isEmpty()) {
							values.put("price", priceString);
						}

						model.addAttribute("values", values);
					}

					return ModelAndViews.ADD;
				},
				(String username, byte[] passwordHash) -> ErrorPage.error(model, "Insufficient privileges!"),
				true
			),
			model
		);
	}
}
