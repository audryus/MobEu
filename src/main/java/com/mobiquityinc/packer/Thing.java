package com.mobiquityinc.packer;

public class Thing implements Comparable<Thing> {
	
	int index;
	Double weight;
	Double price;

	public Thing(int index, double weight, double price) {
		super();
		this.index = index;
		this.weight = weight;
		this.price = price;
	}

	@Override
	public String toString() {
		return "Thing [index=" + index + ", weight=" + weight + ", price=" + price + "]";
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	@Override
	public int compareTo(Thing o) {
		int compareTo = o.getPrice().compareTo(this.getPrice());

		if (compareTo == 0) {
			return this.getWeight().compareTo(o.getWeight());

		}

		return compareTo;
	}

}
