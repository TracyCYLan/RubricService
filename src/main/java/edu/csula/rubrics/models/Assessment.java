package edu.csula.rubrics.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "assessments")
public class Assessment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assesor_id")
    private User assessor;
    
    //one assessment might have more than one file submitted.
    @OneToMany(mappedBy = "assessment")
    private List<Artifact> artifacts;
    
    //which rubric we are using to assess this assessment.
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "rubric_id")
    private Rubric rubric;
    
    //one assessment belongs to one group
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "assessment_group_id")
    private AssessmentGroup assessmentGroup;
    
//    @ManyToMany
//    @JoinTable(name = "assessment_ratings",
//    joinColumns = @JoinColumn(name = "assessment_id"),
//    inverseJoinColumns = @JoinColumn(name = "rating_id"))
//    private List<Rating> ratings;
    
    @OneToMany(mappedBy = "assessment")
    private List<Comment> comments;
    
    //assessment_type, e.g., peer_review, grading
    private String type;

    private Date date;

    private boolean deleted;
    
    public Assessment()
    {
//        ratings = new ArrayList<Rating>();
        deleted = false;
    }

    public Assessment( User assessor )
    {
        this();
        this.assessor = assessor;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getAssessor() {
		return assessor;
	}

	public void setAssessor(User assessor) {
		this.assessor = assessor;
	}

	public List<Artifact> getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(List<Artifact> artifacts) {
		this.artifacts = artifacts;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Rubric getRubric() {
		return rubric;
	}

	public void setRubric(Rubric rubric) {
		this.rubric = rubric;
	}

	public AssessmentGroup getAssessmentGroup() {
		return assessmentGroup;
	}

	public void setAssessmentGroup(AssessmentGroup assessmentGroup) {
		this.assessmentGroup = assessmentGroup;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
