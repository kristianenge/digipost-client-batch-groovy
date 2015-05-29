package digipost.gradle.model

import groovy.transform.ToString;

@ToString(ignoreNulls = true,includeNames=true)
public class Faktura{
	def id,kid,beloep,kontonummer,forfallsdato;
}