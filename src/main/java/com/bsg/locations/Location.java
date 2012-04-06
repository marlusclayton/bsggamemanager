package com.bsg.locations;

import com.bsg.Expansion;
import com.bsg.Item;

public class Location implements Item {

	private String name;
	private String area;
	private boolean isDamaged;
	private Expansion expansion;
	
	public Location(String name, String area, Expansion expansion) {
		this.name = name;
		this.area = area;
		this.expansion = expansion;
		
		this.isDamaged = false;
	}
	
	public String getName() {
		return name;
	}
	
	public String getArea() {
		return area;
	}
	
	public boolean isDamaged() {
		return isDamaged;
	}
	
	public void setDamaged(boolean isDamaged) {
		this.isDamaged = isDamaged;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public Expansion getExpansion() {
		return expansion;
	}
	
	
}
