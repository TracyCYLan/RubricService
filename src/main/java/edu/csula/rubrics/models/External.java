package edu.csula.rubrics.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/*
 * A model records Rubric/Criterion Object's corresponding Id and source (e.g.,Canvas) 
 */
@Entity
@Table(name = "externals")
public class External {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;
    
    private String source;
    
    @Column(name = "external_id")
    private String externalId;
    
    //value = {'rubric', 'criterion'}
    private String type;
    
    @ManyToOne
    private Rubric rubric;
    
    @ManyToOne
    private Criterion criterion;
    
    public External() {
    	
    }

    public External(String source,String externalId, String type) {
    	this.source = source;
    	this.externalId = externalId;
    	this.type = type;
    }
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Rubric getRubric() {
		return rubric;
	}

	public void setRubric(Rubric rubric) {
		this.rubric = rubric;
	}

	public Criterion getCriterion() {
		return criterion;
	}

	public void setCriterion(Criterion criterion) {
		this.criterion = criterion;
	}
    
    
}
