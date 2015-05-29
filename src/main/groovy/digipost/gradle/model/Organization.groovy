package digipost.gradle.model

import groovy.transform.ToString;

@ToString(ignoreNulls = true,includeNames=true)
public class Organization {
	def kunde_id,orgNumber,name,resultat;
}