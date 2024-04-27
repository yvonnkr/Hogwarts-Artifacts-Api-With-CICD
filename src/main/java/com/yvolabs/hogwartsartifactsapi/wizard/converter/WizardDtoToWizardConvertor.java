package com.yvolabs.hogwartsartifactsapi.wizard.converter;

import com.yvolabs.hogwartsartifactsapi.wizard.Wizard;
import com.yvolabs.hogwartsartifactsapi.wizard.dto.WizardDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author Yvonne N
 */
@Component
public class WizardDtoToWizardConvertor implements Converter<WizardDto, Wizard> {
    @Override
    public Wizard convert(WizardDto source) {
        return Wizard.builder()
                .id(source.id())
                .name(source.name())
                .build();
    }
}
