import java.io.File;
import java.util.Date;

public class test {

	public static void removeDirectory(File dir) {
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			if (files != null && files.length > 0) {
				for (File aFile : files) {
					removeDirectory(aFile);
				}
			}
			dir.delete();
		} else {
			dir.delete();
		}
	}

	public static void findOldestDir(String path, StringBuilder fileName,
			StringBuilder filePath) {
		String file;
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isDirectory()) {
				filePath.append(listOfFiles[i].getPath());
				fileName.append(listOfFiles[i].getName());
				break;
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		System.out.println("Hello, World");
		String frontyard = "F:\\MotionCam\\Frontyard";
		String backyard = "F:\\MotionCam\\Backyard";

		long size, size_free;
		File f = new File(frontyard);
		while (true) {
			Thread.sleep(1000*3600*4);
			System.out.println("tick");
			if (f.exists()) {
				size = f.getTotalSpace();
				size_free = f.getFreeSpace();
				
				Date date = new Date();
			    System.out.println(date.toString());
				System.out.format("free %dGB ; total %dGB ; %f%% free\n", size_free/1000000000, size/1000000000, 100*(float)size_free/(float)size);
				if (size >= size_free * 5) {
					System.out.println("try to free some space");

					StringBuilder frontyardFileName = new StringBuilder();
					StringBuilder frontyardFilePath = new StringBuilder();
					findOldestDir(frontyard, frontyardFileName, frontyardFilePath);

					StringBuilder backyardFileName = new StringBuilder();
					StringBuilder backyardFilePath = new StringBuilder();
					findOldestDir(backyard, backyardFileName, backyardFilePath);

					int comparsionResult;
					if(backyardFileName.toString().isEmpty() && !frontyardFileName.toString().isEmpty())
						comparsionResult = -1;
					else if(!backyardFileName.toString().isEmpty() && frontyardFileName.toString().isEmpty())
						comparsionResult = 1;
					else if (backyardFileName.toString().isEmpty() && frontyardFileName.toString().isEmpty())
						continue;
					else 
						comparsionResult = frontyardFileName.toString().compareTo(backyardFileName.toString());

					if (comparsionResult == 0) //delete one for each
					{
						File dir_back = new File(backyardFilePath.toString());
						removeDirectory(dir_back);
						System.out.println("deleted "+backyardFilePath.toString());
						File dir_front = new File(frontyardFilePath.toString());
						removeDirectory(dir_front);
						System.out.println("deleted "+frontyardFilePath.toString());
					}

					else if (comparsionResult > 0) // backyard is older, delete backyard file
					{
						File dir = new File(backyardFilePath.toString());
						removeDirectory(dir);
						System.out.println("deleted "+backyardFilePath.toString());
					}

					else // frontyard is older, delete frontyard file
					{
						File dir = new File(frontyardFilePath.toString());
						removeDirectory(dir);
						System.out.println("deleted "+frontyardFilePath.toString());
					}

				}
			}
		}

	}
}