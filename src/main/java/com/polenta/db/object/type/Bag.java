package com.polenta.db.object.type;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.polenta.db.Row;
import com.polenta.db.catalog.Catalog;
import com.polenta.db.catalog.CatalogItem;
import com.polenta.db.exception.PolentaException;
import com.polenta.db.object.behavior.Dropable;
import com.polenta.db.object.behavior.Insertable;
import com.polenta.db.object.behavior.Selectable;
import com.polenta.db.sorting.SortingExecutor;

public class Bag implements Insertable, Selectable, Dropable {
	
	private String name;
	private Map<Integer, Row> rows;
	
	private static Map<String, Bag> BAGS = new LinkedHashMap<String, Bag>();

	public static void create(String bagName, Map<String, String> fields) throws PolentaException {
		Bag bag = new Bag(bagName, fields);
		BAGS.put(bagName, bag);
		System.out.println("New bag " + bagName + " created");
	}
	
	private Bag(String bagName, Map<String, String> fields) throws PolentaException {
		CatalogItem catalogItem = new CatalogItem(bagName, Bag.class, fields);
		Catalog.getInstance().add(catalogItem);
		this.name = bagName; 
		rows = new LinkedHashMap<Integer, Row>();
	}
	
	public static Bag get(String bagName) {
		return BAGS.get(bagName);
	}

	public void drop() {
		this.rows.clear();
		BAGS.remove(this.name);
	}

	public List<Row> select(List<String> selectFields, Map<String, Object> whereConditions, List<String> orderByFields) throws PolentaException {
		//missing: validate if fields used on all clausules are valids to this bag
		
		List<Row> resultSet = new ArrayList<Row>();
		
		List<Row> filteredRows = filterRows(this.rows, whereConditions);
		
		for (Row row: filteredRows) {
			Row resultRow = new Row();
			for (String field: selectFields) {
				resultRow.set(field, row.get(field));
			}
			resultSet.add(resultRow);
		}
		
		if (orderByFields != null && !orderByFields.isEmpty()) {
			resultSet = SortingExecutor.sort(resultSet, orderByFields);
		}
		
		return resultSet;
	}
	
	protected List<Row> filterRows(Map<Integer, Row> allRows, Map<String, Object> whereConditions) {
		List<Row> filteredRows = new ArrayList<Row>();
		filteredRows.addAll(allRows.values());
		return filteredRows;
	}

	public synchronized void insert(Map<String, Object> values) {
		rows.put(rows.size() + 1, new Row(values));
	}

	public void create() throws PolentaException {
	}


}
