package org.vaadin.sami.javaday;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.*;


@StyleSheet("uploadexample.css")
public class UploadInfoWindow extends Window implements
        Upload.StartedListener, Upload.ProgressListener,
        Upload.FailedListener, Upload.SucceededListener,
        Upload.FinishedListener {
    private final Label state = new Label();
    private final Label result = new Label();
    private final Label fileName = new Label();
    private final Label textualProgress = new Label();

    private final ProgressBar progressBar = new ProgressBar();
    private final Button cancelButton;
    private final LineBreakCounter counter;

    protected UploadInfoWindow(final Upload upload, final LineBreakCounter lineBreakCounter) {
        super("Status");
        this.counter = lineBreakCounter;

        addStyleName("upload-info");

        setResizable(false);
        setDraggable(false);

        final FormLayout uploadInfoLayout = new FormLayout();
        setContent(uploadInfoLayout);
        uploadInfoLayout.setMargin(true);

        final HorizontalLayout stateLayout = new HorizontalLayout();
        stateLayout.setSpacing(true);
        stateLayout.addComponent(state);

        cancelButton = new Button("Cancel");
        cancelButton.addClickListener(event -> upload.interruptUpload());
        cancelButton.setVisible(false);
        cancelButton.setStyleName("small");
        stateLayout.addComponent(cancelButton);

        stateLayout.setCaption("Current state");
        state.setValue("Idle");
        uploadInfoLayout.addComponent(stateLayout);

        fileName.setCaption("File name");
        uploadInfoLayout.addComponent(fileName);

        result.setCaption("Line breaks counted");
        uploadInfoLayout.addComponent(result);

        progressBar.setCaption("Progress");
        progressBar.setVisible(false);
        uploadInfoLayout.addComponent(progressBar);

        textualProgress.setVisible(false);
        uploadInfoLayout.addComponent(textualProgress);

        upload.addStartedListener(this);
        upload.addProgressListener(this);
        upload.addFailedListener(this);
        upload.addSucceededListener(this);
        upload.addFinishedListener(this);

    }

    @Override
    public void uploadFinished(final Upload.FinishedEvent event) {
        state.setValue("Idle");
        progressBar.setVisible(false);
        textualProgress.setVisible(false);
        cancelButton.setVisible(false);
    }

    @Override
    public void uploadStarted(final Upload.StartedEvent event) {
        // this method gets called immediately after upload is started
        progressBar.setValue(0f);
        progressBar.setVisible(true);
        UI.getCurrent().setPollInterval(500);
        textualProgress.setVisible(true);
        // updates to client
        state.setValue("Uploading");
        fileName.setValue(event.getFilename());

        cancelButton.setVisible(true);
    }

    @Override
    public void updateProgress(final long readBytes, final long contentLength) {
        // this method gets called several times during the update
        progressBar.setValue(readBytes / (float) contentLength);
        textualProgress.setValue("Processed " + readBytes + " bytes of " + contentLength);
        result.setValue(counter.getLineBreakCount() + " (counting...)");
    }

    @Override
    public void uploadSucceeded(final Upload.SucceededEvent event) {
        result.setValue(counter.getLineBreakCount() + " (total)");
    }

    @Override
    public void uploadFailed(final Upload.FailedEvent event) {
        result.setValue(counter.getLineBreakCount()
                + " (counting interrupted at "
                + Math.round(100 * progressBar.getValue()) + "%)");
    }
}
