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
    
    @ManyToOne
    @JoinColumn(name = "artifact_id")
    private Artifact artifact;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "association_id")
    private Association association;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
    
    @ManyToMany
    @JoinTable(name = "assessment_ratings",
    joinColumns = @JoinColumn(name = "assessment_id"),
    inverseJoinColumns = @JoinColumn(name = "rating_id"))
    private List<Rating> ratings;

    private String comments;

    private Date date;

    private boolean completed;

    private boolean deleted;
    

    public Assessment()
    {
        ratings = new ArrayList<Rating>();
        completed = false;
        deleted = false;
    }

    public Assessment( User assessor )
    {
        this();
        this.assessor = assessor;
    }

    public Double getOverallRating()
    {
        if( !completed ) return null;

        Double overallRating = 0.0;
        for( Rating rating : ratings )
            overallRating += rating.getValue();
        overallRating /= ratings.size();

        return overallRating;
    }

    public void setCompleted()
    {
        if( completed ) return;
        for( Rating rating : ratings )
            if( rating.getValue()<0 ) return;

        completed = true;

    }

    public Long getId()
    {
        return id;
    }

    public void setId( Long id )
    {
        this.id = id;
    }

	public User getAssessor() {
		return assessor;
	}

	public void setAssessor(User assessor) {
		this.assessor = assessor;
	}

	public List<Rating> getRatings()
    {
        return ratings;
    }

    public void setRatings( List<Rating> ratings )
    {
        this.ratings = ratings;
    }

    public String getComments()
    {
        return comments;
    }

    public void setComments( String comments )
    {
        this.comments = comments;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate( Date date )
    {
        this.date = date;
    }

    public boolean isCompleted()
    {
        return completed;
    }

    public void setCompleted( boolean completed )
    {
        this.completed = completed;
    }

    public boolean isDeleted()
    {
        return deleted;
    }

    public void setDeleted( boolean deleted )
    {
        this.deleted = deleted;
    }

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Artifact getArtifact() {
		return artifact;
	}

	public void setArtifact(Artifact artifact) {
		this.artifact = artifact;
	}

	public Association getAssociation() {
		return association;
	}

	public void setAssociation(Association association) {
		this.association = association;
	}

}
