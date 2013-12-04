package de.uni_potsdam.hpi.metanome.algorithm_integration.results;

import de.uni_potsdam.hpi.metanome.algorithm_integration.ColumnCombination;
import de.uni_potsdam.hpi.metanome.algorithm_integration.ColumnIdentifier;
import de.uni_potsdam.hpi.metanome.algorithm_integration.result_receiver.CouldNotReceiveResultException;
import de.uni_potsdam.hpi.metanome.algorithm_integration.result_receiver.OmniscientResultReceiver;

/**
 * @author Jakob Zwiener
 * 
 * Represents a functional dependency.
 */
public class FunctionalDependency implements Result {

	private static final long serialVersionUID = -8530894352049893955L;

	public static final String FD_SEPARATOR = "-->";
	
	protected ColumnCombination determinant;
	protected ColumnIdentifier dependant;

	/**
	 * Exists for GWT serialization.
	 */
	protected FunctionalDependency() {
		this.determinant = new ColumnCombination();
		this.dependant = new ColumnIdentifier("", "");
	}
	
	public FunctionalDependency(ColumnCombination determinant,
			ColumnIdentifier dependant) {
		this.determinant = determinant;
		this.dependant = dependant;
	}
	
	/**
	 * @return determinant
	 */
	public ColumnCombination getDeterminant() {
		return determinant;
	}

	/**
	 * @return dependant
	 */
	public ColumnIdentifier getDependant() {
		return dependant;
	}

	@Override
	public void sendResultTo(OmniscientResultReceiver resultReceiver)
			throws CouldNotReceiveResultException {
		resultReceiver.receiveResult(this);		
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder
			.append(determinant)
			.append(FD_SEPARATOR)
			.append(dependant);
		
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dependant == null) ? 0 : dependant.hashCode());
		result = prime * result
				+ ((determinant == null) ? 0 : determinant.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FunctionalDependency other = (FunctionalDependency) obj;
		if (dependant == null) {
			if (other.dependant != null)
				return false;
		} else if (!dependant.equals(other.dependant))
			return false;
		if (determinant == null) {
			if (other.determinant != null)
				return false;
		} else if (!determinant.equals(other.determinant))
			return false;
		return true;
	}

}