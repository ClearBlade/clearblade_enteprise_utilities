package com.baeldung.main;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Exec {

	public static Set<String> listFilesUsingFilesList(String dir) throws IOException {
		try (Stream<Path> stream = Files.list(Paths.get(dir))) {
			return stream.filter(file -> !Files.isDirectory(file)).map(Path::getFileName).map(Path::toString)
					.collect(Collectors.toSet());
		}
	}

	public static String getExtensionByApacheCommonLib(String filename) {
		return FilenameUtils.getExtension(filename);
	}

	public static void mainMethod(String fileName, Map<String, Integer> charCountMap)
			throws IOException, ParseException {

		System.out.println("FILE NAME >>>>>> " + fileName);
		JSONParser parser = new JSONParser();

		JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("./logfiles/" + fileName));

		if (jsonArray.size() > 0) {

			Iterator<JSONObject> iterator = jsonArray.iterator();
			while (iterator.hasNext()) {
				JSONObject json = (JSONObject) iterator.next();
				if (json.containsKey("jsonPayload")) {
					String message = ((JSONObject) json.get("jsonPayload")).get("message").toString();
					// System.out.println("jsonpayload value >> " + message);
					int indexsOfAS = message.indexOf("as");
					int indexsOfWith = message.indexOf("with");
					String Device = message.substring(indexsOfAS + 2, indexsOfWith).trim().replaceAll("\"", "");

					if (charCountMap.containsKey(Device)) {
						charCountMap.put(Device, charCountMap.get(Device) + 1);
					} else {
						charCountMap.put(Device, 1);
					}
				} else {
					System.out.println("json node does not have jsonPayload ");

				}

			}

		}

	}

	public static void main(String[] args) {
		JSONParser parser = new JSONParser();

		try {
			File directory = new File("outputFiles");
			if (!directory.exists()) {
				directory.mkdir();
			} else {
				System.out.println("directory hai");
			}
			Set<String> filesName = listFilesUsingFilesList("./logfiles");

			// ExecutorService executor = Executors.newFixedThreadPool(2);
			Map<String, Integer> charCountMap = new HashMap<>();
			for (String a : filesName) {

				System.out.println("file extension " + getExtensionByApacheCommonLib(a));
				if ("json".equals(getExtensionByApacheCommonLib(a))) {
					mainMethod(a, charCountMap);
				}

			}

			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("sheet1");

			int rowno = 1;

			/*
			 * File directory = new File("outputFiles"); if (! directory.exists()){
			 * directory.mkdir(); }else { System.out.println("di"); }
			 */
			LinkedHashMap<String, Integer> sorted = charCountMap.entrySet().stream()
					.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors
							.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

			XSSFRow row1 = sheet.createRow(0);
			row1.createCell(0).setCellValue((String) "Device");
			row1.createCell(1).setCellValue((String) "Count");

			for (HashMap.Entry entry : sorted.entrySet()) {
				XSSFRow row = sheet.createRow(rowno++);
				row.createCell(0).setCellValue((String) entry.getKey());
				row.createCell(1).setCellValue((Integer) entry.getValue());
			}
			String filename = "./outputFiles/outputfile" + createFileName();
			FileOutputStream file = new FileOutputStream(filename + ".xlsx");
			workbook.write(file);
			file.close();
			System.out.println("Successfully Data Copied to Excel in folder outputFiles ");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static final String DEFAULT_FILE_PATTERN = "yyyy-MM-dd-HH-mm-ss";

	public static String createFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat(DEFAULT_FILE_PATTERN);
		return format.format(date);
	}
}
