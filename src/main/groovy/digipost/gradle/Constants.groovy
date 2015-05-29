package digipost.gradle

public final class Constants{
	private final static String resourcePath = "resources/";
	public final static String csv_delimeter = ";";
	public final static String encoding = "UTF-8";

	public final static String sftp_bruker_id = 'prod_642903';//Demo Partner Test Prod
	public final static String avsender_id =  '642903'; //Demo Partner test
	public final static String behandler_id = ''; //'642903'; //Demo Partner test
	public final static String jobb_navn = "job name"; //Change med before every shipmnent!
	public final static String dokument_emne = "";
	public final static int sftpReceiptTimout = 3600000;
	public final static String autoGodkjennJobb = true;

	public final static String sourcePath = resourcePath+"source_data/";
	public final static String source_file = sourcePath+""; //Change med before every shipmnent!

	public final static String zipFilePath = resourcePath+"toSFTP/";
	public final static String zipFileName = "mottakersplitt.zip";
	
	public final static String jobDir = resourcePath+"jobs/";
	public final static String mottakersplittFile = jobDir+"/mottakersplitt/mottakersplitt.xml";
	public final static String masseutsendelseFile = jobDir+"/masseutsendelse/masseutsendelse.xml";
	
	public final static String keyFilePath = resourcePath+ 'keys/DemoPartnerProd.txt';

	public final static String resultPath = resourcePath+"receipt/";
	public final static String mottakersplittResultFileName = "mottakersplitt-resultat.xml"
	public final static String masseutsendelseResultFileName = "masseutsendelse-resultat.xml"
	
	public final static String reportPath =  resourcePath+'report/';
	public final static String reportName = "mottakersplitt-resultat.csv";

	public final static String url = 'sftp.digipost.no';
	public final static String passphrase = '';
	
}

