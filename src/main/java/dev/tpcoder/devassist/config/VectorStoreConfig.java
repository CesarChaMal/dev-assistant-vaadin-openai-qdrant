package dev.tpcoder.devassist.config;

import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.context.annotation.Bean;

public class VectorStoreConfig {

    @Bean
    public VectorStore vectorStore(QdrantVectorStore.QdrantVectorStoreConfig config, EmbeddingClient embeddingClient) {
        return new QdrantVectorStore(config, embeddingClient);
    }
}
