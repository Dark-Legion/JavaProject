package me.web_server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.web_server.ServiceRequestException;
import me.web_server.dao.SaleDao;
import me.web_server.model.SaleUnit;

@Service
public final class SaleService extends GenericService {
	@Autowired
	private SaleDao dao;

	public Void addSale(String username, byte[] passwordHash, String client, SaleUnit[] saleUnits)
		throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.addSale(username, passwordHash, client, saleUnits));
	}

	public ArrayList<HashMap<String, Object>> getSalesReport(String username, byte[] passwordHash, Date start, Date end, int page)
		throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.getSalesReport(username, passwordHash, start, end, page));
	}

	public int getSalesReportPageCount(String username, byte[] passwordHash, Date start, Date end)
		throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.getSalesReportPageCount(username, passwordHash, start, end));
	}

	public ArrayList<HashMap<String, Object>> getSalesReportForSeller(String username, byte[] passwordHash, String seller, Date start, Date end, int page)
		throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.getSalesReportForSeller(username, passwordHash, seller, start, end, page));
	}

	public int getSalesReportForSellerPageCount(String username, byte[] passwordHash, String seller, Date start, Date end)
		throws ServiceRequestException {
		return dao.handleSqlQuery(() -> dao.getSalesReportForSellerPageCount(username, passwordHash, seller, start, end));
	}
}
