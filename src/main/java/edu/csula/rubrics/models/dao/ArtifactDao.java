package edu.csula.rubrics.models.dao;

import java.util.List;

import edu.csula.rubrics.models.Artifact;

public interface ArtifactDao {

	Artifact getArtifact(Long id);

	Artifact saveArtifact(Artifact artifact);

	List<Artifact> getAllArtifacts();
	
}
