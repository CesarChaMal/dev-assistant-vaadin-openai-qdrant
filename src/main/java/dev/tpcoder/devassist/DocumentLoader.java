package dev.tpcoder.devassist;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class DocumentLoader {

    private final VectorStore vectorStore;
    private final Resource pdfResource;
    private final Logger log;

    public DocumentLoader(VectorStore vectorStore,
                          @Value("classpath:/spring-boot-reference.pdf") Resource pdfResource) {
        this.vectorStore = vectorStore;
        this.pdfResource = pdfResource;
        this.log = LoggerFactory.getLogger(DocumentLoader.class);
    }

    @PostConstruct
    public void init() {
        // Check availability of the document in the Vector Store
        // To reduce the cost when initialize if data already exists
        var count = vectorStore.similaritySearch("Spring Boot").size();
        log.info("Vector Store has {} documents", count);
        if (count > 0) {
            log.info("Application is ready");
            return;
        }
        log.info("Loading Spring Boot Reference PDF into Vector Store");
        var config = PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder().withNumberOfBottomTextLinesToDelete(0)
                        .withNumberOfTopPagesToSkipBeforeDelete(0)
                        .build())
                .withPagesPerDocument(1)
                .build();
        var pdfReader = new PagePdfDocumentReader(pdfResource, config);
        var textSplitter = new TokenTextSplitter();
        var splitterResult = textSplitter.apply(pdfReader.get());
        vectorStore.accept(splitterResult);
        log.info("Application is ready");
    }
}
