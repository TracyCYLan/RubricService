package edu.csula.rubrics.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
	
    @Column(name = "publish_date")
    private Calendar publishDate;
    
    @OneToMany(mappedBy = "assessmentGroup")
    private List<Assessment> assessments;

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

	public Calendar getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Calendar publishDate) {
		this.publishDate = publishDate;
	}

	public List<Assessment> getAssessments() {
		return assessments;
	}

	public void setAssessments(List<Assessment> assessments) {
		this.assessments = assessments;
	}
    
    
}
