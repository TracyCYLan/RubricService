package edu.csula.rubricPrj.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

@Entity
@Table(name = "rubric_criterion")
public class Criteria implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String description;

    /* Each criteria has a number of ratings. */
    @OneToMany(mappedBy="criteria")
    private List<Rating> ratings;

    public Criteria()
    {
    	ratings = new ArrayList<Rating>();
    }

    public Criteria clone()
    {
    	Criteria newCriteria = new Criteria();
        newCriteria.description = description;
        for( Rating rating : ratings )
            newCriteria.ratings.add( rating.clone() );

        return newCriteria;
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
