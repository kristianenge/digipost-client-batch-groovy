package digipost.gradle.model

import groovy.transform.ToString;

@ToString(includePackage = false,ignoreNulls = true,includeNames=true)
public class Person {
	def ssn,navn,adresselinje1,postnummer,poststed,mobile,fil_navn,vedlegg_navn,kunde_id,fil_id,ekstra_attr,fulltNavn,resultat;
	Faktura faktura;
}


