package edu.csula.rubrics.models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "artifacts")
public class Artifact implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String name;

	//type: e.g., submission
	@Column(nullable = false)
	private String type;

	//the endpoint to retrieve the artifact (e.g., the endpoint of Canvas API to get submission?)
	//e.g., url:GET|/api/v1/courses/:course_id/assignments/:assignment_id/submissions/:user_id
	@Column(nullable = false)
	private String endpoint;

	//this artifact should be under some certain association
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "association_id")
	private Association association;
	
    //there will be one more many assessor assessing this artifact
    @OneToMany(mappedBy = "artifact",
            cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    private List<Assessment> assessments;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public Association getAssociation() {
		return association;
	}

	public void setAssociation(Association association) {
		this.association = association;
	}

	public List<Assessment> getAssessments() {
		return assessments;
	}

	public void setAssessments(List<Assessment> assessments) {
		this.assessments = assessments;
	}
	
}
