package search;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.unix4j.Unix4j;
import org.unix4j.line.Line;
import org.unix4j.unix.Grep;
import org.unix4j.unix.grep.GrepOptionSet_Fcilnvx;

public class GrepSearch {

	public static void main(String[] args) {
		String yourCVSdirectory = "E:\\Lucene\\sample_directory_for_testing";
		String outputDirectory = "E:\\Lucene";
		String searchSrting = "WiLL";
		// select from c,count , f, v, i , l, n etc
		// if not , then put null
		GrepOptionSet_Fcilnvx specialGrepAddOn = Grep.Options.ignoreCase;

		GrepSearchUtility.entry(yourCVSdirectory, outputDirectory, searchSrting, specialGrepAddOn);
	}

}

class GrepSearchUtility {
	private static int FOLDER_COUNT = 0;
	private static int FILE_COUNT = 0;

	public static void entry(String yourCVSdirectory, String outputDirectory, String searchSrting,
			GrepOptionSet_Fcilnvx specialGrepAddOn) {
		try {
			long start = Calendar.getInstance().getTimeInMillis();
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()).replaceAll("\\.", "-");
			final File inputfolder = new File(yourCVSdirectory);
			System.out.println("Started");
			String csvFile = outputDirectory + "\\search_result_" + timeStamp + ".csv";
			FileWriter csvWriter = new FileWriter(csvFile);
			csvWriter.append("Search result for '" + searchSrting + "'");
			csvWriter.append("\n");
			csvWriter.append("Path");
			csvWriter.append(",");
			csvWriter.append("Hits");
			csvWriter.append("\n");

			scanDirectory(inputfolder, searchSrting, csvWriter, specialGrepAddOn);
			csvWriter.close();

			long end = Calendar.getInstance().getTimeInMillis();
			System.out.println("Folders :" + FOLDER_COUNT + " \nFiles : " + FILE_COUNT);
			System.out.println("Time : " + ((double) (end - start)) / 1000 + " seconds");

			System.out.println("Attempting to open the result file..");
			String[] commands = { "cmd", "/c", csvFile };
			try {
				Runtime.getRuntime().exec(commands);
			} catch (Exception ex) {
				System.out.println("Failed to open the file, Please check : " + csvFile);
			}

			System.out.println("Done");
		} catch (IOException io) {
			System.out.println(io.getMessage());
		}
	}

	private static void scanDirectory(final File inputfolder, String searchSrting, FileWriter csvWriter,
			GrepOptionSet_Fcilnvx specialGrepAddOn) {

		if (null != inputfolder && inputfolder.listFiles() != null && inputfolder.listFiles().length > 0) {
			for (final File fileEntry : inputfolder.listFiles()) {
				if (fileEntry.isDirectory()) {
					scanDirectory(fileEntry, searchSrting, csvWriter, specialGrepAddOn);
					FOLDER_COUNT++;
					System.out.print(".");
				} else {
					FILE_COUNT++;

					grepSearch(searchSrting, csvWriter, fileEntry, specialGrepAddOn);
				}
			}
		}

	}

	private static void grepSearch(String searchSrting, FileWriter csvWriter, final File fileEntry,
			GrepOptionSet_Fcilnvx specialGrepAddOn) {
		String inputFilePath;
		try {
			inputFilePath = fileEntry.getCanonicalFile().toString();
			if (!fileEntry.isHidden() && (inputFilePath.endsWith(".java") || inputFilePath.endsWith(".xml")
					|| inputFilePath.endsWith(".wsdl") || inputFilePath.endsWith(".xsd")
					|| inputFilePath.endsWith(".properties") || inputFilePath.endsWith(".txt"))) {
				File file = new File(inputFilePath);
				List<Line> lines;
				if (specialGrepAddOn != null) {
					lines = Unix4j.grep(specialGrepAddOn, searchSrting, file).toLineList();
				} else {
					lines = Unix4j.grep(searchSrting, file).toLineList();
				} // System.out.println(inputFilePath + " count is " + lines.size());
				if (lines.size() > 0) {
					// System.out.println("Count is " + lines.size()+" : FOUND in : " +
					writeToCSV(inputFilePath, csvWriter, lines.size());
				}

			}
		} catch (IOException e) {
			System.out.println("Grep command failed to execute");
			e.printStackTrace();
		}
		
	}

	private static void writeToCSV(String inputFilePath, FileWriter csvWriter, int count) throws IOException {
		csvWriter.append(String.join(",", inputFilePath));
		csvWriter.append(",");
		csvWriter.append(String.valueOf(count));
		csvWriter.append("\n");
		csvWriter.flush();
	}
}
