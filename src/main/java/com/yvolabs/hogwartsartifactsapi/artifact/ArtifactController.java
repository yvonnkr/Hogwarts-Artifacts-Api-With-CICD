package com.yvolabs.hogwartsartifactsapi.artifact;

import com.yvolabs.hogwartsartifactsapi.artifact.converter.ArtifactDtoToArtifactConverter;
import com.yvolabs.hogwartsartifactsapi.artifact.converter.ArtifactToArtifactDtoConverter;
import com.yvolabs.hogwartsartifactsapi.artifact.dto.ArtifactDto;
import com.yvolabs.hogwartsartifactsapi.system.Result;
import com.yvolabs.hogwartsartifactsapi.system.StatusCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Yvonne N
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/artifacts")
public class ArtifactController {
    private final ArtifactService artifactService;
    private final ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter;
    private final ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter;

    @GetMapping("/{artifactId}")
    public ResponseEntity<Result> findArtifactById(@PathVariable String artifactId) {
        ArtifactDto artifactDto = artifactToArtifactDtoConverter.convert(artifactService.findById(artifactId));
        Result result = Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Find One Success")
                .data(artifactDto)
                .build();

        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Result> findAllArtifacts() {
        List<Artifact> artifacts = artifactService.findAll();
        List<ArtifactDto> artifactDtos = artifacts.stream()
                .map(artifactToArtifactDtoConverter::convert).toList();
        Result result = Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Find All Success")
                .data(artifactDtos)
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
}
