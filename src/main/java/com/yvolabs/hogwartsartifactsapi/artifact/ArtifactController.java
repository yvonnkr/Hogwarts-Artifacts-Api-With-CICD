package com.yvolabs.hogwartsartifactsapi.artifact;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yvolabs.hogwartsartifactsapi.artifact.converter.ArtifactDtoToArtifactConverter;
import com.yvolabs.hogwartsartifactsapi.artifact.converter.ArtifactToArtifactDtoConverter;
import com.yvolabs.hogwartsartifactsapi.artifact.dto.ArtifactDto;
import com.yvolabs.hogwartsartifactsapi.system.Result;
import com.yvolabs.hogwartsartifactsapi.system.StatusCode;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author Yvonne N
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.endpoint.base-url}/artifacts")
public class ArtifactController {
    private final ArtifactService artifactService;
    private final ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter;
    private final ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter;
    private final MeterRegistry meterRegistry;

    @GetMapping("/{artifactId}")
    public ResponseEntity<Result> findArtifactById(@PathVariable String artifactId) {
        Artifact foundArtifact = artifactService.findById(artifactId);

        // custom metric example
        meterRegistry.counter("artifacts.id." + artifactId).increment();

        ArtifactDto artifactDto = artifactToArtifactDtoConverter.convert(foundArtifact);
        Result result = Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Find One Success")
                .data(artifactDto)
                .build();

        return ResponseEntity.ok(result);
    }

    // pagination - artifacts?page=2&size=2&sort=name,desc
    @GetMapping
    public ResponseEntity<Result> findAllArtifacts(Pageable pageable) {
        // find all returns a page of artifacts
        Page<Artifact> artifactPage = artifactService.findAll(pageable);

        // convert artifactPage to a page of artifactDtos
        Page<ArtifactDto> artifactDtoPage = artifactPage
                .map(artifactToArtifactDtoConverter::convert);

        Result result = Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Find All Success")
                .data(artifactDtoPage)
                .build();

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Result> addArtifact(@Valid @RequestBody ArtifactDto artifactDto) {
        Artifact newArtifact = artifactDtoToArtifactConverter.convert(artifactDto);
        Artifact savedArtifact = artifactService.save(newArtifact);
        ArtifactDto savedArtifactDto = artifactToArtifactDtoConverter.convert(savedArtifact);
        Result result = Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Add Success")
                .data(savedArtifactDto).build();

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{artifactId}")
    public ResponseEntity<Result> updateArtifact(@PathVariable String artifactId, @Valid @RequestBody ArtifactDto artifactDto) {
        Artifact update = artifactDtoToArtifactConverter.convert(artifactDto);
        Artifact updatedArtifact = artifactService.update(artifactId, update);
        ArtifactDto updatedArtifactDto = artifactToArtifactDtoConverter.convert(updatedArtifact);
        Result result = Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Update Success")
                .data(updatedArtifactDto).build();
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{artifactId}")
    public ResponseEntity<Result> deleteArtifact(@PathVariable String artifactId) {
        artifactService.delete(artifactId);
        return ResponseEntity.ok(Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Delete Success")
                .data(null)
                .build());

    }

    @GetMapping("/summary")
    // Use with Caution: This endpoint when successfully called will incur a charge from open-ai
    public Result summarizeArtifacts() throws JsonProcessingException {
        List<Artifact> foundArtifacts = artifactService.findAll();
        List<ArtifactDto> artifactDtos = foundArtifacts.stream()
                .map(artifactToArtifactDtoConverter::convert)
                .toList();

        String artifactSummary = artifactService.summarize(artifactDtos);

        return Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Summarize Success")
                .data(artifactSummary)
                .build();
    }

    // Find artifacts by search criteria, Return a page of artifacts that match the search criteria.
    @PostMapping("/search")
    public Result findArtifactsByCriteria(@RequestBody Map<String, String> searchCriteria, Pageable pageable) {

        Page<Artifact> artifactPage = artifactService.findByCriteria(searchCriteria, pageable);

        Page<ArtifactDto> artifactDtoPage = artifactPage
                .map(artifactToArtifactDtoConverter::convert);

        return Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Search Success")
                .data(artifactDtoPage)
                .build();

    }
}
