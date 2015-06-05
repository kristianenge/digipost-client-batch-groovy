package digipost.gradle
import static org.junit.Assert.*;
import org.junit.Test
import static groovy.json.JsonOutput.*


class mottakerSplittTest{
	def rawDataService = new RawDataService();
	def xmlUtil =  new XmlUtil();
	def fileUtil = new FileUtil(JOBTYPE.MOTTAKERSPLITT);
	def rmu =  new ResultManipulationUtil();
	Boolean skipHeader = true;

	//@Test
	void cleanResources(){
		fileUtil.cleanResources();
	}

	@Test
	void shouldDoMottakersplittPerson(){
		fileUtil.cleanResources();
		//1. populate ss from csv
		def personsList = rawDataService.getPeopleFromCSV(skipHeader);
		if(personsList.size() == 0){
			println('personList size: '+personsList.size())
			fail("NO testsubject.. check source file.");
		}

		String request_xml = xmlUtil.makeMottakerSplittWithSSN(personsList);
		
		//3. write mottakersplitt.xml to file
		fileUtil.writeXML(Constants.mottakersplittFile , request_xml);
		//4. Zip files
		fileUtil.zipFiles();
		//5. sftp up to server.
		fileUtil.sftpToDigipost();
		//6. check for receipt
		fileUtil.checkForReceipt();
		//7. unzip result
		fileUtil.unzipFiles();
		//8. append result to new CSV.
		//2- populate who exist in Digipost
		def resultat = rawDataService.populateResultMapFromResult(JOBTYPE.MOTTAKERSPLITT);
		println('Count source['+personsList.size()+'], count result['+resultat.size()+']')
 
		println prettyPrint(toJson(resultat))


		//3- update the People obj with digipost-customer
		rmu.updateDigipostCustomers(personsList,resultat);
		//4- make new csv with digipost column
		fileUtil.writeResultToCSV(personsList);

	}

	//@Test
	void shouldDoMottakersplittBedrift(){
		fileUtil.cleanResources();
		//1. populate ss from csv
		def orgList = rawDataService.getOrganizationsFromCSV(skipHeader);
		
		String request_xml = xmlUtil.makeMottakerSplittWithOrgNr(orgList);
		//3. write mottakersplitt.xml to file
		fileUtil.writeXML(Constants.mottakersplittFile , request_xml);
		//4. Zip files
		fileUtil.zipFiles();
		//5. sftp up to server.
		fileUtil.sftpToDigipost();
		//6. check for receipt
		fileUtil.checkForReceipt();
		//7. unzip result
		fileUtil.unzipFiles();
		//8. append result to new CSV.
		//2- populate who exist in Digipost
		def resultat = rawDataService.populateResultMapFromResult(JOBTYPE.MOTTAKERSPLITT);
		println('Count source['+orgList.size()+'], count result['+resultat.size()+']')


		//3- update the People obj with digipost-customer
		rmu.updateDigipostCustomers(orgList,resultat);
		//4- make new csv with digipost column
		fileUtil.writeSubjectToCSV(orgList);

	}

}