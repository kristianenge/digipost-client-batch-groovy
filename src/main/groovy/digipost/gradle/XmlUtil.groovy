package digipost.gradle

import groovy.xml.MarkupBuilder
import digipost.gradle.model.*

class XmlUtil{

	public String makeMottakerSplittWithSSN(ArrayList personsList){
		println 'Creating mottakersplitt.xml from ssn - list'
		def writer = new StringWriter()
		def xml = new MarkupBuilder(writer)	
		
		xml.getMkp().xmlDeclaration(version:'1.0',encoding:'UTF-8',standalone:'yes');
		xml.mottakersplitt('xmlns':"http://www.digipost.no/xsd/avsender2_1",'xmlns:xsi':"http://www.w3.org/2001/XMLSchema-instance")
		{
		  "jobb-innstillinger"() {
		  	"avsender-id"(Constants.avsender_id);
		  	"behandler-id"(Constants.behandler_id);
		  	"jobb-id"(UUID.randomUUID().toString());
		  	"jobb-navn"(Constants.jobb_navn);
		  	"auto-godkjenn-jobb"(Constants.autoGodkjennJobb);
		  	"klientinformasjon"('Manual');
		  }
		  mottakere(){
			  for(Person p : personsList){
		     		mottaker(){
			     		"kunde-id"(p.kunde_id);
			     		if(p.ssn != null && p.ssn.length() == 11)
			     			"foedselsnummer"(p.ssn);
			     		else{
			     			"navn"(){
				     			"navn-format1"(){
				     				"fullt-navn-fornavn-foerst"(p.fulltNavn);
				     			}
			     			}
				     		"adresse"(){
				     			"adresse-format1"(){
				     				"adresselinje1"(p.adresselinje1);
				     				"postnummer"(p.postnummer);
				     				"poststed"(p.poststed);
				     			}
				     		}
			     		}
		     		}
	          }
		  }
		}
		
		return writer.toString();
	}

	public String makeMottakerSplittWithAddrAndName(ArrayList personsList){
		println 'Creating mottakersplitt.xml from name and address - list'
		def writer = new StringWriter()
		def xml = new MarkupBuilder(writer)	
		
		xml.getMkp().xmlDeclaration(version:'1.0',encoding:'UTF-8',standalone:'yes');
		xml.mottakersplitt('xmlns':"http://www.digipost.no/xsd/avsender2_1",'xmlns:xsi':"http://www.w3.org/2001/XMLSchema-instance")
		{
		  "jobb-innstillinger"() {
		  	"avsender-id"(Constants.avsender_id);
		  	"behandler-id"(Constants.behandler_id);
		  	"jobb-id"(UUID.randomUUID().toString());
		  	"jobb-navn"(Constants.jobb_navn);
		  	"auto-godkjenn-jobb"(Constants.autoGodkjennJobb);
		  	"klientinformasjon"('Manual');
		  }
		  mottakere(){
			  for(Person p : personsList){
		     		mottaker(){
			     		"kunde-id"(p.kunde_id);
			     		"navn"(){
			     			"navn-format1"(){
			     				"fullt-navn-fornavn-foerst"(p.fulltNavn);
			     			}
			     		}
			     		"adresse"(){
			     			"adresse-format1"(){
			     				"adresselinje1"(p.adresselinje1);
			     				"postnummer"(p.postnummer);
			     				"poststed"(p.poststed);
			     			}
			     		}
		     		}
	          }
		  }
		}
		
		return writer.toString();
	}

	public String makeMottakerSplittWithOrgNr(ArrayList orgList){
		println 'Creating mottakersplitt.xml from name and address - list'
		def writer = new StringWriter()
		def xml = new MarkupBuilder(writer)	
		
		xml.getMkp().xmlDeclaration(version:'1.0',encoding:'UTF-8',standalone:'yes');
		xml.mottakersplitt('xmlns':"http://www.digipost.no/xsd/avsender2_1",'xmlns:xsi':"http://www.w3.org/2001/XMLSchema-instance")
		{
		  "jobb-innstillinger"() {
		  	"avsender-id"(Constants.avsender_id);
		  	"behandler-id"(Constants.behandler_id);
		  	"jobb-id"(UUID.randomUUID().toString());
		  	"jobb-navn"(Constants.jobb_navn);
		  	"auto-godkjenn-jobb"(Constants.autoGodkjennJobb);
		  	"klientinformasjon"('Manual');
		  }
		  mottakere(){
			  for(Organization o : orgList){
		     		mottaker(){
			     		"kunde-id"(o.kunde_id);
			     		"organisasjonsnummer"(o.orgNumber)
		     		}
	          }
		  }
		}
		
		return writer.toString();
	}

	public String makeMasseutsendelseWithPrint(ArrayList personsList){
		def writer = new StringWriter()
		def xml = new MarkupBuilder(writer)	
		
		
		xml.getMkp().xmlDeclaration(version:'1.0',encoding:'UTF-8',standalone:'yes');
		xml.'masseutsendelse'('xmlns':'http://www.digipost.no/xsd/avsender2_1','xmlns:xsi':'http://www.w3.org/2001/XMLSchema-instance')
		{
		  "jobb-innstillinger"() {
		  	"avsender-id"(Constants.avsender_id);
		  	"behandler-id"(Constants.behandler_id);
		  	"jobb-id"(UUID.randomUUID().toString());
		  	"jobb-navn"(Constants.jobb_navn);
		  	"auto-godkjenn-jobb"(Constants.autoGodkjennJobb);
		  	"klientinformasjon"('Manual');
		  }
		  "standard-distribusjon"() {
		  	  "felles-innstillinger"(){
				  "globale-dokument-innstillinger"(){
				  		"emne"(Constants.dokument_emne);
				  }
			  }
			  "post"(){
			  	for(Person p : personsList){
			  		"dokument"(){
			  			"id"("id_"+p.kunde_id);
			  			"fil"(p.kunde_id+".pdf");
			  			"innstillinger"(){
				  			"emne"(Constants.dokument_emne);
				  		}
			  		}
			  	}
			  }
			  "forsendelser"(){
			  	for(Person p : personsList){
			  		"brev"('xsi:type':'brev-med-print'){
			  			"mottaker"(){
			  				"kunde-id"(p.kunde_id);
			  				if(p.ssn != null && p.ssn.length() == 11)
			     				"foedselsnummer"(p.ssn);
			     			else{
				     			"navn"(){
					     			"navn-format1"(){
					     				"fullt-navn-fornavn-foerst"(p.fulltNavn);
					     			}
				     			}
				     			"adresse"(){
					     			"adresse-format1"(){
					     				"adresselinje1"(p.adresselinje1);
					     				"postnummer"(p.postnummer);
					     				"poststed"(p.poststed);
					     			}
				     			}
			     			}
			  				
			  			}
			  		"hoveddokument"("uuid":UUID.randomUUID().toString(),"refid":"id_"+p.kunde_id);
			  		"fysisk-print"(){
			  			"postmottaker"(p.fulltNavn);
			  			"norsk-mottakeradresse"{
			  				"adresselinje1"(p.adresselinje1);
			  				"postnummer"(p.postnummer);
			  				"poststed"(p.poststed);
			  			}
			  			"retur-postmottaker"("TELENOR PENSJONSKASSE");
			  			"norsk-returadresse"{
			  				"adresselinje1"("Postboks 800");
			  				"postnummer"("1331");
			  				"poststed"("Fornebu");
			  			}
			  		}
			  		}
			  	}
			  }
			}
		}

		return writer.toString();
	}

	public String makeMasseutsendelseWithInvoice(ArrayList personsList){
		def writer = new StringWriter()
		def xml = new MarkupBuilder(writer)	
		
		
		xml.getMkp().xmlDeclaration(version:'1.0',encoding:'UTF-8',standalone:'yes');
		xml.'masseutsendelse'('xmlns':'http://www.digipost.no/xsd/avsender2_1','xmlns:xsi':'http://www.w3.org/2001/XMLSchema-instance')
		{
		  "jobb-innstillinger"() {
		  	"avsender-id"(Constants.avsender_id);
		  	"behandler-id"(Constants.behandler_id);
		  	"jobb-id"(UUID.randomUUID().toString());
		  	"jobb-navn"(Constants.jobb_navn);
		  	"auto-godkjenn-jobb"(Constants.autoGodkjennJobb);
		  	"klientinformasjon"('Manual');
		  }
		  "standard-distribusjon"() {
		  	  "felles-innstillinger"(){
				  "globale-dokument-innstillinger"(){
				  		"emne"(Constants.dokument_emne);
				  		"sms-varsling"();
				  }
			  }
			  "post"(){
			  	for(Person p : personsList){
			  		"dokument"('xsi:type':'faktura'){
			  			"id"("id_"+p.fil_navn);
			  			"fil"(p.fil_navn+".pdf");
			  			"kid"(" ");
			  			"beloep"(p.faktura.beloep);
			  			"kontonummer"(p.faktura.kontonummer);
			  			"forfallsdato"(p.faktura.forfallsdato);
			  		}
			  	}

			  }
			  "forsendelser"(){
			  	for(Person p : personsList){
			  		"brev"(){
			  			"mottaker"(){
			  				"kunde-id"(p.kunde_id);
			  				"foedselsnummer"(p.ssn);
			  			}
			  		"hoveddokument"("uuid":UUID.randomUUID().toString(),"refid":p.fil_id);
			  		}
			  	}
			  }
			}
		}

		
		return writer.toString();
	}

	public String makeMasseutsendelseWithSSN(ArrayList personsList){
		def writer = new StringWriter()
		def xml = new MarkupBuilder(writer)	
		
		
		xml.getMkp().xmlDeclaration(version:'1.0',encoding:'UTF-8',standalone:'yes');
		xml.'masseutsendelse'('xmlns':'http://www.digipost.no/xsd/avsender2_1','xmlns:xsi':'http://www.w3.org/2001/XMLSchema-instance')
		{
		  "jobb-innstillinger"() {
		  	"avsender-id"(Constants.avsender_id);
		  	"behandler-id"(Constants.behandler_id);
		  	"jobb-id"(UUID.randomUUID().toString());
		  	"jobb-navn"(Constants.jobb_navn);
		  	"auto-godkjenn-jobb"(Constants.autoGodkjennJobb);
		  	"klientinformasjon"('Manual');
		  }
		  "standard-distribusjon"() {
		  	  "felles-innstillinger"(){
				  "globale-dokument-innstillinger"(){
				  		"emne"(Constants.dokument_emne);
				  }
			  }
			  "post"(){
			  	for(Person p : personsList){
			  		"dokument"(){
			  			"id"("id_"+p.kunde_id);
			  			"fil"(p.kunde_id+".pdf");
			  			"innstillinger"(){
				  			"emne"(Constants.dokument_emne);
				  		}
			  		}
			  	}
			  }
			  "forsendelser"(){
			  	for(Person p : personsList){
			  		"brev"(){
			  			"mottaker"(){
			  				"kunde-id"(p.kunde_id);
			  				"foedselsnummer"(p.ssn);
			  			}
			  		"hoveddokument"("uuid":UUID.randomUUID().toString(),"refid":"id_"+p.kunde_id);
			  		}
			  	}
			  }
			}
		}

		
		return writer.toString();
	}

	public String makeMasseutsendelse(ArrayList personsList){
		def writer = new StringWriter()
		def xml = new MarkupBuilder(writer)	
		
		
		xml.getMkp().xmlDeclaration(version:'1.0',encoding:'UTF-8',standalone:'yes');
		xml.'masseutsendelse'('xmlns':'http://www.digipost.no/xsd/avsender2_1','xmlns:xsi':'http://www.w3.org/2001/XMLSchema-instance')
		{
		  "jobb-innstillinger"() {
		  	"avsender-id"(Constants.avsender_id);
		  	"behandler-id"(Constants.behandler_id);
		  	"jobb-id"(UUID.randomUUID().toString());
		  	"jobb-navn"(Constants.jobb_navn);
		  	"auto-godkjenn-jobb"(Constants.autoGodkjennJobb);
		  	"klientinformasjon"('Manual');
		  }
		  "standard-distribusjon"() {
		  	  "felles-innstillinger"(){
				  "globale-dokument-innstillinger"(){
				  		"emne"(Constants.dokument_emne);
				  }
			  }
			  "post"(){
			  	for(Person p : personsList){
			  		if(p.faktura != null){
			  			"dokument"('xsi:type':'faktura'){
				  			"id"("id_"+p.fil_navn);
				  			"fil"(p.fil_navn+".pdf");
				  			"kid"(p.faktura.kid);
				  			"beloep"(p.faktura.beloep);
				  			"kontonummer"(p.faktura.kontonummer);
				  			"forfallsdato"(p.faktura.forfallsdato);
			  			}
			  		}
			  		else{
				  		"dokument"(){
				  			"id"("id_"+p.kunde_id);
				  			"fil"(p.fil_navn+".pdf");
				  			"innstillinger"(){
					  			"emne"(Constants.dokument_emne);
					  			"autentiseringsnivaa"("TO_FAKTOR");
					  			"sensitivitetsnivaa"("SENSITIVT");
					  		}
				  		}
			  		}
			  	}
			  }
			  "forsendelser"(){
			  	for(Person p : personsList){
			  		"brev"(){
			  			mottaker(){
				     		"kunde-id"(p.kunde_id);
				     		if(p.ssn != null)
				     			"foedselsnummer"(p.ssn);
				     		if(p.fulltNavn != null){
					     		"navn"(){
					     			"navn-format1"(){
					     				"fullt-navn-fornavn-foerst"(p.fulltNavn);
					     			}
					     		}
				     		}
				     		if(p.postnummer != null && p.poststed != null && p.adresselinje1 != null){
					     		"adresse"(){
					     			"adresse-format1"(){
					     				"adresselinje1"(p.adresselinje1);
					     				"postnummer"(p.postnummer);
					     				"poststed"(p.poststed);
					     			}
					     		}
				     		}
		     		}

			  		"hoveddokument"("uuid":UUID.randomUUID().toString(),"refid":"id_"+p.kunde_id);
			  		}

			  	}
			  }
			}
		}
		return writer.toString();
	}

	
}
