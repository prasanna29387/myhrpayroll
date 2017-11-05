package com.itext7;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.DottedLine;

public class CustomDashedLineSeparator extends DottedLine {
    protected float dash = 5;
    protected float phase = 2.5f;

    public float getDash() {
        return dash;
    }

    public float getPhase() {
        return phase;
    }

    public void setDash(float dash) {
        this.dash = dash;
    }

    public void setPhase(float phase) {
        this.phase = phase;
    }

    @Override
    public void draw(PdfCanvas canvas, Rectangle drawArea) {
        canvas.saveState()
                .setLineWidth(getLineWidth())
                .setLineDash(dash, gap, phase)
                .moveTo(drawArea.getX(), drawArea.getY())
                .lineTo(drawArea.getX() + drawArea.getWidth(), drawArea.getY())
                .stroke()
                .restoreState();
    }
}


