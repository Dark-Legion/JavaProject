package me.web_server.controller.web.manage;

import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	public Callable<Object> getUsers(
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
							model.addAttribute("products", Product.loadList(productService.getProductList(username, passwordHash, page)));

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
}
