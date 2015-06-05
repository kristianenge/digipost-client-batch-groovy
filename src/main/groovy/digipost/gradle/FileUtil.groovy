package digipost.gradle

import java.nio.file.*;
import java.util.zip.*;
import com.jcraft.jsch.Channel
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import com.jcraft.jsch.SftpException
import com.jcraft.jsch.UserInfo

import digipost.gradle.model.*

class FileUtil {
	JOBTYPE jobtype;
	
	public FileUtil(JOBTYPE jobtype){
		this.jobtype = jobtype;
	}

	public boolean checkIfAllPersonHavePDFFile(ArrayList recievers){

		def reciever_map = recievers.collectEntries {
			[(it.fil_navn): null]
		}
		println 'reciever_map: '+ reciever_map.size();
		def path_map =[:]
		
		new File(Constants.jobDir+jobtype.toString().toLowerCase()).eachFile() { file ->  
			if(file.isDirectory())
			{
				
			}
			else if(file.getName().endsWith(".pdf")){
				def (filename, filetype) = file.getName().tokenize('.');
				path_map."$filename" = null;
			}
		}
		println 'path_map: '+ path_map.size();

		//sort maps
		def sortedRecieverMap = reciever_map.sort()
		def sortedPathMap = path_map.sort()

		println 'recievers::'
		sortedRecieverMap.each{ k, v -> 
			if(!sortedPathMap.containsKey(k)){
				println 'pathmap does not contain '+k
			}
		}

		return sortedRecieverMap.equals(sortedPathMap)
		
	}

	public void movePDFToJobDir(ArrayList recievers){
		def reciever_map = recievers.collectEntries {
			[(it.fil_navn): null]
		}
		println 'movePDFToJobDir['+jobtype.toString()+']';
		def newdir = new File(Constants.jobDir+jobtype.toString().toLowerCase()+'/');
		println 'path: '+newdir;
		new File(Constants.sourcePath).eachFileMatch(~/.*\.pdf/) { f ->
			def (filename, filetype) = f.getName().tokenize('.');
			if(filetype == 'pdf' && !reciever_map.containsKey(filename))
				{
					println 'personlist does not have current file. skipping '+filename;
				}
			else{
	   			println 'Moving '+f+' from source to jobs files.';
	   			copy(f, new File(Constants.jobDir+jobtype.toString().toLowerCase()+'/'+f.getName()))
   			}
	    }
	}

	def copy = { File src,File dest-> 
	 
		def input = src.newDataInputStream()
		def output = dest.newDataOutputStream()
	 
		output << input 
	 
		input.close()
		output.close()
	}

	public void cleanResources(){ 
		println 'cleanFiles:::'
		
		new File(Constants.jobDir+jobtype.toString().toLowerCase()).eachFile() { f ->
	    	if(f.getName() != '.gitignore')
	      		f.delete()
	    }
	    new File(Constants.resultPath+jobtype.toString().toLowerCase()).eachFile() { f ->
	    	
	    	if(f.getName() != '.gitignore')
	      		f.delete()
	    }
	    new File(Constants.reportPath+jobtype.toString().toLowerCase()).eachFile() { f ->
	    	
	    	if(f.getName() != '.gitignore')
	      		f.delete()
	    }
	     new File(Constants.zipFilePath+jobtype.toString().toLowerCase()).eachFile() { f ->
	     	if(f.getName() != '.gitignore')
	      		f.delete()
	    }
	}

	public void writeXML(String pathToWrite,String xml){
		println 'Wrinting file to disk['+pathToWrite+']'
		def file1 = new File(pathToWrite)
		file1.append(xml,Constants.encoding);
	}

	

	public void writeResultToCSV(ArrayList candidates){
		def file1 = new File(Constants.reportPath+jobtype.toString().toLowerCase()+'/'+jobtype.toString().toLowerCase()+'.csv')
	    if(candidates.get(0) instanceof Person){
			//file1.append('Social security number, kunde_id, ekstra_attr, resultat');
			for(Person p in candidates){
				file1.append(p.toString()+'\n','UTF-8')
			}
		}
		else if (candidates.get(0) instanceof Organization){
			for(Organization o in candidates){
				file1.append(o.toString()+'\n','UTF-8')
			}
		}
	}
	
	public void zipFiles(){
		def zipFile;
		switch (jobtype) {
				case JOBTYPE.MOTTAKERSPLITT:
					 zipFile = new ZipOutputStream(new FileOutputStream(Constants.zipFilePath+jobtype.toString().toLowerCase()+"/mottakersplitt.zip"))  
					break;
				case JOBTYPE.MASSEUTSENDELSE:
					 zipFile = new ZipOutputStream(new FileOutputStream(Constants.zipFilePath+jobtype.toString().toLowerCase()+"/masseutsendelse.zip"))  
					break;
				default:
					throw new Exception('Not implemented');
					break;
			}
		println 'Zipping files:'
		
		new File(Constants.jobDir+jobtype.toString().toLowerCase()).eachFile() { file ->  
			if(file.isDirectory())
			{
				
			}
			else if(file.getName().endsWith(".xml") || file.getName().endsWith(".pdf")){
				println " "+file.getName() + " is added to zip."
				zipFile.putNextEntry(new ZipEntry(file.getName()))  
				zipFile << new FileInputStream(file)
				zipFile.closeEntry()  
			}
		}

		zipFile.close()  
	}

	public void unzipFiles(){
		new File(Constants.resultPath+jobtype.toString().toLowerCase()).eachFile() { file ->  
			if(file.isDirectory())
			{
				
			}
			else if(file.getName().endsWith(".zip") && file.getName().contains("resultat")){
				println "unzipper"+file.getName();
				unzipFile(file.getAbsolutePath());
			}
		}
	}
   
   private void unzipFile(zipFileName){
   	println 'unzipFile: '+zipFileName;
   	final int BUFFER = 2048;
      try {
         BufferedOutputStream dest = null;
         FileInputStream fis = new 
	   FileInputStream(zipFileName);
         ZipInputStream zis = new 
	   ZipInputStream(new BufferedInputStream(fis));
         ZipEntry entry;
         while((entry = zis.getNextEntry()) != null) {
            System.out.println("Extracting: " +entry);
            int count;
            byte[] data = new byte[BUFFER];
            // write the files to the disk
            FileOutputStream fos = new 
	      FileOutputStream(Constants.resultPath+jobtype.toString().toLowerCase()+"/"+entry.getName());
            dest = new 
              BufferedOutputStream(fos, BUFFER);
            while ((count = zis.read(data, 0, BUFFER)) 
              != -1) {
               dest.write(data, 0, count);
            }
            dest.flush();
            dest.close();
         }
         zis.close();
      } catch(Exception e) {
         e.printStackTrace();
      }
   }

	
	public void sftpToDigipost(){
		println 'SFTP to Digipost.'+jobtype
		java.util.Properties config = new java.util.Properties()
		config.put "StrictHostKeyChecking", "no"

		JSch ssh = new JSch()
		ssh.addIdentity(Constants.keyFilePath);
		Session sess = ssh.getSession Constants.sftp_bruker_id, Constants.url, 22
		sess.with {
			setConfig config
			setPassword Constants.passphrase
			connect()
			Channel chan = openChannel "sftp"
			ChannelSftp sftp = (ChannelSftp) chan;
			sftp.connect()
			
			def sessionsFile ;
			switch (jobtype) {
				case JOBTYPE.MOTTAKERSPLITT:
					sessionsFile = new File(Constants.zipFilePath+jobtype.toString().toLowerCase()+'/'+jobtype.toString().toLowerCase()+'.zip')
					sessionsFile.withInputStream { istream -> sftp.put(istream, "mottakersplitt/mottakersplitt.zip") }
					break;
				case JOBTYPE.MASSEUTSENDELSE:
					sessionsFile = new File(Constants.zipFilePath+jobtype.toString().toLowerCase()+'/'+jobtype.toString().toLowerCase()+'.zip')
					sessionsFile.withInputStream { istream -> sftp.put(istream, "masseutsendelse/masseutsendelse.zip") }
					break;
			}

			sftp.disconnect()
			disconnect()
		}
	}


	public void checkForReceipt(){
		println 'checkForReceipt to Digipost.'+jobtype
		println 'tostring:'+JOBTYPE.MOTTAKERSPLITT;
		java.util.Properties config = new java.util.Properties()
		config.put "StrictHostKeyChecking", "no"
		String kvitteringsPath = "";
		switch (jobtype) {
				case JOBTYPE.MOTTAKERSPLITT:
					kvitteringsPath= "/mottakersplitt/kvittering/";
					break;
				case JOBTYPE.MASSEUTSENDELSE:
					kvitteringsPath= "/masseutsendelse/kvittering/";
					break;
				default:
					throw new Exception('JOBTYPE not supported');
					break;
		}
		JSch ssh = new JSch()
		ssh.addIdentity(Constants.keyFilePath);
		Session sess = ssh.getSession Constants.sftp_bruker_id, Constants.url, 22
		def beginTime = System.currentTimeMillis()

		sess.with {
			setConfig config
			setPassword ""
			connect()
			Channel chan = openChannel "sftp"
			ChannelSftp sftp = (ChannelSftp) chan;
			sftp.connect()
			boolean hasReceipt = false
			def timeout = false
			println "waiting for receipt.....";
			while(!hasReceipt && !timeout){
				println 'ls '+kvitteringsPath+' :'+ (System.currentTimeMillis() - beginTime) 
				timeout = ((System.currentTimeMillis() - beginTime)  >= Constants.sftpReceiptTimout) // abort after X sec.
				Vector<ChannelSftp.LsEntry> list = sftp.ls("."+kvitteringsPath+"*.zip");
				sleep 1000 //sleep for 1000 ms
				if(list.size() >= 2){
					boolean recievd = false;
					boolean reciept = false;
					for(ChannelSftp.LsEntry entry : list) {
						if(entry.getFilename().contains('resultat'))
							{
								reciept = true;
							}
						if(entry.getFilename().contains('mottatt'))
						{
							recievd = true;
						}
					}
					if(recievd && reciept){
						for(ChannelSftp.LsEntry entry : list) {
						    sftp.get(kvitteringsPath+entry.getFilename(), Constants.resultPath +jobtype.toString().toLowerCase()+'/'+ entry.getFilename());
						    sftp.rm(kvitteringsPath+entry.getFilename());
						    hasReceipt =true;
						}
					}
				}
				else	println 'Size of ls: '+list.size()

			}
			if(timeout){
				throw new Exception('Did not get receipt within the configured receipt-timeout['+Constants.sftpReceiptTimout+'](ms)')
			}
			println "Finished waiting for receipt.....";
			sftp.disconnect()
			disconnect()
		}
	}
}