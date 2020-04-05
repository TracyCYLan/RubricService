package edu.csula.rubrics.models.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.csula.rubrics.models.Artifact;
import edu.csula.rubrics.models.dao.ArtifactDao;

@Repository
public class ArtifactDaoImpl implements ArtifactDao {
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Artifact getArtifact(Long id) {
		return entityManager.find(Artifact.class, id);
	}

	@Override
	@Transactional
	public Artifact saveArtifact(Artifact artifact) {
		return entityManager.merge(artifact);
	}

	@Override
	public List<Artifact> getAllArtifacts() {
		return entityManager.createQuery("from Artifact", Artifact.class).getResultList();
	}

}
