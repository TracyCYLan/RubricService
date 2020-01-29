package edu.csula.rubricPrj.models;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "rubrics")
public class Rubric implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    /* Each rubric has a number of criterion. 
     * Each criteria can be reused by different rubrics.
     * */
    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(
    	    name = "rubrics_criterion_matching",
    	    joinColumns=@JoinColumn(name = "rubric_id"),
    	    inverseJoinColumns=@JoinColumn(name="criteria_id")
    	)
    private List<Criteria> criterion;


    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(name = "publish_date")
    private Calendar publishDate;

    @Column(name = "public", nullable = false)
    private boolean isPublic;

    private boolean obsolete;

    private boolean deleted;

    public Rubric()
    {
        isPublic = false;
        deleted = false;
        criterion = new ArrayList<Criteria>();
    }

    public Rubric clone()
    {
        Rubric newRubric = new Rubric();
        newRubric.name = "Copy of " + name;
        newRubric.description = description;

        for( Criteria criteria : criterion )
            newRubric.criterion.add( criteria.clone() );

        return newRubric;
    }

    public boolean isPublished()
    {
        return publishDate != null
            && Calendar.getInstance().after( publishDate );
    }

    public Long getId()
    {
        return id;
    }

    public void setId( Long id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public List<Criteria> getCriterion() {
		return criterion;
	}

	public void setCriterion(List<Criteria> criterion) {
		this.criterion = criterion;
	}

	public User getCreator()
    {
        return creator;
    }

    public void setCreator( User creator )
    {
        this.creator = creator;
    }

    public Calendar getPublishDate()
    {
        return publishDate;
    }

    public void setPublishDate( Calendar publishDate )
    {
        this.publishDate = publishDate;
    }

    public boolean isPublic()
    {
        return isPublic;
    }

    public void setPublic( boolean isPublic )
    {
        this.isPublic = isPublic;
    }

    public boolean isObsolete()
    {
        return obsolete;
    }

    public void setObsolete( boolean obsolete )
    {
        this.obsolete = obsolete;
    }

    public boolean isDeleted()
    {
        return deleted;
    }

    public void setDeleted( boolean deleted )
    {
        this.deleted = deleted;
    }

}
