package com.model;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.*;
import java.io.Closeable;
import java.io.IOException;

public class ProxyStream implements Closeable {
	@Getter(AccessLevel.PROTECTED)
	private final PDPageContentStream stream;

	public ProxyStream(PDDocument document, PDPage newPage) throws IOException {
		stream = new PDPageContentStream(document, newPage);
	}

	@Override
	public void close() throws IOException {
		stream.close();
	}

	public void beginText() throws IOException {
		stream.beginText();
	}

	public void setFont(PDFont font, float fontSize) throws IOException {
		stream.setFont(font, fontSize);
	}

	public void newLineAtOffset(float tx, float ty) throws IOException {
		stream.newLineAtOffset(tx, ty);
	}

	public void setNonStrokingColor(Color color) throws IOException {
		stream.setNonStrokingColor(color);
	}

	public void endText() throws IOException {
		stream.endText();
	}

	public void showText(String text) throws IOException {
		stream.showText(text);
	}
}
