package digipost.gradle

import org.junit.Test

class masseutsendelseTest{
	def rawDataService = new RawDataService();
	def xmlUtil =  new XmlUtil();
	def fileUtil = new FileUtil(JOBTYPE.MASSEUTSENDELSE);
	def rmu =  new ResultManipulationUtil();
	Boolean skipHeader = true;

	@Test
	void cleanResources(){
		fileUtil.cleanResources();
	}
	
	//@Test
	void shouldDoMasseutsendelse(){
		fileUtil.cleanResources();
		//1. populate ss from csv
		ArrayList personsList = rawDataService.populate_NameAndAdr_from_source_csv(skipHeader);
		//2. create masseutsendelse.xml
		String request_xml = xmlUtil.makeMasseutsendelseWithPrint(personsList);
		//3. write masseutsendelse.xml to file
		fileUtil.writeXML(Constants.masseutsendelseFile,request_xml);
		fileUtil.movePDFToJobDir(personsList);

		//check that there is a file for every reciever
		boolean everyPersonHaveAFile = fileUtil.checkIfAllPersonHavePDFFile(personsList);
		assert everyPersonHaveAFile 

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
		def resultat = rawDataService.populateResultMapFromResult(JOBTYPE.MASSEUTSENDELSE);
		println('Count source['+personsList.size()+'], count result['+resultat.size()+']')

		//3- update the People obj with digipost-customer
		rmu.updateDigipostCustomers(personsList,resultat);
		//4- make new csv with digipost column
		fileUtil.writeSubjectToCSV(personsList);
	}
	
}