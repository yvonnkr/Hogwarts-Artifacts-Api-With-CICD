package com.yvolabs.hogwartsartifactsapi.wizard;

import com.yvolabs.hogwartsartifactsapi.artifact.Artifact;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yvonne N
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wizard implements Serializable {
    @Id
    private Integer id;

    private String name;

    @OneToMany(mappedBy = "owner", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Artifact> artifacts = new ArrayList<>();

    public void addArtifact(Artifact artifact) {
        artifact.setOwner(this);
        this.artifacts.add(artifact);
    }

    public Integer numberOfArtifacts() {
        return this.artifacts.size();
    }

}
