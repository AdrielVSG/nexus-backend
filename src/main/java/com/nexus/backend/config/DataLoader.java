package com.nexus.backend.config;

import com.nexus.backend.entity.Category;
import com.nexus.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            categoryRepository.saveAll(List.of(
                Category.builder().name("Entretenimento").description("Streaming de vídeo e música").build(),
                Category.builder().name("Jogos").description("Consoles e PC").build(),
                Category.builder().name("Produtividade").description("Ferramentas e Softwares").build(),
                Category.builder().name("Armazenamento").description("Nuvem e Backup").build(),
                Category.builder().name("Educação").description("Cursos e Faculdades").build(),
                Category.builder().name("Outros").description("Diversos").build()
            ));
        }
    }
}
