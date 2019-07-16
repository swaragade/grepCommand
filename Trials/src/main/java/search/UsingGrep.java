package search;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.unix4j.Unix4j;
import org.unix4j.line.Line;

public class UsingGrep {
	static int folderCount = 0;
	static int fileCount = 0;

	public static void createIndexFromDirectory(final File inputfolder, String outputDirectory) throws IOException {

		if (null != inputfolder && null != outputDirectory && inputfolder.listFiles() != null
				&& inputfolder.listFiles().length > 0) {
			for (final File fileEntry : inputfolder.listFiles()) {
				if (fileEntry.isDirectory()) {
					createIndexFromDirectory(fileEntry, outputDirectory);
					folderCount++;
				} else {
					String inputFilePath = fileEntry.getCanonicalFile().toString();
					if (!fileEntry.isHidden() && (inputFilePath.endsWith(".java") || inputFilePath.endsWith(".xml")
							|| inputFilePath.endsWith(".wsdl") || inputFilePath.endsWith(".xsd")
							|| inputFilePath.endsWith(".properties"))) {
						fileCount++;
						File file = new File(inputFilePath);
						List<Line> lines = Unix4j.grep("public", file).toLineList();
						// System.out.println(inputFilePath + " count is " + lines.size());
						if (lines.size() > 0) {
							System.out.println("FOUND : " + inputFilePath + " count is " + lines.size());
						}

					}
				}
			}
		}

	}

	public static void main(String[] args) throws IOException {
		long start = Calendar.getInstance().getTimeInMillis();
		final File inputfolder = new File("E:\\");
		// final File inputfolder = new
		// File("E:\\Lucene\\sample_directory_for_testing\\");
		String outputDirectory = "E:\\Lucene\\temp_test_index\\";

		createIndexFromDirectory(inputfolder, outputDirectory);
		long end = Calendar.getInstance().getTimeInMillis();
		System.out.println("Folders :" + folderCount + " \nFiles : " + fileCount);
		System.out.println("Time : " + (end - start) + " ms");
	}

}
