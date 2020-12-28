package me.web_server.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.web_server.ServiceRequestException;
import me.web_server.dao.ProductDao;

@Service
public final class ProductService extends GenericService {
	@Autowired
	private ProductDao dao;

	public Void addProduct(String username, byte[] passwordHash, String product, double price) throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.addProduct(username, passwordHash, product, price));
	}

	public Void changeProduct(String username, byte[] passwordHash, String product, String newName, Double newPrice) throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.changeProduct(username, passwordHash, product, newName, newPrice));
	}

	public Void deleteProduct(String username, byte[] passwordHash, String product, String reason) throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.deleteProduct(username, passwordHash, product, reason));
	}

	public ArrayList<HashMap<String, Object>> getProductList(String username, byte[] passwordHash, int page) throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.getProductList(username, passwordHash, page));
	}

	public int getProductListPageCount(String username, byte[] passwordHash) throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.getProductListPage(username, passwordHash));
	}

	public boolean productExists(String username, byte[] passwordHash, String product) throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.productExists(username, passwordHash, product));
	}
}
