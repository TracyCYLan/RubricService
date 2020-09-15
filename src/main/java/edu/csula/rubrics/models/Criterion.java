package edu.csula.rubrics.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "criteria")
public class Criterion implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String description;
	
    //One Outcome has multiple external Ids and its corresponding source
    //For each externalSourceId, we store external source name AND object ID in the external source
    //e.g., Canvas1001
    @JsonIgnore
    @OneToMany(mappedBy = "criterion")
    private List<External> externals;

	private boolean deleted;

	private boolean reusable;

	/* Each criterion has a number of ratings. */
	@OneToMany(mappedBy = "criterion")
//	@OrderBy("value desc")
	private List<Rating> ratings;

	@ManyToMany
	@JoinTable(name = "criterion_tags", joinColumns = @JoinColumn(name = "criterion_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private List<Tag> tags;
	
	@Column(name = "publish_date")
	private Calendar publishDate;
	
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;
    
	public Criterion() {
		ratings = new ArrayList<Rating>();
		tags = new ArrayList<Tag>();
		externals = new ArrayList<External>();
	}

	public Criterion clone() {
		Criterion newCriterion = new Criterion();
		newCriterion.description = description;
		for (Rating rating : ratings)
			newCriterion.ratings.add(rating.clone());

		return newCriterion;
	}

	public boolean isPublished() {
		return publishDate != null && Calendar.getInstance().after(publishDate);
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

	public List<External> getExternals() {
		return externals;
	}

	public void setExternals(List<External> externals) {
		this.externals = externals;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isReusable() {
		return reusable;
	}

	public void setReusable(boolean reusable) {
		this.reusable = reusable;
	}

	public List<Rating> getRatings() {
		return ratings;
	}

	public void setRatings(List<Rating> ratings) {
		this.ratings = ratings;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public Calendar getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Calendar publishDate) {
		this.publishDate = publishDate;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

}
