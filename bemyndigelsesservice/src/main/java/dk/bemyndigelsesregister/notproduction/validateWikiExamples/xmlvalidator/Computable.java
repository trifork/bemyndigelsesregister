package dk.bemyndigelsesregister.notproduction.validateWikiExamples.xmlvalidator;

public interface Computable<A,V> {
	
	public V compute(A a) throws InterruptedException;

}
