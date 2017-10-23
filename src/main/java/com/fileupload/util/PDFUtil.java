package com.fileupload.util;

import com.config.Config;
import com.util.FileHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public class PDFUtil implements Serializable {
	private static final long serialVersionUID = -5216719170234357945L;
	protected static final String FAX_NAS_BACKUP_FOLDER_KEY = "fax.nas.backup.folder";

	public void pdfMerger(List<File> listOfPdfFiles, File outputFile) {
		PDFMergerUtility mergeUtil = new PDFMergerUtility();
		mergeUtil.setDestinationFileName(outputFile.getAbsolutePath());
		listOfPdfFiles.forEach(f -> addFileToMerge(mergeUtil, f));
		try {
			mergeUtil.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
		} catch (IOException e) {
			log.error("Failed to merge pdf files", e);
		}
	}

	private void addFileToMerge(PDFMergerUtility pdfMerger, File f) {
		try {
			pdfMerger.addSource(f);
		} catch (FileNotFoundException e) {
			log.error("Problems merging file.", e);
		}
	}

	public long scissor(File source, String nameSuffix) {
		try (PDDocument doc = PDDocument.load(source)) {
			return savePages(source, new Splitter().split(doc), nameSuffix);
		} catch (IOException e) {
			log.error("Failed to scissor the fax file {}", source, e);
			cleanup(source);
		}
		return -1L;
	}

	public int getNumberOfPages(File source) {
		try (PDDocument doc = PDDocument.load(source)) {
			return doc.getNumberOfPages();
		} catch (IOException e) {
			log.error("Failed to get number of pages for fax file {}", source, e);
		}
		return -1;
	}

	public int savePages(File source, File destination, int startPage, int endPage) throws IOException {
		PDDocument partialDocument = new PDDocument();
		try (PDDocument sourceDoc = PDDocument.load(source)) {

			int pagesSaved = 0;
			for (int i = startPage - 1; i < endPage; i++) {
				partialDocument.addPage(sourceDoc.getPage(i));
				pagesSaved++;
			}
			partialDocument.save(destination);
			return pagesSaved;
		} catch (Exception e) {
			log.error("Failed to process {}", source, e);
			return 0;
		} finally {
			partialDocument.close();
		}
	}

	private long savePages(File source, List<PDDocument> pages, String nameSuffix) throws IOException {
		long result = IntStream.range(0, pages.size())
				.mapToObj(i -> savePdfPage(pages.get(i), i + 1, source, nameSuffix)).filter(b -> b).count();
		if (result < pages.size()) {
			throw new IOException("Failed to save all pages.");
		}
		return result;
	}

	private void cleanup(File source) {
		if (source == null || !source.isFile()) {
			return;
		}
		log.error("Cleaning up...");
		String baseName = FileHelper.getBaseName(source);
		FileHelper.getFileList(source.getParentFile()).stream()
				.filter(f -> f.getName().startsWith(baseName) && !baseName.equals(FileHelper.getBaseName(f)))
				.forEach(f -> f.delete());
	}

	private boolean savePdfPage(PDDocument page, int counter, File parent, String nameSuffix) {
		try {
			File pdfFile = getPdfPageFileName(parent, counter, nameSuffix);
			page.save(pdfFile);
			FileHelper.copyFileToDirectory(pdfFile, new File(Config.getProperty(FAX_NAS_BACKUP_FOLDER_KEY)));
			return true;
		} catch (IOException e) {
			log.error("Failed to save page #{} from fax file {}", counter, parent.getAbsolutePath(), e);
		}
		return false;
	}

	private File getPdfPageFileName(File parent, int counter, String nameSuffix) {
		return new File(parent.getParentFile(), FileHelper.getBaseName(parent) + nameSuffix
				+ String.format("%03d", counter) + FileHelper.getFileExtension(parent));
	}
}
