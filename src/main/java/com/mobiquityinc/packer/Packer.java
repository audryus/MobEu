package com.mobiquityinc.packer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.mobiquityinc.exception.APIException;

public class Packer {

	private Packer() {
	}

	public static String pack(String filePath) throws APIException {
		if (StringUtils.isEmpty(filePath)) {
			throw new APIException("filePath not informed.");
		}
		File file = new File(filePath);
		if (!file.exists()) {
			throw new APIException("file does not exists.");
		}

		List<String> indexesToPackage = new ArrayList<String>();
		try (Scanner scanner = new Scanner(file)) {
			String nextLine;
			while(scanner.hasNext()) {
				nextLine = scanner.nextLine();
				
				indexesToPackage.add(getResult(nextLine));
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		return String.join("\n", indexesToPackage);
	}

	/**
	 * Main method that knows what to do with each line from the file.
	 * @param nextLine A line from the file
	 * @return The thing index to put inside the package
	 * @throws APIException
	 */
	private static String getResult(String nextLine) throws APIException {
		// Two blocks: 0 = Weight, 1 = List of things
		String[] split = nextLine.split(":");
		int maximumWeight = extractTotalW(split);

		//Max weight that a package can take is ≤ 100
		if (maximumWeight > 100)
			throw new APIException("Too much weight");
		
		List<String> things = new ArrayList<String>(); // avoid nullpointer
		fillThings(things, split[1]); //Use references
		
		//There might be up to 15 items you need to choose from
		if (things.size() > 15)
			throw new APIException("Too many things");
		
		List<Thing> items = new ArrayList<Thing>(); //nullpointer avoider
		fillItems(items, things, maximumWeight);
		
		//Ordering items
		items = items.stream().sorted().collect(Collectors.toList());
		
		List<Thing> strangeThings = new ArrayList<Thing>();

		//Do the real logic
		fillStrangeThings(strangeThings, items, maximumWeight);
		
		//No result return a '-'
		if (strangeThings.isEmpty()) {
			return "-";
		}
		
		//With results, well, I could run the list, adding to a StringBuilder
		// If the last "item" do not add a ','
		List<String> indexes = new ArrayList<String>();
		strangeThings.stream().forEach(c -> {
			indexes.add(""+c.index);
		});
		//But this  String.join looks so cool.
		// Javascript send his regards
		return String.join(",", indexes);
	}

	/**
	 * Decide if a Item should go inside the package.

	 * @param strangeThings List of Items that will go inside the package.
	 * @param items List of all possible items
	 * @param maximumWeight package Max weight
	 */
	private static void fillStrangeThings(List<Thing> strangeThings, List<Thing> items, int maximumWeight) {
		double totalW = 0.0;
		Thing actual, next;
		
		//Run the List actual plus one, always in pair
		for (int i = 0; i < items.size() - 1; i++) {
			//current value
			actual = items.get(i);
			
			//first time add
			if (totalW == 0) {
				totalW = actual.weight;
				strangeThings.add(actual);
			}
			
			//Get next in line
			next = items.get(i + 1);
			
			//See if the sum is less than the max weight
			if (totalW + next.weight > maximumWeight || next.price == actual.price) {
				continue;
			}
			
			//Add to the result
			totalW += next.weight;
			strangeThings.add(next);
		}
	}

	/**
	 * Convert the list of Things into real Items to be put inside the package.
	 * 
	 * @param items List of items to be considered
	 * @param things All things
	 * @param maximumWeight Max weight from the package
	 * @throws APIException
	 */
	private static void fillItems(List<Thing> items, List<String> things, int maximumWeight) throws APIException {
		if (items == null || things == null)
			return;
		try {
			for (String thing : things) {
				//Not pretty, maybe some regex magic ?
				String[] split = thing.replaceAll("\\(|€|\\)", "").split(",");
				Thing item = new Thing(Integer.parseInt(split[0]), Double.parseDouble(split[1]),
						Double.parseDouble(split[2]));
				//Max weight and cost of an item is ≤ 100
				if (item.price > 100 || item.weight > 100)
					throw new APIException("Too much to handle");
				//Ignores those that are already bigger than the max weight 
				if (item.weight <= maximumWeight)
					items.add(item);
			}
		} catch (NumberFormatException e) {
			throw new APIException("Some numbers might have problem");
		}
	}

	/**
	 * Fill the list of things, that possible can go inside the box.
	 * @param things List of things to be filled
	 * @param allThings Line with all possible things
	 */
	private static void fillThings(List<String> things, String allThings) {
		//Avoiding problems with nullpointer and empty string.
		if (StringUtils.isEmpty(allThings) || things == null) {
			return;
		}
		
		String[] split = allThings.split("\\s+");
		
		for (String thing : split) {
			//Avoid empty lines
			if (StringUtils.isEmpty(thing))
				continue;
			things.add(thing);
		}
		
	}
	/**
	 * Extract from the line the total weight the package can handle.
	 * @param split The first part of the line, contains the total amount
	 * @return The weight converted to INT
	 * @throws APIException
	 */
	private static int extractTotalW(String[] split) throws APIException {
		int total;
		
		try {
			total = Integer.parseInt(split[0].trim());
		} catch(NumberFormatException e) {
			throw new APIException("Wrong total box weight.");
		}
		return total;
	}
}
