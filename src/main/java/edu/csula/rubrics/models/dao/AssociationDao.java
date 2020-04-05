package edu.csula.rubrics.models.dao;

import java.util.List;

import edu.csula.rubrics.models.Association;


public interface AssociationDao {
	
	Association getAssociation(Long id);
	
	Association saveAssociation(Association association);
	
	List<Association> getAllAssociations();
	
}
