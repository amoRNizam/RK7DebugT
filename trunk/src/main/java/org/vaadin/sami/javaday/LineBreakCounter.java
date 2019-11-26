package org.vaadin.sami.javaday;

import com.vaadin.ui.Upload;

import java.io.OutputStream;

public class LineBreakCounter implements Upload.Receiver {
    private int counter;
    private int total;
    private boolean sleep;

    /**
     * return an OutputStream that simply counts lineends
     */
    @Override
    public OutputStream receiveUpload(final String filename, final String MIMEType) {
        counter = 0;
        total = 0;
        return new OutputStream() {
            private static final int searchedByte = '\n';

            @Override
            public void write(final int b) {
                total++;
                if (b == searchedByte) {
                    counter++;
                }
                if (sleep && total % 1000 == 0) {
                    try {
                        Thread.sleep(100);
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    protected int getLineBreakCount() {
        return counter;
    }

    protected void setSlow(boolean value) {
        sleep = value;
    }
}