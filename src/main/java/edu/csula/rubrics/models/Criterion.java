package edu.csula.rubrics.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "criteria")
public class Criterion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String description;

    /* Each criterion has a number of ratings. */
    @OneToMany(mappedBy="criterion")
    private List<Rating> ratings;

    public Criterion()
    {
    	ratings = new ArrayList<Rating>();
    }

    public Criterion clone()
    {
    	Criterion newCriterion = new Criterion();
        newCriterion.description = description;
        for( Rating rating : ratings )
            newCriterion.ratings.add( rating.clone() );

        return newCriterion;
    }

    public Long getId()
    {
        return id;
    }

    public void setId( Long id )
    {
        this.id = id;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

	public List<Rating> getRatings() {
		return ratings;
	}

	public void setRatings(List<Rating> ratings) {
		this.ratings = ratings;
	}

}
