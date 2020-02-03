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
@Table(name = "evaluations")
public class Evaluation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "evaluator_id")
    private User evaluator;
    
    @ManyToOne
    @JoinColumn(name = "evaluatee_id")
    private User evaluatee;
    
    @ManyToOne
    @JoinColumn(name = "rubric_id")
    private Rubric rubric;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
    
    @ManyToMany
    @JoinTable(name = "evaluation_ratings",
    joinColumns = @JoinColumn(name = "evaluation_id"),
    inverseJoinColumns = @JoinColumn(name = "rating_id"))
    private List<Rating> ratings;

    private String comments;

    private Date date;

    private boolean completed;

    private boolean deleted;
    

    public Evaluation()
    {
        ratings = new ArrayList<Rating>();
        completed = false;
        deleted = false;
    }

    public Evaluation( User evaluator )
    {
        this();
        this.evaluator = evaluator;

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

    public User getEvaluator()
    {
        return evaluator;
    }

    public void setEvaluator( User evaluator )
    {
        this.evaluator = evaluator;
    }

    public User getEvaluatee() {
		return evaluatee;
	}

	public void setEvaluatee(User evaluatee) {
		this.evaluatee = evaluatee;
	}

	public Rubric getRubric() {
		return rubric;
	}

	public void setRubric(Rubric rubric) {
		this.rubric = rubric;
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


}
