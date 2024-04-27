package com.yvolabs.hogwartsartifactsapi.wizard;

import com.yvolabs.hogwartsartifactsapi.system.Result;
import com.yvolabs.hogwartsartifactsapi.system.StatusCode;
import com.yvolabs.hogwartsartifactsapi.wizard.converter.WizardDtoToWizardConvertor;
import com.yvolabs.hogwartsartifactsapi.wizard.converter.WizardToWizardDtoConverter;
import com.yvolabs.hogwartsartifactsapi.wizard.dto.WizardDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Yvonne N
 */
@RestController
@RequestMapping("${api.endpoint.base-url}/wizards")
@RequiredArgsConstructor
public class WizardController {
    private final WizardService wizardService;
    private final WizardToWizardDtoConverter wizardToWizardDtoConverter;
    private final WizardDtoToWizardConvertor wizardDtoToWizardConvertor;

    @GetMapping
    public ResponseEntity<Result> findAllWizards() {
        List<Wizard> wizards = wizardService.findAll();

        List<WizardDto> wizardDtos = wizards.stream()
                .map(wizardToWizardDtoConverter::convert)
                .toList();

        Result result = Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Find All Success")
                .data(wizardDtos)
                .build();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{wizardId}")
    public ResponseEntity<Result> findWizardById(@PathVariable Integer wizardId) {
        Wizard wizard = wizardService.findById(wizardId);
        WizardDto wizardDto = wizardToWizardDtoConverter.convert(wizard);

        Result result = Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Find One Success")
                .data(wizardDto)
                .build();

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Result> addWizard(@RequestBody @Valid WizardDto wizardDto) {
        Wizard wizard = wizardDtoToWizardConvertor.convert(wizardDto);
        Wizard savedWizard = wizardService.save(wizard);
        WizardDto wizardDtoSaved = wizardToWizardDtoConverter.convert(savedWizard);

        return ResponseEntity.ok(Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Add Success")
                .data(wizardDtoSaved)
                .build());

    }

    @PutMapping("/{wizardId}")
    public ResponseEntity<Result> updateWizard(@PathVariable Integer wizardId, @RequestBody @Valid WizardDto wizardDto) {
        Wizard update = wizardDtoToWizardConvertor.convert(wizardDto);
        Wizard updatedWizard = wizardService.update(wizardId, update);
        WizardDto wizardDtoUpdated = wizardToWizardDtoConverter.convert(updatedWizard);
        return ResponseEntity.ok(Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Update Success")
                .data(wizardDtoUpdated)
                .build());

    }

    @PutMapping("/{wizardId}/artifacts/{artifactId}")
    public ResponseEntity<Result> assignArtifact(@PathVariable Integer wizardId, @PathVariable String artifactId) {
        wizardService.assignArtifact(wizardId, artifactId);

        Result result = Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Artifact Assignment Success")
                .build();

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{wizardId}")
    public ResponseEntity<Result> deleteWizardById(@PathVariable Integer wizardId) {
        wizardService.delete(wizardId);

        return ResponseEntity.ok(Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Delete Success")
                .build());
    }

}
