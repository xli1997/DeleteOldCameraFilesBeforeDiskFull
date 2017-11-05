import java.io.File;
import java.util.Date;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.attribute.*;
import java.nio.file.Path;
import java.nio.file.Files;

public class Backup {

	// delete dir/file if path is given, path can not be null
	public static void RemoveAFileOrDirectory(File dir) {
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			if (files != null && files.length > 0) {
				for (File aFile : files) {
					RemoveAFileOrDirectory(aFile);
				}
			}
			dir.delete();
		} else {
			dir.delete();
		}
	}
	
	private static void CopyAFile(File source, File dest)
	        throws IOException {
	    Files.copy(source.toPath(), dest.toPath());
	}

	// copy scr single dir/file to dst, dst dir must none exist
	public static void CopyAFileOrDirectory(File sourceLocation, File targetLocation)
			throws IOException{

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				System.out.println("create dir:" + targetLocation.toString());
				targetLocation.mkdir();
				
				String[] children = sourceLocation.list();
				for (int i = 0; i < children.length; i++) {
					CopyAFileOrDirectory(new File(sourceLocation, children[i]), new File(
							targetLocation, children[i]));
				}
			}
		} else {
/*			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();*/
			CopyAFile(sourceLocation, targetLocation);
		}
	}

	public static void BackupDirectory(File sourceDir , File targetDir)
			throws IOException{
		File[] listOfSrcFiles = sourceDir.listFiles();
		File[] listOfDstFiles = targetDir.listFiles();
		
		//*******************************************************************
		// Use Src as base run first round
		for (int i = 0; i < listOfSrcFiles.length; i++) {
			if (listOfSrcFiles[i].isDirectory()) {
				int dirFound = 0;
				for (int j = 0; j < listOfDstFiles.length; j++) {
					if(listOfDstFiles[j].isDirectory()){
						// only compare name difference if both are directory
						if(listOfSrcFiles[i].getName().toString().equals(listOfDstFiles[j].getName().toString())){
							dirFound = 1;
							//System.out.println("found 1");
							BackupDirectory(listOfSrcFiles[i], listOfDstFiles[j]);
						}
					}
				}
				if (dirFound == 0){
					StringBuilder str = new StringBuilder();
					str.append(targetDir.getPath());
					str.append("\\");
					str.append(listOfSrcFiles[i].getName());
					System.out.println("go to dir:" + str.toString());
					File dst = new File(str.toString());
					CopyAFileOrDirectory(listOfSrcFiles[i], dst);
				}
			}
			else  //is file
			{
				int fileFound = 0;
				for (int j = 0; j < listOfDstFiles.length; j++) {
					if(listOfDstFiles[j].isFile()){
						long bytes_i = listOfSrcFiles[i].length();
						long bytes_j = listOfDstFiles[j].length();
						
/*						Path path1 = listOfSrcFiles[i].toPath();
						BasicFileAttributes attributes1 = Files.readAttributes(path1, BasicFileAttributes.class);
						FileTime creationTime1 = attributes1.creationTime();
						System.out.println(creationTime1.toString());
						Path path2 = listOfDstFiles[j].toPath();
						BasicFileAttributes attributes2 = Files.readAttributes(path2, BasicFileAttributes.class);
						FileTime creationTime2 = attributes2.creationTime();
						System.out.println(creationTime2.toString());
						
						System.out.format("%d, %d\n", bytes1, bytes2);
						System.out.println(listOfSrcFiles[i].getName().toString());
						System.out.println(listOfDstFiles[j].getName().toString());
						System.out.format("%d, %d\n", listOfSrcFiles[i].lastModified(), listOfDstFiles[j].lastModified());*/

						// compare name, mod date and bytes if both are files
						if(listOfSrcFiles[i].getName().toString().equals(listOfDstFiles[j].getName().toString()) &&
								listOfSrcFiles[i].lastModified() == listOfDstFiles[j].lastModified() &&
								bytes_i == bytes_j){
							fileFound = 1;
							//System.out.println("found 2");
						}
					}
				}
				if (fileFound == 0){
					StringBuilder str = new StringBuilder();
					str.append(targetDir.getPath());
					str.append("\\");
					str.append(listOfSrcFiles[i].getName());
					System.out.println("copy file:"+str.toString());
					File dst = new File(str.toString());
					copyAFileOrDirectory(listOfSrcFiles[i], dst);
				}
				
			}
		}
		
		//*******************************************************************
		// Use Dst as base run first round
		for (int i = 0; i < listOfDstFiles.length; i++) {
			if (listOfDstFiles[i].isDirectory()) {
				int dirFound = 0;
				for (int j = 0; j < listOfSrcFiles.length; j++) {
					if(listOfSrcFiles[j].isDirectory()){
						// only compare name difference if both are directory
						if(listOfDstFiles[i].getName().toString().equals(listOfSrcFiles[j].getName().toString())){
							dirFound = 1;
							//System.out.println("found 3");
						}		
					}
				}
				if (dirFound == 0){
					RemoveAFileOrDirectory(listOfDstFiles[i]);
					System.out.println("remove fir: "+ listOfDstFiles[i].toPath());
				}
			}
			else{  // is file
				int fileFound = 0;
				for (int j = 0; j < listOfSrcFiles.length; j++) {
					if(listOfSrcFiles[j].isFile()){
						long bytes_i = listOfDstFiles[i].length();
						long bytes_j = listOfSrcFiles[j].length();
						// compare name, mod date and bytes if both are files
						if(listOfDstFiles[i].getName().toString().equals(listOfSrcFiles[j].getName().toString()) &&
								listOfDstFiles[i].lastModified() == listOfSrcFiles[j].lastModified() &&
								bytes_i == bytes_j){
							fileFound = 1;
							//System.out.println("found 4");
						}		
					}
				}
				if (fileFound == 0){
					RemoveAFileOrDirectory(listOfDstFiles[i]);
					System.out.println("remove file:"+ listOfDstFiles[i].toPath());
				}
			}
		}
    }

	public static void main(String[] args) throws InterruptedException, IOException {

		String srcDir = "D:\\MyBook";
		String dstDir = "G:\\MyBookBackup";
		File src = new File(srcDir);
		File dst = new File(dstDir);



		while (true) {

			Date date = new Date();
			System.out.println(date.toString());
			if(dst.exists())
			{
				long size, size_free;			
				size = dst.getTotalSpace();
				size_free = dst.getFreeSpace();
				System.out.format("free %dGB ; total %dGB ; %f%% free\n",
						size_free / 1000000000, size / 1000000000, 100
								* (float) size_free / (float) size);
	
				//File test = new File(testDir);
						
				if (src.exists() && src.exists()) {
					System.out.println("starting bacup process");
					BackupDirectory(src, dst);
					System.out.println("done, wait for 24h");
					Thread.sleep(1000*3600*24);
					break;
				}
			}
			Thread.sleep(1000*5);
		}
	}
}