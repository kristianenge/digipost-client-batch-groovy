package digipost.gradle
import digipost.gradle.model.*

class RawDataService{

	public ArrayList populate_NameAndAdr_from_source_csv(Boolean skipHeader){ 
		def doesntWork = new File(".").getCanonicalPath()
		println doesntWork
		ArrayList personList = new ArrayList();
		boolean skip = skipHeader;
		def counter = 1;
		new File(Constants.source_file).splitEachLine(Constants.csv_delimeter) {fields ->
			if(skip){
		  		skip = false;
		  	}
		  	else if(fields[4] != null){
//Brevtype; MedlemsNr ; Navn ;Født dato; Pnr ; Adresse ; Postadresse ; Land ;
				def person = new Person(
					ssn:formatSSN(fields[3],fields[4]),
					kunde_id:fields[1],
					fil_navn:fields[1],
					ekstra_attr:fields[1],
					adresselinje1:fields[5],
					postnummer:returnPostnummer(fields[6]),
					poststed:returnPostSted(fields[6]),
					fulltNavn:fields[2]
				);
				personList.add(person)
			}
		}
		return personList;
	}


	private String returnPostnummer(String postadresse){
		
		def (postnummer,postadr) = postadresse.tokenize(' ')
		return postnummer;
	}

	private String returnPostSted(String postadresse){
		def (postnummer,postadr) = postadresse.tokenize(' ')
		return postadr;
	}

	private String formatSSN(String date,String checksum){
		if(date == null)
			return "";
		def (gday,gmonth,gyear) = date.tokenize('.')
		
		def day = addZero(gday)
		def month = addZero(gmonth)
		def year = gyear.substring(2,4)
		
		String ssn = day+month+year+checksum;
		return ssn;
	}



	public ArrayList populate_OrgNr_from_source_csv(Boolean skipHeader){ 
		ArrayList orgList = new ArrayList();
		boolean skip = skipHeader;
		def counter = 1;
		new File(Constants.source_file).splitEachLine(Constants.csv_delimeter) {fields ->
			if(skip){
		  		skip = false;
		  	}
		  	else if(fields[0] != null){
//Kundenr;Fornavn;Etternavn;Adresse;Adresse2;Postnr;Poststed;Født;Kode
				def organization = new Organization(
					kunde_id:fields[0],
					orgNumber:fields[1],
					name:fields[2]
				);
				orgList.add(organization)
			}
		}
		return orgList;
	}

	public ArrayList populate_ss_from_source_csv(Boolean skipHeader){ 
		ArrayList personList = new ArrayList();
		boolean skip = skipHeader;
		def counter = 1;
		new File(Constants.source_file).splitEachLine(Constants.csv_delimeter) {fields ->
			if(skip){
		  		skip = false;
		  	}
		  	else if(fields[0] != null){

				def person = new Person(
					ssn:fields[0].trim(),
					kunde_id:''+counter++
				);
				personList.add(person)
			}
		}
		return personList;
	}

	
	

	public ArrayList populate_ss_and_Invoice_from_source_csv(String pathToSourceCSV,Boolean skipHeader){ 
		ArrayList personList = new ArrayList();
		boolean skip = skipHeader;
		def counter = 1;
		new File(pathToSourceCSV).splitEachLine(Constants.csv_delimeter) {fields ->	
		  if(skip){
		  	skip = false;

		  }else{
			  def id = counter++;
			  def faktura = new Faktura(id:id,kid:fields[4],beloep:fields[5],kontonummer:fields[6],forfallsdato:fields[7]);
			  def person = new Person(
			    kunde_id:fields[2],
			    faktura:faktura
			    );
			  
			  personList.add(person)
		  }
		}
		return personList;
	}

	

	private String formatNavn(String navn){
		String fulltNavn ='';

		if(navn == null){
			println "navn er null!";
			return "";	
		}
		String[] navnArr = navn.split(" ");

		for(int i = 1; i < navnArr.length; i++) {
			fulltNavn += navnArr[i]+ ' ';
		}
		fulltNavn += navnArr[0];
		
		return fulltNavn;
	}

	private addZero(String date){
		return (date.length() < 2 ? "0" + date:date)
	}


	public Map populateResultMapFromResult(JOBTYPE jobtype){
		def resultat =[:];
		def doc = new XmlSlurper().parse(Constants.resultPath+jobtype.toString().toLowerCase()+'/'+jobtype.toString().toLowerCase()+'-resultat.xml')
		doc."mottaker-resultater".each { res ->
			
		  res.children().each { tag ->
		  	String kundeID = "";
		  	tag.children().each { inner ->
		  		if(inner.name() == "kunde-id" ){
		  			kundeID = inner.text();
		  		}
		  		if(inner.name() == "status" ){
		  			resultat.put(kundeID, inner.text());
		  			kundeID="";
		  		}
		  	 }
		  }
		}
		return resultat;
	}


	public Map populateResultMapFromMasseutsendelseold(JOBTYPE jobtype){
 
		def resultat =[:];
		def doc = new XmlSlurper().parse(Constants.resultPath+jobtype.toString().toLowerCase()+'/'+Constants.masseutsendelseResultFileName) 
		doc."standard-distribusjon"."forsendelser".each { res ->
		  res.children().each { tag ->
		  	tag.children().each { inner ->
		  			if(inner.name()== "mottaker"){
		  				inner.children().each { inner2 ->
		  					if(inner2.name() == "foedselsnummer"){
					  			resultat.put(inner2.text())	
					  		}	
			  			}
			  		}
		  	 }
		  }
		}
		return resultat;
	}

	


}