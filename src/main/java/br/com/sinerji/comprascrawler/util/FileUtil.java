package br.com.sinerji.comprascrawler.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtil {
	
	public static void createOrReplaceDirectory(File directory) {
		if (directory.exists()) {
			if (!deleteDirectory(directory)) {
				throw new IllegalStateException("Cannot delete directory: " + directory.getAbsolutePath());
			}
		}
		
		createDirectoryIfNotExists(directory);
	}

	public static boolean deleteFile(File file) {
		if (!file.isFile()) {
			throw new IllegalArgumentException(file.getPath() + " is not a file");
		}

		return file.delete();
	}

	public static boolean deleteDirectory(File file) {
		for (File childFile : file.listFiles()) {
			if (childFile.isDirectory()) {
				deleteDirectory(childFile);
			} else {
				childFile.delete();
			}
		}
		
		return file.delete();
	}
	
	public static void createDirectoryIfNotExists(File directory) {
		if (!directory.exists()) {
			if (!directory.mkdir()) {
				throw new IllegalStateException("Cannot create directory: " + directory.getAbsolutePath());
			}
			log.info("Creating " + directory.getName() + " directory...");
		}
	}
	
	public static void createFileIfNotExists(File file) {
		if (!file.exists()) {
			createFile(file);
		}
	}

	
	public static void createFile(File file) {
		try {
			file.createNewFile();
		} catch (IOException e) {
			throw new IllegalStateException("Cannot create file: " + file.getAbsolutePath());
		}
	}
	
	public static List<String> readLinesAsList(File file) {
		return readLinesAsList(file.getAbsolutePath());
	}
	
	public static List<String> readLinesAsList(String filePath) {
		Path path = Paths.get(filePath);
		try {
			return Files.readAllLines(path, StandardCharsets.UTF_8);
		} catch (IOException e) {
			try {
				return Files.readAllLines(path, StandardCharsets.ISO_8859_1);
			} catch (IOException e1) {
				throw new IllegalStateException("Error to read file: " + filePath, e);
			}
		}
	}
	
	public static String readFile(File file) {
		return readFile(file.getAbsolutePath());
	}

	public static String readFile(String filePath) {
		Path path = Paths.get(filePath);
	    try (Stream<String> lines = Files.lines(path)) {
		    String data = lines.collect(Collectors.joining("\n"));
		    lines.close();
		    return data.trim();
	    } catch (IOException e) {
			throw new IllegalStateException("Error to read file: " + filePath, e);
		}
	}

	public static void writeToFile(File file, String str) throws IOException {
        Path path = Paths.get(file.getAbsolutePath());
        Files.write(path, str.getBytes());
	}
	
	public static void appendToFile(File file, String str) throws IOException {
        Path path = Paths.get(file.getAbsolutePath());
        Files.write(path, str.getBytes(), StandardOpenOption.APPEND);
	}

	public static String getFileNameWithoutExtension(File file) {
		String fileName = file.getName();
		int dotLastIdx = fileName.lastIndexOf(".");
		if (dotLastIdx == -1) {
			return fileName;
		}
		return fileName.substring(0, dotLastIdx);
	}
	
	public static List<File> listDirFilesOrdered(File dir) {
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException(dir.getName() + " in not a directory.");
		}
		File[] files = dir.listFiles();
		Arrays.sort(files, (f1, f2) -> f1.getName().compareTo(f2.getName()));
		List<File> fileList = List.of(files);
		return fileList;
	}
}
