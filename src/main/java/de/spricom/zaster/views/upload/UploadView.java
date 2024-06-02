package de.spricom.zaster.views.upload;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.spricom.zaster.services.UploadService;
import de.spricom.zaster.views.MainLayout;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.AbstractResource;

import java.io.IOException;
import java.io.InputStream;

@PageTitle("Upload")
@Route(value = "upload", layout = MainLayout.class)
@Log4j2
public class UploadView extends Div {

    private final UploadService uploadService;

    private final MultiFileMemoryBuffer fileBuffer = new MultiFileMemoryBuffer();
    Upload upload = new Upload(fileBuffer);

    public UploadView(UploadService uploadService) {
        this.uploadService = uploadService;
        addClassNames("upload-view");

        add(upload);

        upload.addSucceededListener(this::handleUploadSucceeded);
    }

    private void handleUploadSucceeded(SucceededEvent event) {
        uploadService.importFile("Postbank CSV", new UploadResource(event));
    }

    class UploadResource extends AbstractResource {
        private final SucceededEvent event;

        public UploadResource(SucceededEvent event) {
            this.event = event;
        }

        @Override
        public String getDescription() {
            return getFilename() + " [" + event.getMIMEType() + "]";
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return fileBuffer.getInputStream(getFilename());
        }

        @Override
        public String getFilename() {
            return event.getFileName();
        }

        @Override
        public long contentLength() throws IOException {
            return event.getContentLength();
        }
    }
}
