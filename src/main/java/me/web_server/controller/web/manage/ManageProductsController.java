package me.web_server.controller.web.manage;

import java.util.HashMap;
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

import me.web_server.ServiceRequestException;
import me.web_server.controller.web.AuthAgent;
import me.web_server.controller.web.ErrorPage;
import me.web_server.controller.web.ModelAndViews;
import me.web_server.model.Product;
import me.web_server.service.GenericService;
import me.web_server.service.ProductService;

@Controller
@RequestMapping("/manage/products")
public class ManageProductsController {
	@Autowired
	private AuthAgent authAgent;
	@Autowired
	private ProductService productService;

	@GetMapping
	public Callable<Object> getProducts(
		HttpSession session,
		HttpServletRequest request,
		Model model,
		@RequestParam(name = "page", required = false) Integer page
	) {
		return GenericService.handleAsyncWebRequest(
			() -> authAgent.authenticateAndCallHandler(
				session,
				request,
				model,
				(String username, byte[] passwordHash) -> {
					int pageCount = productService.getProductListPageCount(username, passwordHash);

					if (page != null) {
						if (0 < page && page <= pageCount) {
							model.addAttribute(
								"products",
								Product.loadList(productService.getProductList(username, passwordHash, page))
							);

							if (page > 1) {
								model.addAttribute("prev", page - 1);
							}

							if (page != pageCount) {
								model.addAttribute("next", page + 1);
							}

							return ModelAndViews.MANAGE_PRODUCTS;
						}
					}

					model.addAttribute("pages", pageCount);

					return ModelAndViews.PAGE_SELECT;
				},
				(String username, byte[] passwordHash) -> ErrorPage.error(model, "Insufficient privileges!"),
				true
			),
			model
		);
	}

	@PostMapping
	public Callable<Object> changeDelete(
		HttpSession session,
		HttpServletRequest request,
		Model model,
		@RequestParam("action") String action,
		@RequestParam("name") String product,
		@RequestParam HashMap<String, String> variableParameters
	) {
		return GenericService.handleAsyncWebRequest(
			() -> authAgent.authenticateAndCallHandler(
				session,
				request,
				model,
				(String username, byte[] passwordHash) -> {
					boolean edit;

					if (action.equals("edit")) {
						edit = true;
					} else if (action.equals("delete")) {
						edit = false;
					} else {
						throw new ServiceRequestException("Invalid action!");
					}

					model.addAttribute("edit", edit);

					if (productService.productExists(username, passwordHash, product)) {
						model.addAttribute("name", product);
					} else {
						throw new ServiceRequestException("No such client exists!");
					}

					variableParameters.computeIfPresent(
						"new_name",
						(String key, String oldValue) -> oldValue.isBlank() ? null : oldValue
					);

					variableParameters.computeIfPresent(
						"new_price",
						(String key, String oldValue) -> oldValue.isBlank() ? null : oldValue
					);

					variableParameters.computeIfPresent(
						"reason",
						(String key, String oldValue) -> oldValue.isBlank() ? null : oldValue
					);

					if (edit) {
						if (variableParameters.containsKey("commitEdit")) {
							Double newPrice = null;

							try {
								newPrice = Double.parseDouble(variableParameters.get("new_price"));

								if (newPrice < 0) {
									model.addAttribute("error", "Price can not be less than zero!");

									return ModelAndViews.CHANGE_DELETE_PRODUCT;
								}
							} catch (NullPointerException exception) {
							} catch (NumberFormatException exception) {
								model.addAttribute("error", "Invalid value passed for new price!");

								return ModelAndViews.CHANGE_DELETE_PRODUCT;
							}

							productService.changeProduct(
								username,
								passwordHash,
								product,
								variableParameters.get("new_name"),
								newPrice
							);

							return ModelAndViews.MAIN_REDIRECT;
						}
					} else if (variableParameters.containsKey("confirmDelete")) {
						String reason = variableParameters.get("reason");

						if (reason == null) {
							model.addAttribute("error", "No delete reason provided!");
						} else {
							try {
								productService.deleteProduct(username, passwordHash, product, reason);

								return ModelAndViews.MAIN_REDIRECT;
							} catch (ServiceRequestException exception) {
								model.addAttribute("error", exception.getMessage());
							}
						}
					}

					return ModelAndViews.CHANGE_DELETE_PRODUCT;
				},
				(String username, byte[] passwordHash) -> ErrorPage.error(model, "Insufficient privileges!"),
				true
			),
			model
		);
	}
}
