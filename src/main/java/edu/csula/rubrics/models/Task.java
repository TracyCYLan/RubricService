package edu.csula.rubrics.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tasks")
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Type {
        INSTRUCTOR, PEER, EXTERNAL
    };

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;
    
    @Column(nullable = false)
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "assessor_id")
    private User assessor;
    
    @ManyToOne
    @JoinColumn(name ="rubric_id")
    private Rubric rubric;
    
    @Column(name = "due_date")
    private Calendar dueDate;
    
    @JsonIgnore
    @OneToMany(mappedBy="task")
    private List<Assessment> assessments;
    
    public Task()
    {
    	dueDate = Calendar.getInstance();
        dueDate.add( Calendar.DATE, 7 );
        dueDate.set( Calendar.HOUR_OF_DAY, 23 );
        dueDate.set( Calendar.MINUTE, 59 );
        dueDate.set( Calendar.SECOND, 59 );
        dueDate.set( Calendar.MILLISECOND, 0 );
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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public User getAssessor() {
		return assessor;
	}

	public void setAssessor(User assessor) {
		this.assessor = assessor;
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

	public Calendar getDueDate()
    {
        return dueDate;
    }

    public void setDueDate( Calendar dueDate )
    {
        this.dueDate = dueDate;
    }
    
}
