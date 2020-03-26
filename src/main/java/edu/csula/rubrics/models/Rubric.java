package edu.csula.rubrics.models;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import javax.persistence.OrderColumn;
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

    /* 
     * Each rubric has a number of criteria. 
     * Each criterion can be reused by different rubrics.
     * */
    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(
    	    name = "rubric_criteria",
    	    joinColumns=@JoinColumn(name = "rubric_id"),
    	    inverseJoinColumns=@JoinColumn(name="criterion_id")
    	)
    @OrderColumn(name = "criterion_order")
    private List<Criterion> criteria;


    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(name = "publish_date")
    private Calendar publishDate;
    
	@Column(name = "last_updated_date")
	private Date lastUpdatedDate;

    @Column(name = "public", nullable = false)
    private boolean isPublic;

    private boolean obsolete;

    private boolean deleted;

    public Rubric()
    {
		lastUpdatedDate = new Date();
        isPublic = false;
        deleted = false;
        criteria = new ArrayList<Criterion>();
    }

    public Rubric clone()
    {
        Rubric newRubric = new Rubric();
        newRubric.name = "Copy of " + name;
        newRubric.description = description;

        for( Criterion criterion : criteria )
            newRubric.criteria.add( criterion.clone() );

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

	public List<Criterion> getCriteria() {
		return criteria;
	}

	public void setCriteria(List<Criterion> criteria) {
		this.criteria = criteria;
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
    
	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
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
