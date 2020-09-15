package edu.csula.rubrics.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
@Table(name = "assessment_group")
public class AssessmentGroup {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	//e.g., CS4540 HW1
	private String name;
	
	private String description;
	
    @Column(name = "assess_date")
    private Calendar assessDate;
    
    @OneToMany(mappedBy = "assessmentGroup")
    private List<Assessment> assessments;
    
    @ManyToOne
    @JoinColumn(name = "rubric_id")
    private Rubric rubric;
    
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator; //who imported this assessmentGroup

    public AssessmentGroup() {
    	assessments = new ArrayList<Assessment>();
    }

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Calendar getAssessDate() {
		return assessDate;
	}

	public void setAssessDate(Calendar assessDate) {
		this.assessDate = assessDate;
	}

	public Rubric getRubric() {
		return rubric;
	}

	public void setRubric(Rubric rubric) {
		this.rubric = rubric;
	}

	public List<Assessment> getAssessments() {
		return assessments;
	}

	public void setAssessments(List<Assessment> assessments) {
		this.assessments = assessments;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}
    
}
