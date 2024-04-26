package com.yvolabs.hogwartsartifactsapi.wizard.converter;

import com.yvolabs.hogwartsartifactsapi.wizard.Wizard;
import com.yvolabs.hogwartsartifactsapi.wizard.dto.WizardDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author Yvonne N
 */
@Component
public class WizardToWizardDtoConverter implements Converter<Wizard, WizardDto> {
    @Override
    public WizardDto convert(Wizard source) {
        return new WizardDto(
                source.getId(),
                source.getName(),
                source.numberOfArtifacts()
        );

    }
}
