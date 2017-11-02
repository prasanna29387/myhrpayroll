package com.pdf;

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
			log.warn("File not found. Merge continues.", e);
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
			page.save(getPdfPageFileName(parent, counter, nameSuffix));
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
